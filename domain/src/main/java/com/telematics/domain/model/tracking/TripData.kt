package com.telematics.domain.model.tracking

import java.io.Serializable
import java.util.*

data class TripData(

    var date: Date? = null,
    var dateMonthDay: String? = null,
    var timeStart: String? = null,
    var timeEnd: String? = null,
    var duration: Int = 0,    // in minute

    var dist: Float = 0f,
    var streetStart: String? = null,
    var streetEnd: String? = null,
    var cityStart: String? = null,
    var cityEnd: String? = null,
    var districtStart: String? = null,
    var districtEnd: String? = null,
    var id: String? = null,
    var rating: Double = 0.0,
    var isHideDate: Boolean = false,

    var type: TripType? = null,
    var isOriginChanged: Boolean = false,
    val tag: Tag = Tag(),
    var isDeleted: Boolean = false

) : Serializable {

    enum class TripType {
        DRIVER,
        PASSENGER,
        BUS,
        TAXI,
        TRAIN,
        BICYCLE,
        MOTORCYCLE,
        WALKING,
        RUNNING,
        OTHER;

        override fun toString(): String {
            return when (this) {
                DRIVER -> "OriginalDriver"
                PASSENGER -> "Passanger"
                BUS -> "Bus"
                TAXI -> "Taxi"
                TRAIN -> "Train"
                BICYCLE -> "Bicycle"
                MOTORCYCLE -> "Motorcycle"
                WALKING -> "Walking"
                RUNNING -> "Running"
                OTHER -> "Other"
            }
        }
    }

    enum class TagType {
        PERSONAL, BUSINESS;

        override fun toString(): String {
            return when (this) {
                PERSONAL -> "Personal"
                BUSINESS -> "Business"
            }
        }

        fun reverse(): TagType {
            return if (this.toString() == "Personal") BUSINESS else PERSONAL
        }

        companion object {
            fun parse(value: String?): TagType {
                return when (value?.toLowerCase(Locale.ROOT)) {
                    "business" -> BUSINESS
                    else -> PERSONAL
                }
            }
        }
    }

    data class Tag(var type: TagType = TagType.PERSONAL): Serializable
}