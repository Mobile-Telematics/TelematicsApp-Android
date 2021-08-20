package com.telematics.features.leaderboard.ui.leaderboard_summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.leaderboard.LeaderboardUserItems
import com.telematics.domain.repository.LeaderboardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class LeaderboardSummaryViewModel @Inject constructor(
    private val leaderboardRepo: LeaderboardRepo
) : ViewModel() {

    fun observeLeaderboardList(): LiveData<Result<List<LeaderboardUserItems>>> {

        val leaderboardListState = MutableLiveData<Result<List<LeaderboardUserItems>>>()
        flow {
            val data = leaderboardRepo.getLeaderboardUserData()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(leaderboardListState)
            .launchIn(viewModelScope)
        return leaderboardListState
    }
}