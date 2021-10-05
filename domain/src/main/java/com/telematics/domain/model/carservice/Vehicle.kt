package com.telematics.domain.model.carservice

import java.io.Serializable

data class Vehicle(
    var plateNumber: String? = "",
    var vin: String? = "",
    var manufacturer: String? = "",
    var manufacturerId: Int? = null,
    var model: String? = "",
    var modelId: Int? = null,
    var name: String? = "",
    var carYear: Int? = null,
    var initialMileage: String? = "",

    var token: String? = "",
    var activated: Boolean = false,

    var company: String? = "",
    var type: String? = "",
    var specialMarks: String? = "",
    var nvic: String? = "",
    var vehicleIdString: String? = ""
) : Serializable