package com.telematics.zenroad.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data_.utils.Resource
import com.telematics.domain_.model.LoginType
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.usecase.LoginUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {
    val loginState = MutableLiveData<Resource<SessionData>>()

    fun login(login: String, password: String, loginType: LoginType) {
        loginUseCase.runLogin(login, password, loginType)
            .onStart {
                loginState.postValue(Resource.Loading())
            }
            .onEach {
                com.telematics.features.dashboard.ui.ui.deviceToken = it.deviceToken
                loginState.postValue(Resource.Success(it))
            }
            .catch { error ->
                Log.d("LoginViewModel", "${error.printStackTrace()}")
                loginState.postValue(Resource.Failure(error))
            }.launchIn(viewModelScope)
    }

    suspend fun getSessionData(): SessionData {
        return loginUseCase.getSessionData()
    }

    fun checkUserExists() {
        //todo check login
    }
}