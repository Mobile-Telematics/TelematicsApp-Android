package com.telematics.domain_.repository

import com.telematics.domain_.model.dashboard.*

interface DashboardRepo {
    suspend fun getDriveCoins(): DriveCoins
    suspend fun getUserStatisticsIndividualData(): UserStatisticsIndividualData
    suspend fun getDrivingDetails(): List<DrivingDetailsData>
    suspend fun getUserStatisticsScoreData(): UserStatisticsScoreData
    suspend fun getMainEcoScoring(): DashboardEcoScoringMain
    suspend fun getEcoScoringStatisticsData(type: Int): DashboardEcoScoringTabData
    suspend fun getLastTrip()
}