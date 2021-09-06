package com.telematics.domain.model.reward

import java.io.Serializable

data class DriveCoinsDetailedData(
        var travellingSum: Int = 0,
        var travelingMileage: Int = 0,
        var travelingTimeDriven: Int = 0,
        var travelingAccelerations: Int = 0,
        var travelingBrakings: Int = 0,
        var travelingCornerings: Int = 0,
        var travelingPhoneUsage: Int = 0,
        var travelingSpeeding: Int = 0,

        var travelingMileageData: Int = 0,
        var travelingTimeDrivenData: Int = 0,
        var travelingAccelerationCount: Int = 0,
        var travelingBrakingCount: Int = 0,
        var travelingCorneringCount: Int = 0,
        var travelingTotalSpeedingKm: Int = 0,
        var travelingDrivingTime: Int = 0,


        var safeDrivingSum: Int = 0,
        var safeDrivingCoinsTotal: Int = 0,

        var ecoDrivingSum: Int = 0,
        var ecoDrivingEcoScore: Int = 0,
        var ecoDrivingBrakes: Int = 0,
        var ecoDrivingFuel: Int = 0,
        var ecoDrivingTires: Int = 0,
        var ecoDrivingCostOfOwnership: Int = 0,

        var ecoScore: Int = 0,
        var ecoScoreBrakes: Int = 0,
        var ecoScoreFuel: Int = 0,
        var ecoScoreTyres: Int = 0,
        var ecoScoreCostOfOwnership: Int = 0
) : Serializable