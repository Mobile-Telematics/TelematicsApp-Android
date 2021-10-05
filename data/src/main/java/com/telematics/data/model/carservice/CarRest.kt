package com.telematics.data.model.carservice

import com.google.gson.annotations.SerializedName

data class CarRest(
    @SerializedName("Company") val company: String?,
    @SerializedName("Name") val name: String?,
    @SerializedName("PlateNumber") val plateNumber: String?,
    @SerializedName("Vin") val vin: String?,
    @SerializedName("ManufacturerId") val manufacturerId: Int?,
    @SerializedName("Manufacturer") val manufacturer: String?,
    @SerializedName("ModelId") val modelId: Int?,
    @SerializedName("Model") val model: String?,
    @SerializedName("Type") val type: String?,
    @SerializedName("CarYear") val carYear: Int?,
    @SerializedName("SpecialMarks") val specialMarks: String?,
    @SerializedName("Nvic") val nvic: String?,
    @SerializedName("InitialMilage") val initialMilage: String?,
    @SerializedName("VehicleIdString") val vehicleIdString: String?,
    @SerializedName("Token") val token: String?,
    var activated: Boolean
)