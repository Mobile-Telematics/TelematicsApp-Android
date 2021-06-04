package com.telematics.data.api

import com.telematics.data.model.dashboard.DriveCoinsRest
import com.telematics.data.model.rest.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DriveCoinsApi {

    @GET("v1/Coins/individual")
    suspend fun getDriveCoinsIndividual(
        @Query("DateFrom") startDate: String,
        @Query("DateTo") endDate: String
    ): ApiResponse<DriveCoinsRest>
}