package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class DrivingDetailsRest(
    @SerializedName("SafetyScore")
    val score: Int,
    @SerializedName("AccelerationScore")
    val scoreAcceleration: Int,
    @SerializedName("CalcDate")
    val scoreDate: String,
    @SerializedName("BrakingScore")
    val scoreDeceleration: Int,
    @SerializedName("PhoneUsageScore")
    val scoreDistraction: Int,
    @SerializedName("SpeedingScore")
    val scoreSpeeding: Int,
    @SerializedName("CorneringScore")
    val scoreTurn: Int
)