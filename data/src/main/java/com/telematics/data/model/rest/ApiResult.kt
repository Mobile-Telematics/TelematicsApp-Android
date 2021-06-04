package com.telematics.data.model.rest

import com.google.gson.annotations.SerializedName

data class ApiResult(
    @SerializedName("AccessToken")
    val accessToken: AccessToken,
    @SerializedName("DeviceToken")
    val deviceToken: String,
    @SerializedName("RefreshToken")
    val refreshToken: String
)