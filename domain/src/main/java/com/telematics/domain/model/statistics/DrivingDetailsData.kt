package com.telematics.domain.model.statistics

data class DrivingDetailsData(
    val createdAtUtc: String = "",
    val distanceKm: Double = 0.0,
    val durationMinutes: Double = 0.0,
    val periodDays: Int = 0,
    val score: Int = 0,
    val scoreAcceleration: Int = 0,
    val scoreDate: String = "",
    val scoreDeceleration: Int = 0,
    val scoreDistraction: Int = 0,
    val scoreSpeeding: Int = 0,
    val scoreTurn: Int = 0,
    val trips: Int = 0
)