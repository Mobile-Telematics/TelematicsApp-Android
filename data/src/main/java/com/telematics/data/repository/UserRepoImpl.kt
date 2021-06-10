package com.telematics.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.telematics.domain.model.authentication.User
import com.telematics.domain.repository.UserRepo
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : UserRepo {

    private val deviceTokenKey = "device_token"
    private val userIdKey = "user_id"

    override suspend fun saveDeviceToken(deviceToken: String?) {
        Log.d("UserRepoImpl", "setDeviceToken: deviceId $deviceToken")
        sharedPreferences.edit()
            .putString(deviceTokenKey, deviceToken)
            .apply()
    }

    override suspend fun getDeviceToken(): String {
        val deviceToken = sharedPreferences.getString(deviceTokenKey, "") ?: ""
        Log.d("UserRepoImpl", "getDeviceToken: deviceId $deviceToken")
        return deviceToken
    }

    override suspend fun saveUserId(uId: String?) {
        sharedPreferences.edit()
            .putString(userIdKey, uId)
            .apply()
    }

    override suspend fun getUserId(): String? {
        return sharedPreferences.getString(userIdKey, null)
    }

    override suspend fun clear() {
        sharedPreferences.edit()
            .remove("email")
            .remove("deviceToken")
            .remove("userId")
            .remove("firstName")
            .remove("lastName")
            .remove("phone")
            .remove("birthday")
            .remove("address")
            .remove("clientId")
            .remove(deviceTokenKey)
            .remove(userIdKey)
            .apply()
    }

    override suspend fun getUser(): User {
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

        Log.d("UserRepoImpl", "getUser: email:${user.email}, phone:${user.phone} $user")
        return user
    }

    override suspend fun saveUser(user: User) {
        Log.d("UserRepoImpl", "saveUser: email:${user.email}, phone:${user.phone} $user")
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