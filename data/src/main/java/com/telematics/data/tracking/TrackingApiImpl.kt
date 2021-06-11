package com.telematics.data.tracking

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.raxeltelematics.v2.sdk.TrackingApi
import com.raxeltelematics.v2.sdk.utils.permissions.PermissionsWizardActivity
import com.telematics.domain.repository.TrackingApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@SuppressLint("MissingPermission")
class TrackingApiImpl : TrackingApiRepo {

    private val TAG = "TrackingApiImpl"

    private val trackingApi = TrackingApi.getInstance()

    override fun setContext(context: Context) {
        TrackingApi.getInstance().initialize(context)
    }

    override fun setDeviceToken(deviceId: String) {
        Log.d(TAG, "setDeviceToken: deviceId $deviceId")
        trackingApi.setDeviceID(deviceId)
    }

    override fun checkPermissionAndStartWizard(activity: Activity) {

        if (!trackingApi.isAllRequiredPermissionsAndSensorsGranted()) {
            activity.startActivityForResult(
                PermissionsWizardActivity.getStartWizardIntent(
                    activity,
                    enableAggressivePermissionsWizard = false,
                    enableAggressivePermissionsWizardPage = true
                ), PermissionsWizardActivity.WIZARD_PERMISSIONS_CODE
            )
        }
    }

    override fun checkPermissions(): Flow<Boolean> {

        return flow {
            val data = trackingApi.isAllRequiredPermissionsGranted()
            emit(data)
        }
    }

    override fun startTracking() {

        trackingApi.setEnableSdk(true)
        trackingApi.startTracking()
    }

    override fun setEnableTrackingSDK(enable: Boolean) {

        trackingApi.setEnableSdk(enable)
    }

    override fun setIntentForNotification(intent: Intent) {

        TrackingApi.getInstance().setIntentForNotification(intent)
    }

    override fun logout() {
        trackingApi.setEnableSdk(false)
        trackingApi.clearDeviceID()
        trackingApi.setEnableSdk(false)
    }


}