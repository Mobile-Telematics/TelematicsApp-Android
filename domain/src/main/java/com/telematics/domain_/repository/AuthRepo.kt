package com.telematics.domain_.repository

import com.telematics.domain_.model.LoginType
import com.telematics.domain_.model.SessionData

interface AuthRepo {
    suspend fun login(login: String, password: String, loginType: LoginType): SessionData
    suspend fun logout()
}