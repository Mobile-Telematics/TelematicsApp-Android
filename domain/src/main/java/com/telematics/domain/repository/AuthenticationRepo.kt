package com.telematics.domain.repository

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepo {

    fun needPhoneVerification(): LiveData<Result<Boolean>>

    suspend fun signInEmailPassword(email: String, password: String)
    suspend fun signInPhone(phone: String, activity: Activity?)
    suspend fun login()

    fun getSessionData(): MutableLiveData<Result<SessionData>>

    suspend fun checkPhoneVerificationCode(code: String)
    suspend fun registrationUser(phone: String, password: String?)
    suspend fun logout()
    suspend fun updateUser(user: User)

    fun getUser(): MutableLiveData<Result<User>>
}