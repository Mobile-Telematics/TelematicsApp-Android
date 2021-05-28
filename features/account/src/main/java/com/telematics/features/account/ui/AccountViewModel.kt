package com.telematics.features.account.ui

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.domain_.listener.AuthenticationListener
import com.telematics.domain_.listener.UserUpdatedListener
import com.telematics.domain_.model.LoginType
import com.telematics.domain_.model.authentication.User
import com.telematics.domain_.repository.AuthenticationRepo
import com.telematics.features.account.model.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val authenticationRepo: AuthenticationRepo
) : ViewModel() {

    private val TAG = "AccountViewModel"

    fun setListener(
        authenticationListener: AuthenticationListener?,
        userUpdatedListener: UserUpdatedListener?
    ) {
        Log.d(TAG, "setListener")
        authenticationListener?.let {
            authenticationRepo.setListener(it)
        }
        userUpdatedListener?.let {
            authenticationRepo.setListener(it)
        }
    }

    fun login(login: String, password: String?, loginType: LoginType, activity: Activity) {
        Log.d(TAG, "setListener")
        when (loginType) {
            LoginType.EMAIL -> authenticationRepo.signInEmailPassword(login, password.orEmpty())
            LoginType.PHONE -> {
                authenticationRepo.signInPhone(login, activity)
            }
        }
    }

    fun setVerifyCode(code: String?) {
        code?.let {
            authenticationRepo.checkPhoneVerificationCode(code)
        }
    }

    fun registration(login: String, password: String, loginType: LoginType) {
        Log.d(TAG, "registration")
        when (loginType) {
            LoginType.EMAIL -> authenticationRepo.registrationUser(login, password)
            LoginType.PHONE -> {
                authenticationRepo.registrationUser(login, null)
            }
        }
    }

    fun logout() {
        Log.d(TAG, "logout")
        authenticationRepo.logout()
    }

    fun updateUser(user: User) {
        authenticationRepo.updateUser(user)
    }

    fun tryLogin() {
        authenticationRepo.tryLogin()
    }

    private val mutableState = MutableStateFlow<Resource<User>>(Resource.Success(User()))

    init {
        viewModelScope.launch {
            mutableState.value = Resource.Success(authenticationRepo.getUser())
        }
    }

    val user: StateFlow<Resource<User>>
        get() = mutableState
}