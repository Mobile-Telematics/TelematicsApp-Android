package com.telematics.data.repository

import com.telematics.data.api.DriveCoinsApi
import com.telematics.data.api.UserStatisticsApi
import com.telematics.data.extentions.DateFormat
import com.telematics.data.extentions.timeMillsToDisplayableString
import com.telematics.data.mappers.*
import com.telematics.domain.model.statistics.*
import com.telematics.domain.repository.StatisticRepo
import com.telematics.domain.repository.UserRepo
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class StatisticRepoImpl @Inject constructor(
    private val driveCoinsApi: DriveCoinsApi,
    private val userStatisticsApi: UserStatisticsApi,
    private val userRepo: UserRepo
) : StatisticRepo {

    override suspend fun getDriveCoins(): DriveCoins {

        val dateEnd =
            System.currentTimeMillis().timeMillsToDisplayableString(DateFormat.Iso8601DateTime())

        val rest =
            driveCoinsApi.getDriveCoinsIndividual("2000-01-01T00:00:01", dateEnd)
        val coins = rest.result?.totalCoins ?: 0
        return DriveCoins(coins)
    }

    override suspend fun getUserStatisticsIndividualData(): UserStatisticsIndividualData {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2000)
        val startDate = format.format(calendar.time)
        val endDate = format.format(Calendar.getInstance().time)
        return userStatisticsApi.getIndividualData(
            startDate,
            endDate
        ).result?.toUserStatisticsIndividualData()
            ?: UserStatisticsIndividualData()
    }

    override suspend fun getDrivingDetails(): List<DrivingDetailsData> {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val endDate = format.format(calendar.time)
        calendar.add(Calendar.DATE, -14)
        val startDate = format.format(calendar.time)

        return userStatisticsApi.getDrivingDetails(startDate, endDate).result!!.map {
            it.toDrivingDetailsData()
        }
    }

    override suspend fun getUserStatisticsScoreData(): UserStatisticsScoreData {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val endDate = format.format(calendar.time)
        val startDate = format.format(calendar.time)

        val response =
            userStatisticsApi.getScoreData(userRepo.getDeviceToken(), startDate, endDate)
        return response.result?.toUserStatisticsScoreData() ?: UserStatisticsScoreData()
    }

    override suspend fun getMainEcoScoring(): StatisticEcoScoringMain {
        return userStatisticsApi.getMainEcoScoring().result?.toDashboardEcoScoringMain()
            ?: StatisticEcoScoringMain()
    }

    override suspend fun getEcoScoringStatisticsData(type: Int): StatisticEcoScoringTabData {

        val amount = when (type) {
            Calendar.DAY_OF_WEEK -> -7
            Calendar.MONTH -> -30
            Calendar.YEAR -> -365
            else -> 0
        }

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, amount)
        val startDate = format.format(calendar.time)
        val endDate = format.format(Calendar.getInstance().time)

        return userStatisticsApi.getIndividualData(
            startDate,
            endDate
        ).result?.toDashboardEcoScoringTabData() ?: StatisticEcoScoringTabData()
    }
}