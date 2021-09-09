package com.telematics.domain.repository

import com.telematics.domain.model.LoginType
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.authentication.IUser
import com.telematics.domain.model.company_id.InstanceName

interface UserServicesRepo {
    suspend fun login(login: String, password: String, loginType: LoginType): SessionData
    suspend fun logout()
    suspend fun loginWithDeviceToken(deviceToken: String): SessionData
    suspend fun registration(): RegistrationApiData
    suspend fun updateUser(user: IUser): SessionData
    suspend fun updateUserPicture(path: String)
    suspend fun changeCompanyId(companyId: String): InstanceName
}