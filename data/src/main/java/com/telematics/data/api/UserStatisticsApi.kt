package com.telematics.data.api

import com.telematics.data.model.dashboard.DrivingDetailsRest
import com.telematics.data.model.dashboard.EcoScoringRest
import com.telematics.data.model.dashboard.UserStatisticsIndividualRest
import com.telematics.data.model.dashboard.UserStatisticsScoreRest
import com.telematics.data.model.rest.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UserStatisticsApi {

    @GET("Statistics/v1/Statistics/individual")
    suspend fun getIndividualData(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<UserStatisticsIndividualRest>

    @GET("Statistics/v1/Scorings/individual/daily")
    suspend fun getDrivingDetails(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<List<DrivingDetailsRest>>

    @GET("Statistics/v1/Scorings/individual")
    suspend fun getScoreData(
        @Header("DeviceToken") content_type: String,
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<UserStatisticsScoreRest>


    @GET("Statistics/v1/Scorings/individual/eco")
    suspend fun getMainEcoScoring(): ApiResponse<EcoScoringRest>
}