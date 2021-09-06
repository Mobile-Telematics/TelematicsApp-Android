package com.telematics.domain.repository

import com.telematics.domain.model.SessionData


interface SessionRepo {
    fun saveSession(session: SessionData)
    fun getSession(): SessionData
    fun clearSession()

    fun saveStateForRewardInviteScreen()
    fun isRewardInviteScreenOpened(): Boolean
    fun clearStateForRewardInviteScreen()
}