package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class DrivingDetailsRest(
    //    @SerializedName("CreatedAtUtc")
//    val createdAtUtc: String,
    @SerializedName("DeviceToken")
    val deviceToken: String,
//    @SerializedName("DistanceKm")
//    val distanceKm: Double,
//    @SerializedName("DurationMinutes")
//    val durationMinutes: Double,
//    @SerializedName("PeriodDays")
//    val periodDays: Int,
    @SerializedName("OverallScore")
    val score: Int,
    @SerializedName("AccelerationScore")
    val scoreAcceleration: Int,
    @SerializedName("CalcDate")
    val scoreDate: String,
    @SerializedName("BrakingScore")
    val scoreDeceleration: Int,
    @SerializedName("DistractedScore")
    val scoreDistraction: Int,
    @SerializedName("SpeedingScore")
    val scoreSpeeding: Int,
    @SerializedName("CorneringScore")
    val scoreTurn: Int
//    @SerializedName("Trips")
//    val trips: Int
)