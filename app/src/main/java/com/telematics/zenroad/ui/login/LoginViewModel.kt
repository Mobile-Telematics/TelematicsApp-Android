package com.telematics.zenroad.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.authentication.PhoneAuthCallback
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private var loginUseCase: LoginUseCase
) : ViewModel() {

    private val TAG = "LoginViewModel"

    fun authorize(
        login: String,
        password: String
    ): LiveData<Result<Boolean>> {

        val authorizeState = MutableLiveData<Result<Boolean>>()

        loginUseCase.authorize(login, password)
            .setLiveDataForResult(authorizeState)
            .launchIn(viewModelScope)

        return authorizeState
    }

    fun authorizeWithPhone(
        phone: String,
        activity: Activity,
        callback: PhoneAuthCallback
    ): LiveData<Result<Boolean>> {

        val authorizeState = MutableLiveData<Result<Boolean>>()

        loginUseCase.authorize(phone, activity, callback)
            .setLiveDataForResult(authorizeState)
            .launchIn(viewModelScope)

        return authorizeState
    }

    fun registration(
        login: String,
        password: String,
        loginType: LoginType
    ): LiveData<Result<Boolean>> {

        val registrationState = MutableLiveData<Result<Boolean>>()
        loginUseCase.registration(login, password)
            .setLiveDataForResult(registrationState)
            .launchIn(viewModelScope)
        return registrationState
    }
}