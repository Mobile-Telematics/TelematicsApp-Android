package com.telematics.zenroad.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.domain.repository.SessionRepo
import com.telematics.domain.repository.SettingsRepo
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val trackingUseCase: TrackingUseCase,
    private val sessionRepo: SessionRepo,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    fun logout(): LiveData<Result<Boolean>> {

        val logoutState = MutableLiveData<Result<Boolean>>()
        loginUseCase.logout()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(logoutState)
            .launchIn(viewModelScope)

        trackingUseCase.logout()
        sessionRepo.clearStateForRewardInviteScreen()

        return logoutState
    }

    fun getTrackingState(): LiveData<Result<TrackingState>> {

        val state = MutableLiveData<Result<TrackingState>>()
        flow {
            val data = settingsRepo.getTrackingState()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)

        return state
    }
}