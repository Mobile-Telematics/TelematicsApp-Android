package com.telematics.data.model.carservice

import com.google.gson.annotations.SerializedName

data class CarUpdateBody(
	@SerializedName("Name") val name: String? = "",
	@SerializedName("PlateNumber") val plateNumber: String? = "",
	@SerializedName("Vin") val vin: String? = "",
	@SerializedName("ManufacturerId") val manufacturerId: Int? = null,
	@SerializedName("ManufacturerName") val manufacturer: String? = "",
	@SerializedName("ModelId") val modelId: Int? = null,
	@SerializedName("ModelName") val model: String? = "",
	@SerializedName("TypeName") val type: String? = "",
	@SerializedName("CarYear") val carYear: Int? = 0,
	@SerializedName("InitialMilage") val initialMileage: String? = "",
	@SerializedName("EntityStatus") val status: Int? = 1
)