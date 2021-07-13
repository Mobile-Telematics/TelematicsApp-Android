package com.telematics.data.model.statistics


import com.google.gson.annotations.SerializedName

data class LeaderboardUserResponse(
		@SerializedName("AccelerationPerc")
		val accelerationPerc: Double?,
		@SerializedName("AccelerationPlace")
		val accelerationPlace: Int?,
		@SerializedName("AccelerationScore")
		val accelerationScore: Double?,
		@SerializedName("DecelerationPerc")
		val decelerationPerc: Double?,
		@SerializedName("DecelerationPlace")
		val decelerationPlace: Int?,
		@SerializedName("DecelerationScore")
		val decelerationScore: Double?,
		@SerializedName("DeviceToken")
		val deviceToken: String?,
		@SerializedName("Distance")
		val distance: Double?,
		@SerializedName("DistancePlace")
		val distancePlace: Int?,
		@SerializedName("DistractionPerc")
		val distractionPerc: Double?,
		@SerializedName("DistractionPlace")
		val distractionPlace: Int?,
		@SerializedName("DistractionScore")
		val distractionScore: Double?,
		@SerializedName("Duration")
		val duration: Double?,
		@SerializedName("DurationPlace")
		val durationPlace: Int?,
		@SerializedName("FirstName")
		val firstName: String?,
		@SerializedName("Image")
		val image: String?,
		@SerializedName("LastName")
		val lastName: String?,
		@SerializedName("Nickname")
		val nickname: String?,
		@SerializedName("Perc")
		val perc: Double?,
		@SerializedName("Place")
		val place: Int?,
		@SerializedName("Score")
		val score: Double?,
		@SerializedName("SpeedingPerc")
		val speedingPerc: Double?,
		@SerializedName("SpeedingPlace")
		val speedingPlace: Int?,
		@SerializedName("SpeedingScore")
		val speedingScore: Double?,
		@SerializedName("Trips")
		val trips: Int?,
		@SerializedName("TripsPlace")
		val tripsPlace: Int?,
		@SerializedName("TurnPerc")
		val turnPerc: Double?,
		@SerializedName("TurnPlace")
		val turnPlace: Int?,
		@SerializedName("TurnScore")
		val turnScore: Double?,
		@SerializedName("UsersNumber")
		val usersNumber: Int?
)