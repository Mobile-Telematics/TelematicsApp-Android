package com.telematics.data.repository

import android.util.Log
import com.telematics.data.api.DriveCoinsApi
import com.telematics.data.api.UserStatisticsApi
import com.telematics.data.mappers.*
import com.telematics.domain.model.reward.*
import com.telematics.domain.repository.RewardRepo
import com.telematics.domain.repository.SettingsRepo
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RewardRepoImpl @Inject constructor(
    private val driveCoinsApi: DriveCoinsApi,
    private val userStatisticsApi: UserStatisticsApi,
    private val settingsRepo: SettingsRepo
) : RewardRepo {

    override suspend fun getDaily(): List<Pair<Int, Int>> {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendarS = Calendar.getInstance().apply {
            this.add(Calendar.DAY_OF_YEAR, -13)
        }

        val startDate = format.format(calendarS.time)
        val endDate = format.format(Calendar.getInstance().time)

        val data = driveCoinsApi.getDriveCoinsDaily(startDate, endDate).result
        return data?.map {
            val index = it.data.indexOf("T")
            val date = if (index >= 0) it.data.substring(index - 2, index).toInt() else 0
            Pair(date, it.totalEarnedCoins)
        } ?: emptyList()
    }

    override suspend fun getDailyLimit(): DailyLimitData {

        return driveCoinsApi.getDriveCoinsDailyLimit().result.toDailyLimitData()
    }

    override suspend fun getTotalCoinsByDuration(duration: DriveCoinsDuration): DriveCoinsTotalData {

        val startDate = getPeriodByDuration(duration).first
        val endDate = getPeriodByDuration(duration).second
        return driveCoinsApi.getDriveCoinsTotal(startDate, endDate).result.toDriveCoinsTotalData()
    }

    override suspend fun getDetailed(duration: DriveCoinsDuration): DriveCoinsDetailedData {

        val startDate = getPeriodByDuration(duration).first
        val endDate = getPeriodByDuration(duration).second

        val userStatisticsIndividualRest =
            userStatisticsApi.getIndividualData(startDate, endDate).result
        val driveCoinsDetailedList = driveCoinsApi.getDriveCoinsDetailed(startDate, endDate).result
        val driveCoinsDetailed2 = userStatisticsApi.getStatisticsData(startDate, endDate).result
        val driveCoinsScoreEco = userStatisticsApi.getScore(startDate, endDate).result

        return DriveCoinsDetailedData().setCompleteData(
            userStatisticsIndividualRest,
            driveCoinsDetailedList,
            driveCoinsDetailed2,
            driveCoinsScoreEco
        )
    }

    override suspend fun getStreaks(): List<Streak> {

        val data = userStatisticsApi.getStreaks().result
        return data.toStreakList(settingsRepo.getDateMeasure())
    }

    override suspend fun getDrivingStreaks(): StreaksData {

        val data = userStatisticsApi.getStreaks().result
        return data.toStreakData()
    }

    /** additional date */
    private fun getPeriodByDuration(duration: DriveCoinsDuration): Pair<String, String> {
        val startDate = when (duration) {
            DriveCoinsDuration.ALL_TIME -> {
                allTimePeriod.first
            }
            DriveCoinsDuration.DAY -> {
                dayPeriod.first
            }
            DriveCoinsDuration.THIS_MONTH -> {
                thisMonthPeriod.first
            }
            DriveCoinsDuration.LAST_MONTH -> {
                lastMonthPeriod.first
            }
        }

        val endDate =
            if (duration != DriveCoinsDuration.LAST_MONTH)
                allTimePeriod.second
            else {
                lastMonthPeriod.second
            }

        Log.d("Rewards", "getPeriodByDuration: $startDate $endDate")

        return Pair(startDate, endDate)
    }

    private val allTimePeriod: Pair<String, String>
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2000)
            val startDate = format.format(calendar.time)

            val endDate = format.format(Calendar.getInstance().time)

            return Pair(startDate, endDate)
        }

    private val dayPeriod: Pair<String, String>
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.SECOND, 1)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.HOUR, 0)
            //calendar.add(Calendar.DAY_OF_YEAR, -1)
            val startDate = format.format(calendar.time)

            val endDate = format.format(Calendar.getInstance().time)

            return Pair(startDate, endDate)
        }

    private val thisMonthPeriod: Pair<String, String>
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.SECOND, 1)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            val startDate = format.format(calendar.time)

            val endDate = format.format(Calendar.getInstance().time)

            return Pair(startDate, endDate)
        }

    private val lastMonthPeriod: Pair<String, String>
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val calendarS = Calendar.getInstance().apply {
                this.set(Calendar.SECOND, 1)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.HOUR_OF_DAY, 0)
                this.set(Calendar.DAY_OF_MONTH, 1)
                this.add(Calendar.MONTH, -1)
            }

            val startDate = format.format(calendarS.time)

            val calendarE = Calendar.getInstance().apply {
                this.set(Calendar.SECOND, 59)
                this.set(Calendar.MINUTE, 59)
                this.set(Calendar.HOUR_OF_DAY, 23)
                this.set(Calendar.DAY_OF_MONTH, 1)
                this.add(Calendar.DAY_OF_MONTH, -1)
            }

            val endDate = format.format(calendarE.time)

            return Pair(startDate, endDate)
        }
}