package com.telematics.data.model.tracking


import com.google.gson.annotations.SerializedName

data class WrongEventBody(
    @SerializedName("EventType")
    val eventType: String?,
    @SerializedName("Latitude")
    val latitude: Double?,
    @SerializedName("Longitude")
    val longitude: Double?,
    @SerializedName("PointDate")
    val pointDate: String?
)

data class ChangeEventBody(
		@SerializedName("EventType")
		val eventType: String?,
		@SerializedName("Latitude")
		val latitude: Double?,
		@SerializedName("Longitude")
		val longitude: Double?,
		@SerializedName("PointDate")
		val pointDate: String?,
		@SerializedName("ChangeTypeTo")
		val changeTypeTo: String?
)