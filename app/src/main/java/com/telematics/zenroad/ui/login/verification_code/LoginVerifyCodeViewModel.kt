package com.telematics.zenroad.ui.login.verification_code

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.authentication.PhoneAuthCallback
import com.telematics.domain.model.authentication.PhoneAuthCred
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class LoginVerifyCodeViewModel @Inject constructor(
    private var loginUseCase: LoginUseCase
) : ViewModel() {


    fun authorise(phone: String, activity: Activity, callback: PhoneAuthCallback) {

        loginUseCase.authorize(phone, activity, callback)
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun authoriseWithCred(credential: PhoneAuthCred<*>) {
        loginUseCase.authorize(credential)
    }

    fun sendCode(phone: String, code: String, verificationId: String): LiveData<Result<Boolean>> {

        val sendCodeState = MutableLiveData<Result<Boolean>>()
        loginUseCase.sendVerifyCode(phone, code, verificationId)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(sendCodeState)
            .launchIn(viewModelScope)
        return sendCodeState
    }
}