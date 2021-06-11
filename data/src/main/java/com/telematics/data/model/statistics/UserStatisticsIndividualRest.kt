package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class UserStatisticsIndividualRest(
    @SerializedName("TripsCount") val tripsCount: Double,
    @SerializedName("MileageMile") val mileageMile: Double,
    @SerializedName("MileageKm") val mileageKm: Double,
    @SerializedName("DrivingTime") val drivingTime: Double,
    @SerializedName("AverageSpeedKmh") val averageSpeedKmh: Double,
    @SerializedName("MaxSpeedKmh") val maxSpeedKmh: Double
)