package com.telematics.data.model.reward

import com.google.gson.annotations.SerializedName

data class StreaksRest(
    //Acceleration
    @SerializedName("StreakAccelerationBest") val StreakAccelerationBest: Int,
    @SerializedName("StreakAccelerationBestDurationSec") val StreakAccelerationBestDurationSec: Int, //in min
    @SerializedName("StreakAccelerationBestDistanceKm") val StreakAccelerationBestDistanceKm: Double,
    @SerializedName("StreakAccelerationBestFromDate") val StreakAccelerationBestFromDate: String,
    @SerializedName("StreakAccelerationBestToDate") val StreakAccelerationBestToDate: String,

    @SerializedName("StreakAccelerationCurrentStreak") val StreakAccelerationCurrentStreak: Int,
    @SerializedName("StreakAccelerationCurrentDurationSec") val StreakAccelerationCurrentDurationSec: Int, //in min
    @SerializedName("StreakAccelerationCurrentDistanceKm") val StreakAccelerationCurrentDistanceKm: Double,
    @SerializedName("StreakAccelerationCurrentFromDate") val StreakAccelerationCurrentFromDate: String,
    @SerializedName("StreakAccelerationCurrentToDate") val StreakAccelerationCurrentToDate: String,

    //Braking
    @SerializedName("StreakBrakingBest") val StreakBrakingBest: Int,
    @SerializedName("StreakBrakingBestDurationSec") val StreakBrakingBestDurationSec: Int, //in min
    @SerializedName("StreakBrakingBestDistanceKm") val StreakBrakingBestDistanceKm: Double,
    @SerializedName("StreakBrakingBestFromDate") val StreakBrakingBestFromDate: String,
    @SerializedName("StreakBrakingBestToDate") val StreakBrakingBestToDate: String,

    @SerializedName("StreakBrakingCurrentStreak") val StreakBrakingCurrentStreak: Int,
    @SerializedName("StreakBrakingCurrentDurationSec") val StreakBrakingCurrentDurationSec: Int, //in min
    @SerializedName("StreakBrakingCurrentDistanceKm") val StreakBrakingCurrentDistanceKm: Double,
    @SerializedName("StreakBrakingCurrentFromDate") val StreakBrakingCurrentFromDate: String,
    @SerializedName("StreakBrakingCurrentToDate") val StreakBrakingCurrentToDate: String,

    //Cornering
    @SerializedName("StreakCorneringBest") val StreakCorneringBest: Int,
    @SerializedName("StreakCorneringBestDurationSec") val StreakCorneringBestDurationSec: Int, //in min
    @SerializedName("StreakCorneringBestDistanceKm") val StreakCorneringBestDistanceKm: Double,
    @SerializedName("StreakCorneringBestFromDate") val StreakCorneringBestFromDate: String,
    @SerializedName("StreakCorneringBestToDate") val StreakCorneringBestToDate: String,

    @SerializedName("StreakCorneringCurrentStreak") val StreakCorneringCurrentStreak: Int,
    @SerializedName("StreakCorneringCurrentDurationSec") val StreakCorneringCurrentDurationSec: Int, //in min
    @SerializedName("StreakCorneringCurrentDistanceKm") val StreakCorneringCurrentDistanceKm: Double,
    @SerializedName("StreakCorneringCurrentFromDate") val StreakCorneringCurrentFromDate: String,
    @SerializedName("StreakCorneringCurrentToDate") val StreakCorneringCurrentToDate: String,

    //OverSpeed
    @SerializedName("StreakOverSpeedBest") val StreakOverSpeedBest: Int,
    @SerializedName("StreakOverSpeedBestDurationSec") val StreakOverSpeedBestDurationSec: Int, //in min
    @SerializedName("StreakOverSpeedBestDistanceKm") val StreakOverSpeedBestDistanceKm: Double,
    @SerializedName("StreakOverSpeedBestFromDate") val StreakOverSpeedBestFromDate: String,
    @SerializedName("StreakOverSpeedBestToDate") val StreakOverSpeedBestToDate: String,

    @SerializedName("StreakOverSpeedCurrentStreak") val StreakOverSpeedCurrentStreak: Int,
    @SerializedName("StreakOverSpeedCurrentDurationSec") val StreakOverSpeedCurrentDurationSec: Int, //in min
    @SerializedName("StreakOverSpeedCurrentDistanceKm") val StreakOverSpeedCurrentDistanceKm: Double,
    @SerializedName("StreakOverSpeedCurrentFromDate") val StreakOverSpeedCurrentFromDate: String,
    @SerializedName("StreakOverSpeedCurrentToDate") val StreakOverSpeedCurrentToDate: String,

    //PhoneUsage
    @SerializedName("StreakPhoneUsageBest") val StreakPhoneUsageBest: Int,
    @SerializedName("StreakPhoneUsageBestDurationSec") val StreakPhoneUsageBestDurationSec: Int, //in min
    @SerializedName("StreakPhoneUsageBestDistanceKm") val StreakPhoneUsageBestDistanceKm: Double,
    @SerializedName("StreakPhoneUsageBestFromDate") val StreakPhoneUsageBestFromDate: String,
    @SerializedName("StreakPhoneUsageBestToDate") val StreakPhoneUsageBestToDate: String,

    @SerializedName("StreakPhoneUsageCurrentStreak") val StreakPhoneUsageCurrentStreak: Int,
    @SerializedName("StreakPhoneUsageCurrentDurationSec") val StreakPhoneUsageCurrentDurationSec: Int, //in min
    @SerializedName("StreakPhoneUsageCurrentDistanceKm") val StreakPhoneUsageCurrentDistanceKm: Double,
    @SerializedName("StreakPhoneUsageCurrentFromDate") val StreakPhoneUsageCurrentFromDate: String,
    @SerializedName("StreakPhoneUsageCurrentToDate") val StreakPhoneUsageCurrentToDate: String
)