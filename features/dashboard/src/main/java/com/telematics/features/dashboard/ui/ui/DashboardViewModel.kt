package com.telematics.features.dashboard.ui.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.telematics.data_.utils.Resource
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.usecase.LoginUseCase
import kotlinx.coroutines.flow.*
import androidx.lifecycle.viewModelScope
import com.telematics.data_.extentions.setLiveData
import com.telematics.data_.mappers.toScoreTypeModelList
import com.telematics.domain_.model.dashboard.*
import com.telematics.domain_.repository.DashboardRepo
import java.util.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val dashboardRepo: DashboardRepo
) : ViewModel() {

    private val sessionData = MutableLiveData<Resource<SessionData>>()
    val driveCoinsData = MutableLiveData<Resource<DriveCoins>>()
    val userIndividualStatisticsData = MutableLiveData<Resource<UserStatisticsIndividualData>>()
    val scoreLiveData = MutableLiveData<Resource<DashboardScoringData>>()
    val mainEcoScoringLiveData = MutableLiveData<Resource<DashboardEcoScoringMain>>()
    val tableEcoScoringLiveData = MutableLiveData<Resource<DashboardEcoScoringTabsData>>()

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
            val scoreData = DashboardScoringData(
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
            emit(DashboardEcoScoringTabsData(weekData, monthData, yearData))
        }
            .setLiveData(tableEcoScoringLiveData)
            .launchIn(viewModelScope)
    }


    fun getLastTrip() {

    }
}