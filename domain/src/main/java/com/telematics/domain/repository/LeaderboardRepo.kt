package com.telematics.domain.repository

import com.telematics.domain.model.leaderboard.LeaderboardUserItems

interface LeaderboardRepo {

    suspend fun getLeaderboardUserData(): List<LeaderboardUserItems>
}