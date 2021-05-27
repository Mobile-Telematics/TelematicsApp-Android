package com.telematics.data_.model.login

import com.google.gson.annotations.SerializedName

data class LoginFieldsWithDeviceToken(
    @SerializedName("DeviceToken")
    val deviceToken: String? = null
)