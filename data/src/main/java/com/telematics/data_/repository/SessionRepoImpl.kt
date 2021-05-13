package com.telematics.data_.repository

import android.content.SharedPreferences
import com.telematics.data_.model.SessionData
import javax.inject.Inject

class SessionRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SessionRepo {

    private val deviceTokenKey = "device_token"
    private val accessTokenKey = "access_token"
    private val refreshTokenKey = "refresh_token"
    private val expiresInKey = "expires_in"

    @Synchronized
    override suspend fun saveSession(session: SessionData) {
        sharedPreferences.edit()
            .putString(deviceTokenKey, session.deviceToken)
            .putString(accessTokenKey, session.accessToken)
            .putString(refreshTokenKey, session.refreshToken)
            .putLong(expiresInKey, session.expiresIn ?: -1)
            .commit()
    }

    @Synchronized
    override suspend fun getSession(): SessionData {
        val deviceToken = sharedPreferences.getString(deviceTokenKey, "") ?: ""
        val accessToken = sharedPreferences.getString(accessTokenKey, "") ?: ""
        val refreshToken = sharedPreferences.getString(refreshTokenKey, "") ?: ""
        val expiresIn = sharedPreferences.getLong(expiresInKey, -1)
        return SessionData(deviceToken, accessToken, refreshToken, expiresIn)
    }

    @Synchronized
    override suspend fun clearSession() {
        sharedPreferences.edit()
            .remove(deviceTokenKey)
            .commit()
    }

    @Synchronized
    override suspend fun isLoggedIn(): Boolean =
        !sharedPreferences.getString(deviceTokenKey, "").isNullOrBlank()
}