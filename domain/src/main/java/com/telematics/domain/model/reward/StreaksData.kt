package com.telematics.domain.model.reward

data class StreaksData(

    //Acceleration
    val StreakAccelerationBest: Int = 0,
    val StreakAccelerationBestDurationSec: Int = 0, //in min
    val StreakAccelerationBestDistanceKm: Double = 0.0,
    val StreakAccelerationBestFromDate: String = "",
    val StreakAccelerationBestToDate: String = "",

    val StreakAccelerationCurrentStreak: Int = 0,
    val StreakAccelerationCurrentDurationSec: Int = 0, //in min
    val StreakAccelerationCurrentDistanceKm: Double = 0.0,
    val StreakAccelerationCurrentFromDate: String = "",
    val StreakAccelerationCurrentToDate: String = "",

    //Braking
    val StreakBrakingBest: Int = 0,
    val StreakBrakingBestDurationSec: Int = 0, //in min
    val StreakBrakingBestDistanceKm: Double = 0.0,
    val StreakBrakingBestFromDate: String = "",
    val StreakBrakingBestToDate: String = "",

    val StreakBrakingCurrentStreak: Int = 0,
    val StreakBrakingCurrentDurationSec: Int = 0, //in min
    val StreakBrakingCurrentDistanceKm: Double = 0.0,
    val StreakBrakingCurrentFromDate: String = "",
    val StreakBrakingCurrentToDate: String = "",

    //Cornering
    val StreakCorneringBest: Int = 0,
    val StreakCorneringBestDurationSec: Int = 0, //in min
    val StreakCorneringBestDistanceKm: Double = 0.0,
    val StreakCorneringBestFromDate: String = "",
    val StreakCorneringBestToDate: String = "",

    val StreakCorneringCurrentStreak: Int = 0,
    val StreakCorneringCurrentDurationSec: Int = 0, //in min
    val StreakCorneringCurrentDistanceKm: Double = 0.0,
    val StreakCorneringCurrentFromDate: String = "",
    val StreakCorneringCurrentToDate: String = "",

    //OverSpeed
    val StreakOverSpeedBest: Int = 0,
    val StreakOverSpeedBestDurationSec: Int = 0, //in min
    val StreakOverSpeedBestDistanceKm: Double = 0.0,
    val StreakOverSpeedBestFromDate: String = "",
    val StreakOverSpeedBestToDate: String = "",

    val StreakOverSpeedCurrentStreak: Int = 0,
    val StreakOverSpeedCurrentDurationSec: Int = 0, //in min
    val StreakOverSpeedCurrentDistanceKm: Double = 0.0,
    val StreakOverSpeedCurrentFromDate: String = "",
    val StreakOverSpeedCurrentToDate: String = "",

    //PhoneUsage
    val StreakPhoneUsageBest: Int = 0,
    val StreakPhoneUsageBestDurationSec: Int = 0, //in min
    val StreakPhoneUsageBestDistanceKm: Double = 0.0,
    val StreakPhoneUsageBestFromDate: String = "",
    val StreakPhoneUsageBestToDate: String = "",

    val StreakPhoneUsageCurrentStreak: Int = 0,
    val StreakPhoneUsageCurrentDurationSec: Int = 0, //in min
    val StreakPhoneUsageCurrentDistanceKm: Double = 0.0,
    val StreakPhoneUsageCurrentFromDate: String = "",
    val StreakPhoneUsageCurrentToDate: String = ""
)