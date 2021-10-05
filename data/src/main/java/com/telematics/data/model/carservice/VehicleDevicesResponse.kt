package com.telematics.data.model.carservice

import com.google.gson.annotations.SerializedName

data class VehicleDevicesResponse(
        @SerializedName("Elms") val elms: List<ElmRest>?
)