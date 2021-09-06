package com.telematics.domain.model.reward

data class Streak(
    val cardType: StreakCarType,
    val currentDistance: String,
    val currentDuration: String?,
    val currentTrips: String,
    val bestDistance: String,
    val bestDuration: String?,
    val bestTrips: String
)