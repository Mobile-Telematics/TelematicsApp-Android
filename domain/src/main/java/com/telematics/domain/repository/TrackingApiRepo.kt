package com.telematics.domain.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.telematics.domain.model.tracking.ChangeTripEvent
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.model.tracking.TripDetailsData
import com.telematics.domain.model.tracking.TripImageHolder
import kotlinx.coroutines.flow.Flow

interface TrackingApiRepo {

    /*/ handle sdk*/
    fun setContext(context: Context)
    fun setDeviceToken(deviceId: String)

    fun checkPermissions(): Flow<Boolean>
    fun checkPermissionAndStartWizard(activity: Activity)

    fun startTracking()
    fun setEnableTrackingSDK(enable: Boolean)

    fun setIntentForNotification(intent: Intent)

    fun logout()

    /** handle tracks */
    suspend fun getLastTrack(): TripData?
    suspend fun getTrackImageHolder(trackId: String): TripImageHolder?
    suspend fun getTrips(offset: Int, limit: Int): List<TripData>

    suspend fun getTripDetails(tripId: String): TripDetailsData?
    suspend fun changeTripType(tripId: String, tripType: TripData.TripType): Boolean
    suspend fun changeTripEvent(tripId: String, data: ChangeTripEvent): Boolean

    suspend fun hideTrip(tripId: String)
    suspend fun setStatusDelete(tripId: String)
}