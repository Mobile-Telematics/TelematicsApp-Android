package com.telematics.domain.repository

import com.telematics.domain.model.reward.DailyLimitData
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.domain.model.reward.DriveCoinsDuration
import com.telematics.domain.model.reward.DriveCoinsTotalData
import com.telematics.domain.model.reward.Streak
import com.telematics.domain.model.reward.StreaksData

interface RewardRepo {

    suspend fun getDaily(): List<Pair<Int, Int>>
    suspend fun getDailyLimit(): DailyLimitData
    suspend fun getTotalCoinsByDuration(duration: DriveCoinsDuration): DriveCoinsTotalData
    suspend fun getDetailed(duration: DriveCoinsDuration): DriveCoinsDetailedData
    suspend fun getStreaks(): List<Streak>
    suspend fun getDrivingStreaks(): StreaksData
}