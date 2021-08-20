package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class LeaderboardUserBody(
    @SerializedName("DeviceToken")
    val deviceToken: String?,
    @SerializedName("Distance")
    val distance: Double?,
    @SerializedName("Duration")
    val duration: Double?,
    @SerializedName("FirstName")
    val firstName: String?,
    @SerializedName("Image")
    val image: String?,
    @SerializedName("IsCurrentUser")
    val isCurrentUser: Boolean?,
    @SerializedName("LastName")
    val lastName: String?,
    @SerializedName("Nickname")
    val nickname: String?,
    @SerializedName("Place")
    val place: Int?,
    @SerializedName("Trips")
    val trips: Int?,
    @SerializedName("Value")
    val value: Double?,
    @SerializedName("ValuePerc")
    val valuePerc: Double?
)
