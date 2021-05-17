package com.telematics.domain_.model

data class SessionData(
    val deviceToken: String,
    val accessToken: String,
    val refreshToken: String,
    var expiresIn: Long?
)