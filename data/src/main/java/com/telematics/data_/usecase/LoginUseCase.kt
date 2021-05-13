package com.telematics.data_.usecase

import com.telematics.data_.model.SessionData
import com.telematics.data_.model.login.LoginType
import com.telematics.data_.repository.AuthRepo
import com.telematics.data_.repository.SessionRepo
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
            sessionRepository.saveSession(result)
            emit(result)
        }
    }

    suspend fun getSessionData(): SessionData = sessionRepository.getSession()
}