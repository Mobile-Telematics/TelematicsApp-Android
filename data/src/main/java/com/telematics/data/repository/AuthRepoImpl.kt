package com.telematics.data.repository

import android.webkit.MimeTypeMap
import com.telematics.data.BuildConfig
import com.telematics.data.api.LoginApi
import com.telematics.data.mappers.toInstanceName
import com.telematics.data.mappers.toRegistrationApiData
import com.telematics.data.mappers.toSessionData
import com.telematics.data.model.login.*
import com.telematics.domain.model.LoginType
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.IUser
import com.telematics.domain.model.authentication.User
import com.telematics.domain.model.company_id.InstanceName
import com.telematics.domain.repository.UserServicesRepo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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
        return response.result.toRegistrationApiData()
    }

    override suspend fun updateUser(user: IUser): SessionData {

        val newUser = user as User
        val userUpdateBody = UserUpdateBody(
            email = newUser.email,
            phone = newUser.phone,
            address = newUser.address,
            birthday = newUser.birthday,
            childrenCount = newUser.childrenCount,
            firstName = newUser.firstName,
            lastName = newUser.lastName,
            maritalStatus = newUser.maritalStatus,
            imageUrl = newUser.profilePictureUrl,
            gender = newUser.gender,
            clientId = newUser.clientId
        )
        val response = api.updateUser(userUpdateBody)
        return response.result.toSessionData()
    }

    override suspend fun updateUserPicture(path: String) {

        val file = File(path)
        val type = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path))
        val requestFile = RequestBody.create(MediaType.parse(type), file)
        val body = MultipartBody.Part.createFormData("file", path, requestFile)
        api.uploadImage(body)
    }

    override suspend fun changeCompanyId(companyId: String): InstanceName {

        val response = api.sendCompanyId(companyId)
        return if (response.status == 200) {
            response.result.toInstanceName()
        } else InstanceName(null, false)
    }
}