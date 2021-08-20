package com.telematics.data.repository

import com.telematics.data.api.LeaderboardApi
import com.telematics.data.mappers.toLeaderboardData
import com.telematics.data.mappers.toLeadetboardUser
import com.telematics.data.mappers.toListofLeaderboardUserItems
import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.model.leaderboard.LeaderboardUserItems
import com.telematics.domain.repository.LeaderboardRepo
import com.telematics.domain.repository.UserRepo
import javax.inject.Inject

class LeaderboardRepoImpl @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
    private val userRepo: UserRepo
) : LeaderboardRepo {

    override suspend fun getLeaderboardUserData(): List<LeaderboardUserItems> {

        val leaderboardUserResponse =
            leaderboardApi.getUserLeaderboard(userRepo.getDeviceToken()).result
        val leaderboardUser = leaderboardUserResponse.toLeadetboardUser()
        return leaderboardUser.toListofLeaderboardUserItems()
    }

    override suspend fun getLeaderboardUserList(type: Int): List<LeaderboardMemberData> {
        val d = leaderboardApi.getLeaderBoard(userRepo.getDeviceToken(), 10, 2, type)
        return d.result?.toLeaderboardData(type) ?: listOf()
    }
}