package com.telematics.telematics_api.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
//import com.raxeltelematics.v2.sdk.Settings
//import com.raxeltelematics.v2.sdk.TrackingApi
//import com.raxeltelematics.v2.sdk.utils.permissions.PermissionsWizardActivity
import com.telematics.domain_.repository.TrackingApiRepo

class TrackingApiImpl(context: Context) : TrackingApiRepo {

    companion object {
        //val trackingApi = TrackingApi.getInstance()
    }

    private var deviceId = ""

    init {
//        val settings = Settings(
//            stopTrackingTimeout = Settings.stopTrackingTimeHigh,
//            accuracy = Settings.accuracyHigh,
//            autoStartOn = true,
//            elmOn = false,
//            hfOn = true
//        )
//        val api = TrackingApi.getInstance()
//        api.initialize(context, settings)
    }

    override fun setDeviceToken(deviceId: String) {

        this.deviceId = deviceId
    }

    @SuppressLint("MissingPermission")
    override fun checkPermissionAndStartWizard(activity: Activity) {

//        if (trackingApi.isAllRequiredPermissionsAndSensorsGranted()) {
//            trackingApi.setDeviceID(deviceId)
//            trackingApi.setEnableSdk(true)
//        } else {
//            tryStartPermissionWizard(activity)
//        }
    }

    private fun tryStartPermissionWizard(activity: Activity) {
//        activity.startActivityForResult(
//            PermissionsWizardActivity.getStartWizardIntent(
//                activity,
//                enableAggressivePermissionsWizard = false,
//                enableAggressivePermissionsWizardPage = true
//            ), PermissionsWizardActivity.WIZARD_PERMISSIONS_CODE
//        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int) {
//        if (requestCode == PermissionsWizardActivity.WIZARD_PERMISSIONS_CODE) {
//            when (resultCode) {
//                PermissionsWizardActivity.WIZARD_RESULT_ALL_GRANTED -> {
//
//                }
//                PermissionsWizardActivity.WIZARD_RESULT_NOT_ALL_GRANTED -> {
//
//                }
//                PermissionsWizardActivity.WIZARD_RESULT_CANCELED -> {
//
//                }
//            }
//        }
    }
}