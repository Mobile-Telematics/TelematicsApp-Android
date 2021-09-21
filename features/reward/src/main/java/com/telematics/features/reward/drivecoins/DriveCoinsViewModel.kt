package com.telematics.features.reward.drivecoins

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.domain.model.reward.DailyLimitData
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.domain.model.reward.DriveCoinsDuration
import com.telematics.domain.model.reward.DriveCoinsTotalData
import com.telematics.domain.repository.RewardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject
import kotlin.math.roundToInt

class DriveCoinsViewModel @Inject constructor(
    private val rewardRepo: RewardRepo,
    val measuresFormatter: MeasuresFormatter
) : ViewModel() {

    fun getTotalCoinsByDuration(duration: DriveCoinsDuration): LiveData<Result<DriveCoinsTotalData>> {

        val totalCoinsDataState = MutableLiveData<Result<DriveCoinsTotalData>>()
        flow {
            val data = rewardRepo.getTotalCoinsByDuration(duration)
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(totalCoinsDataState)
            .launchIn(viewModelScope)
        return totalCoinsDataState
    }

    fun getDailyLimit(): LiveData<Result<DailyLimitData>> {

        val state = MutableLiveData<Result<DailyLimitData>>()
        flow {
            val data = rewardRepo.getDailyLimit()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun getDaily(): LiveData<Result<List<Pair<Int, Int>>>> {

        val state = MutableLiveData<Result<List<Pair<Int, Int>>>>()
        flow {
            val data = rewardRepo.getDaily()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun getDetailedByDuration(duration: DriveCoinsDuration): LiveData<Result<DriveCoinsDetailedData>> {

        val state = MutableLiveData<Result<DriveCoinsDetailedData>>()
        flow {
            val data = rewardRepo.getDetailed(duration)
            data.travelingTotalSpeedingKm =
                measuresFormatter.getDistanceByKm(data.travelingTotalSpeedingKm).roundToInt()
            data.travelingMileageData =
                measuresFormatter.getDistanceByKm(data.travelingMileageData).roundToInt()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }
}