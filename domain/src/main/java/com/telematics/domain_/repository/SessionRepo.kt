package com.telematics.domain_.repository

import com.telematics.domain_.model.SessionData


interface SessionRepo {
    suspend fun saveSession(session: SessionData)
    suspend fun getSession(): SessionData
    suspend fun clearSession()
    suspend fun isLoggedIn(): Boolean
}