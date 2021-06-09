package com.telematics.domain.model

interface IAvailableSession

data class SessionData(
    val accessToken: String,
    val refreshToken: String,
    var expiresIn: Long?
) : IAvailableSession {
    fun isEmpty(): Boolean = accessToken.isBlank()
}