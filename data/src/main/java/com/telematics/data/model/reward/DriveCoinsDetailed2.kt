package com.telematics.data.model.reward

import com.google.gson.annotations.SerializedName

data class DriveCoinsDetailed2(
    @SerializedName("AccelerationCount") val accelerationCount: Int,
    @SerializedName("BrakingCount") val brakingCount: Int,
    @SerializedName("CorneringCount") val corneringCount: Int,
    @SerializedName("TotalSpeedingKm") val totalSpeedingKm: Double,
    @SerializedName("PhoneUsageDurationMin") val phoneUsage: Double
)