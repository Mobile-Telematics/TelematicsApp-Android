package com.telematics.features.account.ui

import androidx.lifecycle.ViewModel
import com.telematics.domain_.usecase.LoginUseCase
import javax.inject.Inject

class AccountViewModel @Inject constructor(private val loginUseCase: LoginUseCase) :
    ViewModel() {

    suspend fun logout() {
        loginUseCase.logout()
    }
}