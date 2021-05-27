package com.telematics.data_.model.login

import com.google.gson.annotations.SerializedName

data class RegistrationBody(
    @SerializedName("Email")
    val email: String
)