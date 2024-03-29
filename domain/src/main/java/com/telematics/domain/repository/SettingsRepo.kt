package com.telematics.domain.repository

import android.content.Context
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandState
import com.telematics.domain.model.on_demand.TrackingState

interface SettingsRepo {

    fun getTelematicsLink(context: Context): String

    fun getDateMeasure(): DateMeasure
    fun getDistanceMeasure(): DistanceMeasure
    fun getTimeMeasure(): TimeMeasure

    fun setDateMeasure(dateMeasure: DateMeasure)
    fun setDistanceMeasure(distanceMeasure: DistanceMeasure)
    fun setTimeMeasure(timeMeasure: TimeMeasure)

    fun getTrackingState(): TrackingState
    fun setTrackingState(mode: TrackingState)
    fun getDemandDutyState(): OnDemandState
    fun setDemandDutyState(state: OnDemandState)
    fun setOnDemandLastCurrentJob(job: DashboardOnDemandJob)
    fun getOnDemandLastCurrentJob(): Long

    fun isNotificationPermissionCompleted(): Boolean
    fun setNotificationPermissionCompleted()
}