package com.telematics.zenroad

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.domain.model.SessionData
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun login(): LiveData<Result<SessionData>> {
        flow {
            loginUseCase.login()
            emit(Unit)
        }.launchIn(viewModelScope)
        return loginUseCase.getSessionData()
    }
}