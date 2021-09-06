package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class UserStatisticsScoreRest(
        @SerializedName("AccelerationScore") val accelerationScore: Double,
        @SerializedName("BrakingScore") val brakingScore: Double,
        @SerializedName("SpeedingScore") val speedingScore: Double,
        @SerializedName("PhoneUsageScore") val distractedScore: Double,
        @SerializedName("CorneringScore") val corneringScore: Double,
        @SerializedName("SafetyScore") val overallScore: Double
)