package com.telematics.zenroad.ui.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.SessionData
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private var loginUseCase: LoginUseCase
) : ViewModel() {

    private val TAG = "LoginViewModel"

    fun login(
        username: String,
        password: String,
        loginType: LoginType,
        activity: Activity
    ): LiveData<Result<SessionData>> {

        flow {
            loginUseCase.login(username, password, loginType, activity)
            emit(Unit)
        }
            .launchIn(viewModelScope)

        return loginUseCase.getSessionData()
    }

    fun isNeedPhoneVerify(): LiveData<Result<Boolean>> {
        return loginUseCase.isNeedPhoneVerify()
    }

    fun registration(login: String, password: String, loginType: LoginType) {

        loginUseCase.registration(login, password, loginType).launchIn(viewModelScope)
    }
}