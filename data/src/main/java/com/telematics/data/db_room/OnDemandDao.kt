package com.telematics.data.db_room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.domain.model.on_demand.DashboardOnDemandJob

@Dao
interface OnDemandDao {

    @Query("SELECT * FROM DashboardOnDemandJob")
    fun getOnDemandJobList(): List<DashboardOnDemandJob>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putOnDemandJobList(list: List<DashboardOnDemandJob>)

    @Query("DELETE FROM DashboardOnDemandJob WHERE state = :state")
    fun removeOnDemandJobByState(state: String)

    @Query("UPDATE DashboardOnDemandJob SET state = :newState WHERE createTime = :originJobName")
    fun updateOnDemandJob(originJobName: String, newState: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOnDemandJob(job: DashboardOnDemandJob)

    @Query("DELETE FROM DashboardOnDemandJob WHERE createTime = :originJobName")
    fun removeOnDemandJob(originJobName: String)
}