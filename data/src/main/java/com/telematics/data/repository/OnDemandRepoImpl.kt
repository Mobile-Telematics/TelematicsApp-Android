package com.telematics.data.repository

import com.telematics.data.db_room.OnDemandDao
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandJobState
import com.telematics.domain.repository.OnDemandRepo
import javax.inject.Inject

class OnDemandRepoImpl @Inject constructor(
    private val onDemandDao: OnDemandDao
) : OnDemandRepo {

    private val dashboardOnDemandKey = "dashboardOnDemandKey"

    override suspend fun getOnDemandJobList(): List<DashboardOnDemandJob> {
        return onDemandDao.getOnDemandJobList()
    }

    override suspend fun putOnDemandJobList(list: List<DashboardOnDemandJob>) {
        onDemandDao.putOnDemandJobList(list)
    }

    override suspend fun removeOnDemandJobByState(state: OnDemandJobState) {
        onDemandDao.removeOnDemandJobByState(state.toString())
    }

    override suspend fun updateOnDemandJob(jobTagName: String, newState: String) {
        onDemandDao.updateOnDemandJob(jobTagName, newState)
    }

    override suspend fun addOnDemandJob(job: DashboardOnDemandJob) {
        onDemandDao.addOnDemandJob(job)
    }

    override fun removeOnDemandJob(job: DashboardOnDemandJob) {
        onDemandDao.removeOnDemandJob(job.getOriginName)
    }
}