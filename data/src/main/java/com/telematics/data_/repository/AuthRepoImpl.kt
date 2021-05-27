package com.telematics.data_.repository

import com.telematics.data_.api.LoginApi
import com.telematics.data_.api.errors.ApiError
import com.telematics.data_.mappers.toSessionData
import com.telematics.data_.model.login.*
import com.telematics.domain_.BuildConfig
import com.telematics.domain_.model.LoginType
import com.telematics.domain_.model.SessionData
import com.telematics.domain_.repository.UserServicesRepo
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
        if (response.status != 200)
            throw ApiError(response.status, response.msg)
        return response.result!!.toSessionData()
    }

    override suspend fun logout() {

    }

    override suspend fun loginWithDeviceToken(deviceToken: String): SessionData {

        val loginFieldsWithDeviceToken = LoginFieldsWithDeviceToken(deviceToken)
        val body = LoginWithDeviceTokenBody(loginFieldsWithDeviceToken, BuildConfig.INSTANCE_KEY)
        val response = api.loginWithDeviceToken(body)
        if (response.status != 200)
            throw ApiError(response.status, response.msg)
        return response.result!!.toSessionData()
    }



    override suspend fun registrationWithEmail(email: String): SessionData {
        val registrationBody = RegistrationBody(email)
        val response = api.registrationWithEmail(registrationBody)
        if (response.status != 200)
            throw ApiError(response.status, response.msg)
        return response.result!!.toSessionData()
    }
}