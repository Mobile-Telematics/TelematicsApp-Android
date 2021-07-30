package com.telematics.domain.model.tracking

class TripDetailsData(

    var id: String? = null,
    var addressStartParts: TripDetailsAddressesData? = null,
    var addressFinishParts: TripDetailsAddressesData? = null,
    var cityStart: String? = null,
    var cityFinish: String? = null,
    var startAddress: String? = null,
    var endAddress: String? = null,

    var startDate: String? = null,
    var endDate: String? = null,
    var time: Int = 0,
    var distance: Float = 0f,

    var rating: Int = 0,
    var ratingAcceleration: Int = 0,
    var ratingBraking: Int = 0,
    var ratingCornering: Int = 0,
    var ratingPhoneUsage: Int = 0,
    var ratingSpeeding: Int = 0,
    var ratingTimeOfDay: Int = 0,

    var accelerationCount: Int = 0,
    var brakingCount: Int = 0,
    var speedCount: Float = 0f,
    var phoneCount: Float = 0f,
    var phoneUse: Double = 0.0,

    var points: List<TripPointData>? = null,
    var isOriginChanged: Boolean = false,
    var type: TripData.TripType? = null,
    var tripTips: String? = null,

    var shareType: ShareType? = ShareType.SHARED

) {

    enum class ShareType {
        SHARED,
        NOT_SHARED
    }

}