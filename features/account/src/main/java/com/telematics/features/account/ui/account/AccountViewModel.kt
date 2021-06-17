package com.telematics.features.account.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val trackingUseCase: TrackingUseCase
) : ViewModel() {

    fun logout(): LiveData<Result<Boolean>> {

        val logoutState = MutableLiveData<Result<Boolean>>()
        loginUseCase.logout()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(logoutState)
            .launchIn(viewModelScope)

        trackingUseCase.logout()

        return logoutState
    }

    fun getUser(): LiveData<Result<User>> {

        val userState = MutableLiveData<Result<User>>()
        loginUseCase.getUser()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(userState)
            .launchIn(viewModelScope)
        return userState
    }
}