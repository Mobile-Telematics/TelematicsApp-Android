package com.telematics.data.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("session")
    val session: String
)