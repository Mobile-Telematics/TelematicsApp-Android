package com.telematics.domain.repository

import android.content.Context
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure

interface SettingsRepo {

    fun getTelematicsLink(context: Context): String

    fun getDateMeasure(): DateMeasure
    fun getDistanceMeasure(): DistanceMeasure
    fun getTimeMeasure(): TimeMeasure

    fun setDateMeasure(dateMeasure: DateMeasure)
    fun setDistanceMeasure(distanceMeasure: DistanceMeasure)
    fun setTimeMeasure(timeMeasure: TimeMeasure)
}