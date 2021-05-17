package com.telematics.zenroad.di

import com.telematics.domain_.usecase.LoginUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CoreDependencies {
    fun loginUseCase(): LoginUseCase
}