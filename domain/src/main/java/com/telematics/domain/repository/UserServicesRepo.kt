package com.telematics.domain.repository

import com.telematics.domain.model.LoginType
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData

interface UserServicesRepo {
    suspend fun login(login: String, password: String, loginType: LoginType): SessionData
    suspend fun logout()
    suspend fun loginWithDeviceToken(deviceToken: String): SessionData
    suspend fun registration(): RegistrationApiData
}