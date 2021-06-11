package com.telematics.data.tracking

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.telematics.domain.repository.TrackingApiRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackingUseCase
@Inject constructor(
    context: Context,
    private val trackingApiRepo: TrackingApiRepo
) {

    init {
        trackingApiRepo.setContext(context)
    }

    fun setDeviceToken(deviceToken: String) {

        if (deviceToken.isNotBlank())
            trackingApiRepo.setDeviceToken(deviceToken)
    }

    fun checkPermissions(): Flow<Boolean> {
        return trackingApiRepo.checkPermissions()
    }

    fun startWizard(activity: Activity) {
        trackingApiRepo.checkPermissionAndStartWizard(activity)
    }

    fun enableTracking() {
        trackingApiRepo.startTracking()
    }

    fun disableTrackingSDK() {
        trackingApiRepo.setEnableTrackingSDK(false)
    }

    fun setIntentForNotification(intent: Intent){

        trackingApiRepo.setIntentForNotification(intent)
    }

    fun logout() {
        trackingApiRepo.logout()
    }


}