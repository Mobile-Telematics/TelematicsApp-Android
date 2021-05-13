package com.telematics.data_.repository

import com.telematics.data_.api.LoginApi
import com.telematics.data_.api.errors.ApiError
import com.telematics.data_.mappers.toSessionData
import com.telematics.data_.model.SessionData
import com.telematics.data_.model.login.LoginBody
import com.telematics.data_.model.login.LoginFields
import com.telematics.data_.model.login.LoginType
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val api: LoginApi
) : AuthRepo {

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
        //api.logout()
    }
}