package com.telematics.domain_.model.dashboard

data class UserStatisticsIndividualData(
    val tripsCount: Int = 0,
    val mileageKm: Double = .0,
    val mileageMile: Double = .0,
    val drivingTime: Int = 0
)