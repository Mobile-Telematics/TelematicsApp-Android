package com.telematics.features.leaderboard.ui.leaderboard_details.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.repository.LeaderboardRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class LeaderboardDetailsPageViewModel @Inject constructor(
    private val leaderboardRepo: LeaderboardRepo
) : ViewModel() {

    fun getUserListByType(type: Int): LiveData<Result<List<LeaderboardMemberData>>> {

        val leaderboardUserListState = MutableLiveData<Result<List<LeaderboardMemberData>>>()
        flow {
            val data = leaderboardRepo.getLeaderboardUserList(type)
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(leaderboardUserListState)
            .launchIn(viewModelScope)

        return leaderboardUserListState
    }
}