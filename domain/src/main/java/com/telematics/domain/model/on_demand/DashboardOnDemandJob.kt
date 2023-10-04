package com.telematics.domain.model.on_demand

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DashboardOnDemandJob")
data class DashboardOnDemandJob(
    val name: String,
    val state: String,
    val createTime: Long //use for sort by date
) {

    @PrimaryKey(autoGenerate = true)
    var key: Int = 0

    val getTag: String
        get() {
            return name
        }

    val getOriginName: String
        get() {
            return createTime.toString()
        }

    val getName: String
        get() {
            return name
        }
}