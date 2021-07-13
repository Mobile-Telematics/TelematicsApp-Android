package com.telematics.zenroad

import androidx.lifecycle.ViewModel
import com.telematics.data.tracking.TrackingUseCase
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase
) : ViewModel() {

    fun allPermissionsGranted(){

        trackingUseCase.enableTrackingSDK()
    }
}