package com.telematics.zenroad.ui.main

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.repository.UserRepo
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    private val userRepo: UserRepo
) : ViewModel() {

    fun setDeviceTokenForTrackingApi() {
        flow {
            val deviceToken = userRepo.getDeviceToken()
            val value = trackingUseCase.setDeviceToken(deviceToken)
            emit(value)
        }.launchIn(viewModelScope)
    }

    fun checkPermissions(): LiveData<Result<Boolean>> {
        val permissionsState = MutableLiveData<Result<Boolean>>()
        trackingUseCase.checkPermissions()
            .setLiveDataForResult(permissionsState)
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun startWizard(activity: Activity) {
        trackingUseCase.startWizard(activity)
    }

    fun enableTracking() {
        trackingUseCase.enableTracking()
    }
}