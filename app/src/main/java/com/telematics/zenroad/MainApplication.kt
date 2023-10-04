package com.telematics.zenroad

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.telematics.data.extentions.isExactAlarmGranted
import com.telematics.data.tracking.TrackingUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var trackingUseCase: TrackingUseCase

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (isExactAlarmGranted(this)) {
            trackingUseCase.initializeSdk()
        }
    }
}