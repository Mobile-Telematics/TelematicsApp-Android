package com.telematics.features.account.use_case

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.User
import com.telematics.domain.repository.AuthenticationRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authenticationRepo: AuthenticationRepo
) {

    private val TAG = "AccountViewModel"

    suspend fun login(
        login: String,
        password: String?,
        loginType: LoginType,
        activity: Activity
    ) {
        Log.d(TAG, "setListener")

        return when (loginType) {
            LoginType.EMAIL -> authenticationRepo.signInEmailPassword(login, password.orEmpty())
            LoginType.PHONE -> authenticationRepo.signInPhone(login, activity)
        }
    }

    suspend fun login() {
        authenticationRepo.login()
    }

    fun getSessionData(): MutableLiveData<Result<SessionData>> {
        return authenticationRepo.getSessionData()
    }

    fun setVerifyCode(code: String?): Flow<Unit> {
        return flow {
            authenticationRepo.checkPhoneVerificationCode(code ?: "")
            emit(Unit)
        }
    }

    fun isNeedPhoneVerify(): LiveData<Result<Boolean>> {
        return authenticationRepo.needPhoneVerification()
    }

    fun registration(login: String, password: String, loginType: LoginType): Flow<Unit> {
        Log.d(TAG, "registration")

        return flow {
            when (loginType) {
                LoginType.EMAIL -> authenticationRepo.registrationUser(login, password)
                LoginType.PHONE -> authenticationRepo.registrationUser(login, null)
            }
            emit(Unit)
        }
    }

    suspend fun logout() {
        Log.d(TAG, "logout")
        authenticationRepo.logout()
    }

    fun getUser(): MutableLiveData<Result<User>> {
        return authenticationRepo.getUser()
    }

    fun updateUser(user: User): Flow<Unit> {
        return flow {
            emit(authenticationRepo.updateUser(user))
        }
    }
//
//    fun tryLogin() {
//        authenticationRepo.tryLogin()
//    }
}