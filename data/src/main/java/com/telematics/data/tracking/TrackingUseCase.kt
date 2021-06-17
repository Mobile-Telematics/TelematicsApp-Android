package com.telematics.data.tracking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.telematics.data.utils.ImageLoader
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.repository.TrackingApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TrackingUseCase
@Inject constructor(
    context: Context,
    private val trackingApiRepo: TrackingApiRepo,
    private val imageLoader: ImageLoader
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

    fun setIntentForNotification(intent: Intent) {

        trackingApiRepo.setIntentForNotification(intent)
    }

    fun logout() {
        trackingApiRepo.logout()
    }


    fun getLastTrip(): Flow<TripData?> {

        return flow {
            val data = trackingApiRepo.getLastTrack()
            emit(data)
        }
    }

    fun getTripImage(token: String): Flow<Bitmap?> {

        return flow {
            val data = trackingApiRepo.getTrackImageHolder(token) ?: return@flow emit(null)
            val bitmap = imageLoader.loadImage(data.url, data.r, token)
            emit(bitmap)
        }
    }
}