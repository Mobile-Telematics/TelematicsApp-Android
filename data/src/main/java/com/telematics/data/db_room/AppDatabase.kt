package com.telematics.data.db_room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.telematics.domain.model.on_demand.DashboardOnDemandJob

@Database(entities = [DashboardOnDemandJob::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun onDemandDao(): OnDemandDao
}