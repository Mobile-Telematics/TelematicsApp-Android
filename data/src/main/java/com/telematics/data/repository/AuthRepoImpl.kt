package com.telematics.data.repository

import android.util.Log
import com.telematics.data.BuildConfig
import com.telematics.data.api.LoginApi
import com.telematics.data.mappers.toRegistrationApiData
import com.telematics.data.mappers.toSessionData
import com.telematics.data.model.login.*
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.repository.UserServicesRepo
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val api: LoginApi
) : UserServicesRepo {

    override suspend fun login(login: String, password: String, loginType: LoginType): SessionData {
        val loginFields = when (loginType) {
            LoginType.EMAIL -> LoginFields(email = login)
            LoginType.PHONE -> LoginFields(phone = login)
        }
        val body = LoginBody(loginFields, password)
        val response = api.login(body)
        return response.result.toSessionData()
    }

    override suspend fun logout() {

    }

    override suspend fun loginWithDeviceToken(deviceToken: String): SessionData {

        val loginFieldsWithDeviceToken = LoginFieldsWithDeviceToken(deviceToken)
        val body = LoginWithDeviceTokenBody(loginFieldsWithDeviceToken, BuildConfig.INSTANCE_KEY)
        val response = api.loginWithDeviceToken(body)
        return response.result.toSessionData()
    }


    override suspend fun registration(): RegistrationApiData {
        val registrationBody = RegistrationBody()
        val response = api.registration(registrationBody)
        Log.d("API_API", "registration: response${response}")
        val r = response.result.toRegistrationApiData()
        Log.d("API_API", "registration: toRegistrationApiData ${r}")
        return r
    }
}