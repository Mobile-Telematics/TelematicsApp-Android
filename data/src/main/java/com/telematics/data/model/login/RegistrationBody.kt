package com.telematics.data.model.login

import com.google.gson.annotations.SerializedName

data class RegistrationBody(
    @SerializedName("Email")
    val email: String? = null,
    @SerializedName("Phone")
    val phone: String? = null
)