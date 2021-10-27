package com.telematics.domain.repository

import android.content.Context
import android.graphics.Bitmap
import com.telematics.domain.model.authentication.User

interface UserRepo {

    suspend fun saveDeviceToken(deviceToken: String?)
    suspend fun getDeviceToken(): String

    suspend fun saveUserId(uId: String?)
    suspend fun getUserId(): String?

    suspend fun getUser(): User
    suspend fun saveUser(user: User)

    suspend fun getUserPicture(context: Context): Bitmap

    suspend fun needOnboarding(): Boolean
    suspend fun setNeedOnboarding(need: Boolean)

    suspend fun clear()
}