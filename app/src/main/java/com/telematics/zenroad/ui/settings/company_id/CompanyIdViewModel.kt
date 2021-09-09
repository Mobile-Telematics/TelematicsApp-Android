package com.telematics.zenroad.ui.settings.company_id

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.company_id.InstanceName
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class CompanyIdViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun send(companyId: String): LiveData<Result<InstanceName>> {

        val logoutState = MutableLiveData<Result<InstanceName>>()
        loginUseCase.changeCompanyId(companyId)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(logoutState)
            .launchIn(viewModelScope)
        return logoutState
    }
}