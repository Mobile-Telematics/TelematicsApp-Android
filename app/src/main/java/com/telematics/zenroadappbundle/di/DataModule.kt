package com.telematics.zenroadappbundle.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.telematics.data_.repository.SessionRepo
import com.telematics.data_.repository.SessionRepoImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindSessionRepository(repository: SessionRepoImpl): SessionRepo
}