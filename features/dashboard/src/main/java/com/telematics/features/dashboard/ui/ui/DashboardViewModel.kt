package com.telematics.features.dashboard.ui.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveData
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.mappers.toScoreTypeModelList
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.data.utils.Resource
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.statistics.*
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.repository.SettingsRepo
import com.telematics.domain.repository.StatisticRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import java.util.*
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val dashboardRepo: StatisticRepo,
    private val trackingUseCase: TrackingUseCase,
    private val settingsRepo: SettingsRepo,
) : ViewModel() {

    private val TAG = "DashboardViewModel"

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
            .flowOn(Dispatchers.IO)
            .setLiveData(driveCoinsData)
            .launchIn(viewModelScope)
    }

    fun getUserIndividualStatistics() {

        flow {
            val data = dashboardRepo.getUserStatisticsIndividualData()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
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
            .flowOn(Dispatchers.IO)
            .setLiveData(scoreLiveData)
            .launchIn(viewModelScope)


    }

    fun getMainEcoScoring() {

        flow {
            emit(dashboardRepo.getMainEcoScoring())
        }
            .flowOn(Dispatchers.IO)
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
            .flowOn(Dispatchers.IO)
            .setLiveData(tableEcoScoringLiveData)
            .launchIn(viewModelScope)
    }


    fun getLastTrip(): LiveData<Result<TripData?>> {

        val lastTripDataState = MutableLiveData<Result<TripData?>>()
        trackingUseCase.getLastTrip()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(lastTripDataState)
            .launchIn(viewModelScope)
        return lastTripDataState
    }

    fun getLastTripImage(token: String): LiveData<Result<Bitmap?>> {

        val lastTripImageState = MutableLiveData<Result<Bitmap?>>()
        trackingUseCase.getTripImage(token)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(lastTripImageState)
            .launchIn(viewModelScope)
        return lastTripImageState
    }

    fun getTelematicsLink(context: Context): String {
        return settingsRepo.getTelematicsLink(context)
    }
}