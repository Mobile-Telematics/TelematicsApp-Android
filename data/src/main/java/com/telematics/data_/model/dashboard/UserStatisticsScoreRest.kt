package com.telematics.data_.model.dashboard

import com.google.gson.annotations.SerializedName

data class UserStatisticsScoreRest(
        @SerializedName("AccelerationScore") val accelerationScore: Double,
        @SerializedName("BrakingScore") val brakingScore: Double,
        @SerializedName("SpeedingScore") val speedingScore: Double,
        @SerializedName("DistractedScore") val distractedScore: Double,
        @SerializedName("CorneringScore") val corneringScore: Double,
        @SerializedName("OverallScore") val overallScore: Double
)