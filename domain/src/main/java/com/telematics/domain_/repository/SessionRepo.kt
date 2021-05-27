package com.telematics.domain_.repository

import com.telematics.domain_.model.SessionData


interface SessionRepo {
    fun saveSession(session: SessionData)
    fun getSession(): SessionData
    fun clearSession()
}