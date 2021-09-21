package com.telematics.domain.model.reward

data class Streak(
    val cardType: StreakCarType,
    val currentDistance: String,
    val currentDate: String?,
    val currentTrips: String,
    val bestDistance: String,
    val bestDate: String?,
    val bestTrips: String
)