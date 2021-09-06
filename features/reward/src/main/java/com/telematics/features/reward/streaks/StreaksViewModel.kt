package com.telematics.features.reward.streaks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.reward.Streak
import com.telematics.domain.repository.RewardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class StreaksViewModel @Inject constructor(
    private val rewardRepo: RewardRepo
) : ViewModel() {

    fun getStreaksData(): LiveData<Result<List<Streak>>> {

        val streakState = MutableLiveData<Result<List<Streak>>>()
        flow {
            val data = rewardRepo.getStreaks()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(streakState)
            .launchIn(viewModelScope)
        return streakState
    }
}