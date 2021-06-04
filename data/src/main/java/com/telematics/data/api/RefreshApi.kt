package com.telematics.data.api

import com.telematics.data.model.refresh_token.RefreshRequest
import com.telematics.data.model.rest.ApiResponse
import com.telematics.data.model.rest.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {

    // refresh token
    @POST("v1/Auth/RefreshToken")
    fun refreshToken(@Body refreshRequest: RefreshRequest): ApiResponse<ApiResult>
}