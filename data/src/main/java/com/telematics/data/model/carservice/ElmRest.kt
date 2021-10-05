package com.telematics.data.model.carservice

import com.google.gson.annotations.SerializedName

data class ElmRest(
    @SerializedName("Id") val id: Int,
    @SerializedName("UUID") val uuid: String,
    @SerializedName("Name") val name: String?,
    @SerializedName("Model") val model: String?,
    @SerializedName("Manufacturer") val manufacturer: String?,
    @SerializedName("SoftwareVersion") val softwareVersion: String?,
    @SerializedName("Mac") val mac: String?
)