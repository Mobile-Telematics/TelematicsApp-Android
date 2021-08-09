package com.telematics.domain.model.leaderboard

import com.telematics.domain.R


enum class LeaderboardType(val index: Int) {
    Acceleration(1),
    Deceleration(2),
    Distraction(3),
    Speeding(4),
    Turn(5),
    Rate(6),
    Distance(7),
    Trips(8),
    Duration(9);

    companion object {
        fun getFromIndex(index: Int): LeaderboardType {
            return values()[index - 1]
        }
    }
}