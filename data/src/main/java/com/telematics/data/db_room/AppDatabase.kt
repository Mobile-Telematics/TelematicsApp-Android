package com.telematics.data.db_room

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.telematics.data.BuildConfig
import com.telematics.domain.model.on_demand.DashboardOnDemandJob

@Database(entities = [DashboardOnDemandJob::class], version = BuildConfig.DB_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun onDemandDao(): OnDemandDao

    companion object {
        @VisibleForTesting
        val DATABASE_NAME = "telematics-app-db"

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        fun getDatabasePath(context: Context) = context.getDatabasePath(DATABASE_NAME).absolutePath
        fun getDatabaseFile(context: Context) = context.getDatabasePath(DATABASE_NAME)
        fun getDatabaseSize(context: Context) = context.getDatabasePath(DATABASE_NAME).length()

        private fun buildDatabase(context: Context) = Room
            .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(
//                MIGRATION_1_2,
            )
            .build()

        // Add database migration
        //@VisibleForTesting
        //val MIGRATION_1_2 = Migration1To2()
    }
}