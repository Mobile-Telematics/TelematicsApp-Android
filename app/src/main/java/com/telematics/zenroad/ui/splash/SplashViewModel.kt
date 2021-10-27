package com.telematics.zenroad.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.repository.UserRepo
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userRepo: UserRepo
) : ViewModel() {

    fun isSessionAvailable(): LiveData<Result<Boolean>> {
        val loginState = MutableLiveData<Result<Boolean>>()
        loginUseCase.isSessionAvailable()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(loginState)
            .launchIn(viewModelScope)
        return loginState
    }

    fun needOpenOnboarding(): LiveData<Result<Boolean>> {

        val needState = MutableLiveData<Result<Boolean>>()

        flow {
            delay(2000)
            val data = userRepo.needOnboarding()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(needState)
            .launchIn(viewModelScope)

        return needState
    }
}