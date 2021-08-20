package com.telematics.domain.repository

import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.model.leaderboard.LeaderboardUserItems

interface LeaderboardRepo {

    suspend fun getLeaderboardUserData(): List<LeaderboardUserItems>
    suspend fun getLeaderboardUserList(type: Int): List<LeaderboardMemberData>
}