package com.telematics.data.repository

import android.content.SharedPreferences
import com.telematics.domain.model.authentication.User
import com.telematics.domain.repository.UserRepo
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : UserRepo {

    private val deviceTokenKey = "device_token"
    private val userIdKey = "user_id"

    override fun saveDeviceToken(deviceToken: String) {
        sharedPreferences.edit()
            .putString(deviceTokenKey, deviceToken)
            .apply()
    }

    override fun getDeviceToken(): String {
        return sharedPreferences.getString(deviceTokenKey, "") ?: ""
    }

    override fun saveUserId(uId: String) {
        sharedPreferences.edit()
            .putString(userIdKey, uId)
            .apply()
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(userIdKey, null)
    }

    override suspend fun clear() {
        sharedPreferences.edit()
            .remove(deviceTokenKey)
            .remove(userIdKey)
            .apply()
    }

    override fun getUser(): User {

        val user = User()
        user.email = sharedPreferences.getString("email", null)
        user.deviceToken = sharedPreferences.getString("deviceToken", null)
        user.userId = sharedPreferences.getString("userId", null)
        user.firstName = sharedPreferences.getString("firstName", null)
        user.lastName = sharedPreferences.getString("lastName", null)
        user.phone = sharedPreferences.getString("phone", null)
        user.birthday = sharedPreferences.getString("birthday", null)
        user.address = sharedPreferences.getString("address", null)
        user.clientId = sharedPreferences.getString("clientId", null)
        return user
    }

    override fun saveUser(user: User) {
        sharedPreferences.edit()
            .putString("email", user.email)
            .putString("deviceToken", user.deviceToken)
            .putString("userId", user.userId)
            .putString("firstName", user.firstName)
            .putString("lastName", user.lastName)
            .putString("phone", user.phone)
            .putString("birthday", user.birthday)
            .putString("address", user.address)
            .putString("clientId", user.clientId)
            .apply()
    }
}