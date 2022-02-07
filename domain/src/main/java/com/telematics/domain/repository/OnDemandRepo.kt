package com.telematics.domain.repository

import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandJobState

interface OnDemandRepo {

    suspend fun getOnDemandJobList(): List<DashboardOnDemandJob>

    suspend fun putOnDemandJobList(list: List<DashboardOnDemandJob>)
    suspend fun removeOnDemandJobByState(state: OnDemandJobState)
    suspend fun updateOnDemandJob(jobTagName: String, newState: String)
    suspend fun addOnDemandJob(job: DashboardOnDemandJob)
    fun removeOnDemandJob(job: DashboardOnDemandJob)
}