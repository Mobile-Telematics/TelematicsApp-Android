package com.telematics.data.api

import com.telematics.data.model.rest.ApiResponse
import com.telematics.data.model.reward.DriveCoinsDetailed2
import com.telematics.data.model.reward.DriveCoinsScoreEco
import com.telematics.data.model.reward.StreaksRest
import com.telematics.data.model.statistics.DrivingDetailsRest
import com.telematics.data.model.statistics.EcoScoringRest
import com.telematics.data.model.statistics.UserStatisticsIndividualRest
import com.telematics.data.model.statistics.UserStatisticsScoreRest
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UserStatisticsApi {

    companion object {
        const val API_PATH = "indicators/v1"
    }

    @GET("$API_PATH/Scores/safety")
    suspend fun getScoreData(
        @Header("DeviceToken") content_type: String,
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<UserStatisticsScoreRest>

    @GET("$API_PATH/Statistics")
    suspend fun getIndividualData(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<UserStatisticsIndividualRest>

    @GET("$API_PATH/Scores/safety/daily")
    suspend fun getDrivingDetails(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<List<DrivingDetailsRest>>

    @GET("$API_PATH/Scores/eco")
    suspend fun getMainEcoScoring(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<EcoScoringRest>

    @GET("$API_PATH/Scores/eco")
    suspend fun getScore(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<DriveCoinsScoreEco>

    @GET("$API_PATH/Statistics")
    suspend fun getStatisticsData(
        @Query("StartDate") startDate: String,
        @Query("EndDate") endDate: String
    ): ApiResponse<DriveCoinsDetailed2>

    @GET("$API_PATH/Streaks")
    suspend fun getStreaks(): ApiResponse<StreaksRest>
}