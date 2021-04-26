package com.telematics.data_.repository

import android.content.SharedPreferences
import com.telematics.data_.model.SessionData
import javax.inject.Inject

class SessionRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SessionRepo {

    private val deviceTokenKey = "device_token"

    @Synchronized
    override suspend fun saveSession(session: SessionData) {
        sharedPreferences.edit()
            .putString(deviceTokenKey, session.deviceToken)
            .commit()
    }

    @Synchronized
    override suspend fun getSession(): SessionData {
        val deviceToken = sharedPreferences.getString(deviceTokenKey, "") ?: ""
        return SessionData(deviceToken)
    }

    @Synchronized
    override suspend fun clearSession() {
        sharedPreferences.edit()
            .remove(deviceTokenKey)
            .commit()
    }

    @Synchronized
    override suspend fun isLoggedIn(): Boolean {
        return !sharedPreferences.getString(deviceTokenKey, "").isNullOrBlank()

    }
}