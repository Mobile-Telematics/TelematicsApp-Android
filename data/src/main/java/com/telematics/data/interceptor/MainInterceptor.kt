package com.telematics.data.interceptor

import com.telematics.data.api.RefreshApi
import com.telematics.data.mappers.toSessionData
import com.telematics.data.model.refresh_token.RefreshRequest
import com.telematics.domain.model.SessionData
import com.telematics.domain.repository.SessionRepo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
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
            val accessToken = sessionRepository.getSession().accessToken
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }
        return chain.proceed(request)
    }

    override fun authenticate(route: Route?, response: Response): Request {
        synchronized(monitor) {
            return runBlocking {
                val request = response.request()
                val sessionData = sessionRepository.getSession()
                if (response.request().header("Authorization") != sessionData.accessToken) {
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
                        }.first()
                    sessionRepository.saveSession(newSession)
                    request.newBuilder().removeHeader("Authorization")
                        .addHeader("Authorization", newSession.accessToken).build()
                }
            }
        }
    }
}