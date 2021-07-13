package com.telematics.zenroad.ui.login

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

class LoginViewModel @Inject constructor(
    private var loginUseCase: LoginUseCase
) : ViewModel() {

    fun authorize(
        login: String,
        password: String
    ): LiveData<Result<Boolean>> {

        val authorizeState = MutableLiveData<Result<Boolean>>()

        loginUseCase.authorize(login, password)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(authorizeState)
            .launchIn(viewModelScope)

        return authorizeState
    }
}