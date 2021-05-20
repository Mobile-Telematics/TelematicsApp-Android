package com.telematics.data_.api

import com.telematics.data_.model.dashboard.DrivingDetailsRest
import com.telematics.data_.model.dashboard.EcoScoringRest
import com.telematics.data_.model.dashboard.UserStatisticsIndividualRest
import com.telematics.data_.model.dashboard.UserStatisticsScoreRest
import com.telematics.data_.model.rest.ApiResponse
import io.reactivex.Single
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