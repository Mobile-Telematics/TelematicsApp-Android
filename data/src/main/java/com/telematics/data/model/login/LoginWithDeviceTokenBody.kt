package com.telematics.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginWithDeviceTokenBody(
    @SerializedName("loginFields")
    val loginFields: LoginFieldsWithDeviceToken,
    @SerializedName("password")
    val password: String
)