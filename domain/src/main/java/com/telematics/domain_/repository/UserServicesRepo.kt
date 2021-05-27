package com.telematics.domain_.repository

import com.telematics.domain_.model.LoginType
import com.telematics.domain_.model.SessionData

interface UserServicesRepo {
    suspend fun login(login: String, password: String, loginType: LoginType): SessionData
    suspend fun logout()
    suspend fun loginWithDeviceToken(deviceToken: String): SessionData
    suspend fun registrationWithEmail(email: String): SessionData
}