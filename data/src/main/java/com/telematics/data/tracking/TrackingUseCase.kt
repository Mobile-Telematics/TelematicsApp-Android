package com.telematics.data.tracking

import android.app.Activity
import android.content.Context
import com.telematics.domain.repository.TrackingApiRepo
import com.telematics.domain.repository.UserRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackingUseCase
@Inject constructor(
    private val context: Context,
    private val userRepo: UserRepo,
    private val trackingApiRepo: TrackingApiRepo
) {

    init {
        trackingApiRepo.setContext(context)
        val deviceToken = userRepo.getDeviceToken()
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

    fun logout() {
        trackingApiRepo.logout()
    }
}