package com.telematics.domain.model.leaderboard

data class LeaderboardMemberData(
    var deviceToken: String? = null,
    var firstName: String? = null,
    var familyName: String? = null,
    var rank: Int = -1, // place
    var tracksCount: Int = 0, // rating
    var photoUrl: String? = null,
    var isCurrentUser: Boolean = false,
    var type: LeaderboardType = LeaderboardType.Rate,
    var nickname: String = "",
    var distance: Double = 0.0,
    var duration: Double = 0.0,
    var value: Double = 0.0,
    var valuePerc: Double = 0.0
)