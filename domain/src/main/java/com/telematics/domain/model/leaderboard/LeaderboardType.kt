package com.telematics.domain.model.leaderboard

enum class LeaderboardType(val index: Int) {
    Rate(1),

    Acceleration(2),
    Deceleration(3),
    Speeding(4),
    Distraction(5),
    Turn(6),

    Trips(7),
    Distance(8),
    Duration(9);

    companion object {
        fun getFromIndex(index: Int): LeaderboardType {
            return values()[index - 1]
        }
    }
}