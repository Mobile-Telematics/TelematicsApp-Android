package com.telematics.data.model.carservice

import com.google.gson.annotations.SerializedName

data class VehicleResponse(
       @SerializedName("Cars") val cars: List<CarRest>
)