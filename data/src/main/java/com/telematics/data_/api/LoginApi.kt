package com.telematics.data_.api

import com.telematics.data_.model.login.LoginBody
import com.telematics.data_.model.rest.ApiResponse
import com.telematics.data_.model.rest.ApiResult
import com.telematics.data_.model.user_exists.UserExists
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {

    @GET("v1/Check/UserExists")
    fun checkUserExists(
        @Query("Phone") phone: String? = null,
        @Query("Email") email: String? = null,
        @Query("ClientId") clientId: String? = null
    ): Single<ApiResponse<UserExists>>

    @POST("v1/Auth/Login")
    suspend fun login(@Body loginBody: LoginBody): ApiResponse<ApiResult>
}

/*
AppId: d0a14a5d-4942-4e0c-8afc-5e8dd6bad424
InstanceId: c05391ba-488c-4f06-99c4-e677604a71d7
InstanceKey: 03dd661f-d0f4-431f-8392-e4e843209db9
*/