package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(
    @SerializedName("ScoringRate")
    val scoringRate: String,
    @SerializedName("Users")
    val users: List<LeaderboardUser>,
    @SerializedName("UsersNumber")
    val usersNumber: Int
)