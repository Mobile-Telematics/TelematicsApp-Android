package com.telematics.data.interceptor

import com.telematics.data.BuildConfig
import com.telematics.data.api.RefreshApi
import com.telematics.data.extentions.await
import com.telematics.data.mappers.toSessionData
import com.telematics.data.model.refresh_token.RefreshRequest
import com.telematics.domain.model.SessionData
import com.telematics.domain.repository.SessionRepo
import com.telematics.domain.repository.UserRepo
import com.telematicssdk.auth.TelematicsAuth
import com.telematicssdk.auth.errors.ApiException
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class MainInterceptor @Inject constructor(
    private val sessionRepository: SessionRepo,
    private val userRepository: UserRepo,
) : Interceptor, Authenticator {
    private val monitor = Any()
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        runBlocking {
            val accessToken = sessionRepository.getSession().accessToken
            request = request.newBuilder()
                .addHeader(AUTHORIZATION_HEADER_NAME, accessToken.buildAuthorizationHeader())
                .build()
        }
        return chain.proceed(request)
    }

    override fun authenticate(route: Route?, response: Response): Request? {

        if (sessionRepository.getSession().run {
                accessToken.isEmpty() || refreshToken.isEmpty()
            }) {
            return null
        }

        if (responseCount(response) >= MAX_AUTHENTICATE_TRIES) {
            return null // If we've failed 3 times, give up.
        }

        synchronized(monitor) {
            return runBlocking {
                val request = response.request
                val sessionData = sessionRepository.getSession()

                val accessToken = sessionData.accessToken

                if (response.request.header(AUTHORIZATION_HEADER_NAME) != accessToken.buildAuthorizationHeader()) {
                    request.newBuilder().removeHeader(AUTHORIZATION_HEADER_NAME)
                        .addHeader(
                            AUTHORIZATION_HEADER_NAME,
                            accessToken.buildAuthorizationHeader()
                        ).build()
                } else {
                    try {
                        val loginResult = TelematicsAuth.refreshTokenOrLogin(
                            BuildConfig.INSTANCE_ID,
                            BuildConfig.INSTANCE_KEY,
                            userRepository.getDeviceToken(),
                            sessionData.accessToken,
                            sessionData.refreshToken
                        ).await()

                        val newSession = SessionData(
                            loginResult.accessToken,
                            loginResult.refreshToken,
                            null
                        )

                        sessionRepository.saveSession(newSession)
                        request
                            .newBuilder()
                            .removeHeader(AUTHORIZATION_HEADER_NAME)
                            .addHeader(
                                AUTHORIZATION_HEADER_NAME,
                                newSession.accessToken.buildAuthorizationHeader()
                            )
                            .build()
                    } catch (e: Exception) {
                        if (e is ApiException && e.errorCode in listOf(400, 404, 422)) {
                            sessionRepository.clearSession()
                        }
                        null
                    }
                }
            }
        }
    }

    private fun String.buildAuthorizationHeader() = "Bearer $this".trim()
    private fun responseCount(response: Response?): Int {
        var responseCurrent = response
        var result = 0
        while (responseCurrent != null) {
            responseCurrent = responseCurrent.priorResponse
            result++
        }
        return result
    }

    companion object {
        private const val MAX_AUTHENTICATE_TRIES = 3
        private const val AUTHORIZATION_HEADER_NAME = "Authorization"
    }
}