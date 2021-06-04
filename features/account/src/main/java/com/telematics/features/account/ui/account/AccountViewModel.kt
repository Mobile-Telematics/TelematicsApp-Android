package com.telematics.features.account.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject
import kotlin.math.log

class AccountViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun logout() {
        flow {
            loginUseCase.logout()
            emit(Unit)
        }.launchIn(viewModelScope)
    }

    fun getUser(): LiveData<Result<User>>{

        return loginUseCase.getUser()
    }
}