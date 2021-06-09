package com.telematics.domain.model.statistics

data class UserStatisticsIndividualData(
    val tripsCount: Int = 0,
    val mileageKm: Double = .0,
    val mileageMile: Double = .0,
    val drivingTime: Int = 0
)