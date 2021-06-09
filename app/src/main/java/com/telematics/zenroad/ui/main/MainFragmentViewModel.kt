package com.telematics.zenroad.ui.main

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase
) : ViewModel() {

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