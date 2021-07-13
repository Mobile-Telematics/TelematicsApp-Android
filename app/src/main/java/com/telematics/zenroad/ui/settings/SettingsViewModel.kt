package com.telematics.zenroad.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
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
}