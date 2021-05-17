package com.telematics.data_.interceptor

import com.telematics.data_.api.RefreshApi
import com.telematics.data_.mappers.toSessionData
import com.telematics.data_.model.refresh_token.RefreshRequest
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.repository.SessionRepo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.*
import javax.inject.Inject

class MainInterceptor @Inject constructor(
    private val sessionRepository: SessionRepo,
    private val refreshApi: RefreshApi
) : Interceptor, Authenticator {
    private val monitor = Any()
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        runBlocking {
            if (sessionRepository.isLoggedIn()) {
                request = request.newBuilder()
                    .addHeader("Authorization", sessionRepository.getSession().accessToken)
                    .build()
            }
        }
        return chain.proceed(request)
    }

    override fun authenticate(route: Route?, response: Response): Request {
        synchronized(monitor) {
            return runBlocking {
                val request = response.request
                val sessionData = sessionRepository.getSession()
                if (response.request.header("Authorization") != sessionData.accessToken) {
                    request.newBuilder().removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer ${sessionData.accessToken}").build()
                } else {
                    val newSession: SessionData =
                        flow {
                            emit(
                                refreshApi.refreshToken(
                                    RefreshRequest(
                                        sessionData.accessToken,
                                        sessionData.refreshToken
                                    )
                                ).result!!.toSessionData()
                            )
                        }.catch {
                            //throw LogoutException("Unable to refresh loken")
                        }.first()
                    sessionRepository.saveSession(newSession)
                    request.newBuilder().removeHeader("Authorization")
                        .addHeader("Authorization", newSession.accessToken).build()
                }
            }
        }
    }
}