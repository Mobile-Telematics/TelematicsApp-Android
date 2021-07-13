package com.telematics.zenroad.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.LoginType
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    fun registration(
        login: String,
        password: String,
        loginType: LoginType
    ): LiveData<Result<Boolean>> {

        val registrationState = MutableLiveData<Result<Boolean>>()
        loginUseCase.registration(login, password, loginType)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(registrationState)
            .launchIn(viewModelScope)
        return registrationState
    }
}