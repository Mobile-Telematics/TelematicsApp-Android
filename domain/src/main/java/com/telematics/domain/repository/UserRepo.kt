package com.telematics.domain.repository

import com.telematics.domain.model.authentication.User

interface UserRepo {

    fun saveDeviceToken(deviceToken: String)
    fun getDeviceToken(): String

    fun saveUserId(uId: String)
    fun getUserId(): String?

    fun getUser(): User
    fun saveUser(user: User)

    suspend fun clear()
}