package com.telematics.domain.repository

import com.telematics.domain.model.LoginType
import com.telematics.domain.model.SessionData

interface UserServicesRepo {
    suspend fun login(login: String, password: String, loginType: LoginType): SessionData
    suspend fun logout()
    suspend fun loginWithDeviceToken(deviceToken: String): SessionData
    suspend fun registrationWithEmail(email: String): SessionData
    suspend fun registrationWithPhone(phone: String): SessionData
}