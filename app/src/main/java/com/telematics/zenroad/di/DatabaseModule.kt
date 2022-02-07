package com.telematics.zenroad.di

import com.telematics.data.db_room.AppDatabase
import com.telematics.data.db_room.OnDemandDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideOnDemandDao(appDatabase: AppDatabase): OnDemandDao {
        return appDatabase.onDemandDao()
    }
}