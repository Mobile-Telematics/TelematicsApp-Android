package com.telematics.zenroad.di

import android.content.Context
import com.telematics.data.db_room.AppDatabase
import com.telematics.data.db_room.OnDemandDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase = AppDatabase(appContext)

    @Provides
    @Singleton
    fun provideOnDemandDao(appDatabase: AppDatabase): OnDemandDao = appDatabase.onDemandDao()
}