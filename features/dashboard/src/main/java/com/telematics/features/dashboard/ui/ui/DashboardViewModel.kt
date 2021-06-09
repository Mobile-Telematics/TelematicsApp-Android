package com.telematics.features.dashboard.ui.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveData
import com.telematics.data.mappers.toScoreTypeModelList
import com.telematics.data.utils.Resource
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.statistics.*
import com.telematics.domain.repository.StatisticRepo
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import java.util.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val dashboardRepo: StatisticRepo
) : ViewModel() {

    private val sessionData = MutableLiveData<Resource<SessionData>>()
    val driveCoinsData = MutableLiveData<Resource<DriveCoins>>()
    val userIndividualStatisticsData = MutableLiveData<Resource<UserStatisticsIndividualData>>()
    val scoreLiveData = MutableLiveData<Resource<StatisticScoringData>>()
    val mainEcoScoringLiveData = MutableLiveData<Resource<StatisticEcoScoringMain>>()
    val tableEcoScoringLiveData = MutableLiveData<Resource<StatisticEcoScoringTabsData>>()

    fun getDriveCoins() {

        flow {
            emit(dashboardRepo.getDriveCoins())
        }
            .setLiveData(driveCoinsData)
            .launchIn(viewModelScope)
    }

    fun getUserIndividualStatistics() {

        flow {
            emit(dashboardRepo.getUserStatisticsIndividualData())
        }
            .setLiveData(userIndividualStatisticsData)
            .launchIn(viewModelScope)
    }

    fun getStatistics() {

        flow {
            val list = dashboardRepo.getDrivingDetails()
            val userStatisticsIndividualData = dashboardRepo.getUserStatisticsIndividualData()
            val userStatisticsScoreData = dashboardRepo.getUserStatisticsScoreData()
            val scoreTypeModelChart = list.toScoreTypeModelList()
            val scoreTypeModelNumbers = userStatisticsScoreData.toScoreTypeModelList()
            val scoreData = StatisticScoringData(
                scoreTypeModelChart,
                userStatisticsIndividualData,
                scoreTypeModelNumbers
            )
            emit(scoreData)
        }
            .setLiveData(scoreLiveData)
            .launchIn(viewModelScope)


    }

    fun getMainEcoScoring() {

        flow {
            emit(dashboardRepo.getMainEcoScoring())
        }
            .setLiveData(mainEcoScoringLiveData)
            .launchIn(viewModelScope)
    }

    fun getEcoScoringTable() {

        flow {
            val weekData = dashboardRepo.getEcoScoringStatisticsData(Calendar.DAY_OF_WEEK)
            val monthData = dashboardRepo.getEcoScoringStatisticsData(Calendar.MONTH)
            val yearData = dashboardRepo.getEcoScoringStatisticsData(Calendar.YEAR)
            emit(StatisticEcoScoringTabsData(weekData, monthData, yearData))
        }
            .setLiveData(tableEcoScoringLiveData)
            .launchIn(viewModelScope)
    }


    fun getLastTrip() {

    }
}