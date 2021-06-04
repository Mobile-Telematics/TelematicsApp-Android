package com.telematics.features.account.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun getUser(): LiveData<Result<User>> {
        return loginUseCase.getUser()
    }

    fun updateUser(user: User) {
        loginUseCase.updateUser(user)
            .launchIn(viewModelScope)
    }
}