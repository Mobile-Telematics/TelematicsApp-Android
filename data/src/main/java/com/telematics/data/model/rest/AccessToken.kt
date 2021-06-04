package com.telematics.data.model.rest

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("ExpiresIn")
    val expiresIn: Long,
    @SerializedName("Token")
    val token: String
)