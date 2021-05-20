package com.telematics.data_.repository

import com.telematics.data_.api.DriveCoinsApi
import com.telematics.data_.api.UserStatisticsApi
import com.telematics.data_.extentions.DateFormat
import com.telematics.data_.extentions.timeMillsToDisplayableString
import com.telematics.data_.mappers.*
import com.telematics.domain_.model.dashboard.*
import com.telematics.domain_.repository.DashboardRepo
import com.telematics.domain_.repository.SessionRepo
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DashboardRepoImpl @Inject constructor(
    private val driveCoinsApi: DriveCoinsApi,
    private val userStatisticsApi: UserStatisticsApi,
    private val sessionRepo: SessionRepo
) : DashboardRepo {

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
            userStatisticsApi.getScoreData(sessionRepo.getSession().deviceToken, startDate, endDate)
        return response.result?.toUserStatisticsScoreData() ?: UserStatisticsScoreData()
    }

    override suspend fun getMainEcoScoring(): DashboardEcoScoringMain {
        return userStatisticsApi.getMainEcoScoring().result?.toDashboardEcoScoringMain()
            ?: DashboardEcoScoringMain()
    }

    override suspend fun getEcoScoringStatisticsData(type: Int): DashboardEcoScoringTabData {

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
        ).result?.toDashboardEcoScoringTabData() ?: DashboardEcoScoringTabData()
    }
}