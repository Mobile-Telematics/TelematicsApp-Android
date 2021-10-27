package com.telematics.data.api

import com.telematics.data.model.company_id.InstanceNameBody
import com.telematics.data.model.login.LoginBody
import com.telematics.data.model.login.LoginWithDeviceTokenBody
import com.telematics.data.model.login.RegistrationBody
import com.telematics.data.model.login.UserUpdateBody
import com.telematics.data.model.rest.ApiResponse
import com.telematics.data.model.rest.ApiResult
import com.telematics.data.model.user_exists.UserExists
import okhttp3.MultipartBody
import retrofit2.http.*

interface LoginApi {

    @GET("v1/Check/UserExists")
    suspend fun checkUserExists(
        @Query("Phone") phone: String? = null,
        @Query("Email") email: String? = null,
        @Query("ClientId") clientId: String? = null
    ): ApiResponse<UserExists>

    @POST("v1/Auth/Login")
    suspend fun login(@Body loginBody: LoginBody): ApiResponse<ApiResult>

    @POST("v1/Registration/create")
    suspend fun registration(
        @Body registrationBody: RegistrationBody
    ): ApiResponse<ApiResult>

    @POST("v1/Auth/Login")
    suspend fun loginWithDeviceToken(@Body loginBodyWithDeviceToken: LoginWithDeviceTokenBody): ApiResponse<ApiResult>

    @PUT("v1/Management/Users")
    suspend fun updateUser(@Body body: UserUpdateBody): ApiResponse<ApiResult>

    @Multipart
    @POST("v1/Management/users/images/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): ApiResponse<String?>

    @POST("/v1/Management/users/instances/change/{companyId}")
    suspend fun sendCompanyId(@Path("companyId") companyId: String): ApiResponse<InstanceNameBody>
}