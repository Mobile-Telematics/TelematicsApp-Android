package com.telematics.domain.repository

import android.content.Context

interface SettingsRepo {

    fun getTelematicsLink(context: Context): String
}