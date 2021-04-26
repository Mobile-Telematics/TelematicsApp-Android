package com.telematics.data_.repository

import com.telematics.data_.model.SessionData


interface SessionRepo {
    suspend fun saveSession(session: SessionData)
    suspend fun getSession(): SessionData
    suspend fun clearSession()
    suspend fun isLoggedIn(): Boolean
}