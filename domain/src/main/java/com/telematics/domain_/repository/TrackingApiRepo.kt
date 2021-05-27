package com.telematics.domain_.repository

import android.app.Activity

interface TrackingApiRepo {
    fun setDeviceToken(deviceId: String)
    fun checkPermissionAndStartWizard(activity: Activity)
    fun onActivityResult(requestCode: Int, resultCode: Int)
}