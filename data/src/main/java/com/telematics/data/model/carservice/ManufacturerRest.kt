package com.telematics.data.model.carservice

import com.google.gson.annotations.SerializedName

data class ManufacturerRest(
    @SerializedName("Id") val id: Int,
    @SerializedName("Name") val name: String
)