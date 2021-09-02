package com.telematics.data.api

import com.telematics.data.model.rest.ApiResponse
import com.telematics.data.model.statistics.DrivingDetailsRest
import com.telematics.data.model.statistics.EcoScoringRest
import com.telematics.data.model.statistics.UserStatisticsIndividualRest
import com.telematics.data.model.statistics.UserStatisticsScoreRest
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UserStatisticsApi {

    /* @GET("Statistics/v1/Statistics/individual")
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
     suspend fun getMainEcoScoring(): ApiResponse<EcoScoringRest>*/

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
}