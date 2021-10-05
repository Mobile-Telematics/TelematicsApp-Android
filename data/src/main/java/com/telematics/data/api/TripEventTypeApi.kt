package com.telematics.data.api

import com.telematics.data.model.tracking.ChangeEventBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TripEventTypeApi {

    @POST("events/reportwrongeventtype/v1/{trackToken}")
    suspend fun changeEvent(
        @Header("DeviceToken") deviceToken: String,
        @Path("trackToken") trackToken: String,
        @Body body: ChangeEventBody
    )

    @POST("track/{trackId}/setdeleted/v1")
    suspend fun setDeleted(
        @Path("trackId") trackId: String,
        @Header("DeviceToken") deviceToken: String)
}