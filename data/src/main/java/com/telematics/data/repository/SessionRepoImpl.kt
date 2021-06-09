package com.telematics.data.repository

import android.content.SharedPreferences
import com.telematics.domain.model.SessionData
import com.telematics.domain.repository.SessionRepo
import javax.inject.Inject

class SessionRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SessionRepo {

    private val accessTokenKey = "access_token"
    private val refreshTokenKey = "refresh_token"
    private val expiresInKey = "expires_in"

    override fun saveSession(session: SessionData) {
        sharedPreferences.edit()
            .putString(accessTokenKey, session.accessToken)
            .putString(refreshTokenKey, session.refreshToken)
            .putLong(expiresInKey, session.expiresIn ?: -1)
            .apply()
    }

    override fun getSession(): SessionData {
        val accessToken = sharedPreferences.getString(accessTokenKey, "") ?: ""
        val refreshToken = sharedPreferences.getString(refreshTokenKey, "") ?: ""
        val expiresIn = sharedPreferences.getLong(expiresInKey, -1)
        return SessionData(accessToken, refreshToken, expiresIn)
    }

    override fun clearSession() {
        sharedPreferences.edit()
            .remove(accessTokenKey)
            .remove(refreshTokenKey)
            .remove(expiresInKey)
            .apply()
    }
}