package com.telematics.domain.model

data class SessionData(
    val deviceToken: String,
    val accessToken: String,
    val refreshToken: String,
    var expiresIn: Long?
)