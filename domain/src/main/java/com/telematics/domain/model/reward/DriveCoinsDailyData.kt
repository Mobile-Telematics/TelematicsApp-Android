package com.telematics.domain.model.reward

data class DriveCoinsDailyData(
    val data: String = "",
    val dateUpdated: String = "",
    val limitReached: Boolean = true,
    val totalEarnedCoins: Int = 0,
    val acquiredCoins: Int = 0
)