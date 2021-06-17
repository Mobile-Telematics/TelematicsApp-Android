package com.telematics.zenroad.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun isSessionAvailable(): LiveData<Result<Boolean>> {
        val loginState = MutableLiveData<Result<Boolean>>()
        loginUseCase.isSessionAvailable()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(loginState)
            .launchIn(viewModelScope)
        return loginState
    }
}