package com.telematics.data.tracking

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.raxeltelematics.v2.sdk.services.main.elm.BluetoothUtils
import com.raxeltelematics.v2.sdk.services.main.elm.Constants
import com.telematics.data.utils.ImageLoader
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.domain.model.tracking.ChangeTripEvent
import com.telematics.domain.model.tracking.ElmDevice
import com.telematics.domain.model.tracking.ElmManagerLinkingResult
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.model.tracking.TripDetailsData
import com.telematics.domain.repository.SettingsRepo
import com.telematics.domain.repository.TrackingApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TrackingUseCase
@Inject constructor(
    private val context: Context,
    private val trackingApiRepo: TrackingApiRepo,
    private val imageLoader: ImageLoader,
    private val settingsRepo: SettingsRepo
) {

    private var notificationIntent: Intent? = null

    fun initializeSdk() {
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

    fun enableTrackingSDK() {
        if (settingsRepo.getTrackingState() == TrackingState.AUTO)
            trackingApiRepo.setEnableTrackingSDK(true)
    }

    fun disableTrackingSDK() {
        trackingApiRepo.setEnableTrackingSDK(false)
    }

    fun startTracking() {
        trackingApiRepo.startTracking()
    }

    fun stopTracking() {
        trackingApiRepo.stopTracking()
    }

    fun setIntentForNotification(intent: Intent) {

        notificationIntent = intent
        trackingApiRepo.setIntentForNotification(intent)
    }

    fun enableTracking() {

        trackingApiRepo.setEnableTrackingSDK(true)
        notificationIntent?.let { notificationIntent ->
            trackingApiRepo.setIntentForNotification(notificationIntent)
        }
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
            emit(null)
        }
    }

    fun getTrips(offset: Int, limit: Int): Flow<List<TripData>> {

        return flow {
            val data = trackingApiRepo.getTrips(offset, limit)
            emit(data)
        }
    }

    fun getTripDetailsByPos(position: Int): Flow<TripDetailsData?> {
        return flow {
            val tripData = trackingApiRepo.getTrips(position, 1).firstOrNull()
            tripData?.id?.let { tripId ->
                val data = trackingApiRepo.getTripDetails(tripId)
                emit(data)
            } ?: run {
                emit(null)
            }
        }
    }

    fun changeTripType(tripId: String, toTripType: TripData.TripType): Flow<Boolean> {

        return flow {
            val data = trackingApiRepo.changeTripType(tripId, toTripType)
            emit(data)
        }
    }

    fun changeTripEvent(tripId: String, changeTripEvent: ChangeTripEvent): Flow<Boolean> {

        return flow {
            val data = trackingApiRepo.changeTripEvent(tripId, changeTripEvent)
            emit(data)
        }
    }

    fun hideTrip(tripId: String): Flow<Unit> {

        return flow {
            val data = trackingApiRepo.hideTrip(tripId)
            emit(data)
        }
    }

    fun setDeleteStatus(tripId: String): Flow<Unit> {

        return flow {
            val data = trackingApiRepo.setStatusDelete(tripId)
            emit(data)
        }
    }

    fun getLastSession(): Flow<Long> {

        return flow {
            val data = trackingApiRepo.getLastSession()
            emit(data)
        }
    }

    fun getBluetoothAdapter(context: Context): BluetoothAdapter? {

        return BluetoothUtils.getBluetoothAdapter(context)
    }

    fun getRequestBluetoothEnableCode() = Constants.REQUEST_BLUETOOTH_ENABLE_CODE

    fun getElmManagerLinkingResult(): Flow<ElmManagerLinkingResult?> {

        return flow {
            val data = trackingApiRepo.setElmManagerLinkingResult()
            emit(data)
        }
    }

    fun getElmDevice(): Flow<Unit> {

        return flow {
            emit(trackingApiRepo.getElmDevice())
        }
    }

    fun connectSelectedDevice(device: ElmDevice, token: String): Flow<Unit> {

        return flow {
            val data = trackingApiRepo.connectSelectedDevice(device, token)
            emit(data)
        }
    }

    fun removeFutureTrackTag(tag: String): Flow<Unit> {

        return flow {
            val data = trackingApiRepo.removeFutureTrackTag(tag)
            emit(data)
        }
    }

    fun addFutureTrackTag(tag: String): Flow<Unit> {

        return flow {
            val data = trackingApiRepo.addFutureTrackTag(tag)
            emit(data)
        }
    }
}