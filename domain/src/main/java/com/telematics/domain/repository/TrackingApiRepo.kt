package com.telematics.domain.repository

import android.app.Activity

interface TrackingApiRepo {
    fun setDeviceToken(deviceId: String)
    fun checkPermissionAndStartWizard(activity: Activity)
    fun onActivityResult(requestCode: Int, resultCode: Int)
}