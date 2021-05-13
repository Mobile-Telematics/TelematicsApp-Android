package com.telematics.data_.repository

import com.telematics.data_.model.SessionData
import com.telematics.data_.model.login.LoginType

interface AuthRepo {
    suspend fun login(login: String, password: String, loginType: LoginType): SessionData
    suspend fun logout()
}