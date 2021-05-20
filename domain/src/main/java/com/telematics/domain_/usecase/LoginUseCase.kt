package com.telematics.domain_.usecase

import com.telematics.domain_.model.LoginType
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.repository.AuthRepo
import com.telematics.domain_.repository.SessionRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepo,
    private val sessionRepository: SessionRepo
) {
    fun runLogin(login: String, password: String, loginType: LoginType): Flow<SessionData> {
        return flow {
            val result = authRepository.login(login, password, loginType)
            result
            sessionRepository.saveSession(result)
            emit(result)
        }
    }

    suspend fun logout() {
        sessionRepository.clearSession()
        authRepository.logout()
    }

    suspend fun getSessionData(): SessionData = sessionRepository.getSession()
}