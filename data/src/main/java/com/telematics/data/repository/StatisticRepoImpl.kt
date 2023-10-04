package com.telematics.data.repository

import com.telematics.data.api.DriveCoinsApi
import com.telematics.data.api.LeaderboardApi
import com.telematics.data.api.UserStatisticsApi
import com.telematics.data.extentions.DateFormat
import com.telematics.data.extentions.timeMillsToDisplayableString
import com.telematics.data.mappers.toDashboardEcoScoringMain
import com.telematics.data.mappers.toDashboardEcoScoringTabData
import com.telematics.data.mappers.toDrivingDetailsData
import com.telematics.data.mappers.toLeaderboardData
import com.telematics.data.mappers.toUserStatisticsIndividualData
import com.telematics.data.mappers.toUserStatisticsScoreData
import com.telematics.data.mappers.transformOnDemand
import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.statistics.DriveCoins
import com.telematics.domain.model.statistics.DrivingDetailsData
import com.telematics.domain.model.statistics.StatisticEcoScoringMain
import com.telematics.domain.model.statistics.StatisticEcoScoringTabData
import com.telematics.domain.model.statistics.UserStatisticsIndividualData
import com.telematics.domain.model.statistics.UserStatisticsScoreData
import com.telematics.domain.repository.StatisticRepo
import com.telematics.domain.repository.UserRepo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class StatisticRepoImpl @Inject constructor(
    private val driveCoinsApi: DriveCoinsApi,
    private val userStatisticsApi: UserStatisticsApi,
    private val leaderboardApi: LeaderboardApi,
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
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2000)
        calendar.set(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = format.format(calendar.time)
        val endDate = format.format(Calendar.getInstance().time)
        return userStatisticsApi.getMainEcoScoring(
            startDate,
            endDate
        ).result?.toDashboardEcoScoringMain()
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

    override suspend fun getLeaderboard(type: LeaderboardType): List<LeaderboardMemberData>? {

        val mappedType = when (type) {
            LeaderboardType.Rate -> 6
            LeaderboardType.Acceleration -> 1
            LeaderboardType.Deceleration -> 2
            LeaderboardType.Speeding -> 4
            LeaderboardType.Distraction -> 3
            LeaderboardType.Turn -> 5
            LeaderboardType.Trips -> 8
            LeaderboardType.Distance -> 7
            LeaderboardType.Duration -> 9
        }

        val deviceToken = userRepo.getDeviceToken()
        val data = leaderboardApi.getLeaderBoard(deviceToken, 10, 2, 6)
        return data.result?.toLeaderboardData(mappedType)
    }

    override suspend fun getOnDemandCompletedData(job: DashboardOnDemandJob): Pair<UserStatisticsIndividualData, UserStatisticsScoreData> {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val endDate = format.format(calendar.time)

        val first =
            userStatisticsApi.getIndividualDataByTag(job.getTag, "2000-01-01T00:00:01", endDate)
                .result?.transformOnDemand() ?: UserStatisticsIndividualData()

        val second = userStatisticsApi.getIndividualScoreDataByTag(
            job.getTag,
            "2000-01-01T00:00:01",
            endDate
        )
            .result?.toUserStatisticsScoreData() ?: UserStatisticsScoreData()


        return Pair(first, second)
    }
}