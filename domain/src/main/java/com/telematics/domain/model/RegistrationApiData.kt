package com.telematics.domain.model

data class RegistrationApiData(
    var deviceToken: String,
    val accessToken: String,
    val refreshToken: String,
    var expiresIn: Long?
)