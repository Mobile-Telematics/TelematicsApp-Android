package com.telematics.domain.model.leaderboard

data class LeaderboardUser(
    val accelerationPerc: Double = 0.0,
    val accelerationPlace: Int = 0,
    val accelerationScore: Double = 0.0,
    val decelerationPerc: Double = 0.0,
    val decelerationPlace: Int = 0,
    val decelerationScore: Double = 0.0,
    val distance: Double = 0.0,
    val distractionPerc: Double = 0.0,
    val distractionPlace: Int = 0,
    val distractionScore: Double = 0.0,
    val duration: Double = 0.0,
    val perc: Double = 0.0,
    val place: Int = 0,
    val score: Double = 0.0,
    val speedingPerc: Double = 0.0,
    val speedingPlace: Int = 0,
    val speedingScore: Double = 0.0,
    val trips: Int = 0,
    val turnPerc: Double = 0.0,
    val turnPlace: Int = 0,
    val turnScore: Double = 0.0,
    val usersNumber: Int = 0,
    val tripsPlace: Int = 0,
    val durationPlace: Int = 0,
    val distancePlace: Int = 0
)