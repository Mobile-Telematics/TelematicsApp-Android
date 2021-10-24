package com.telematics.features.feed.model


import androidx.annotation.DrawableRes
import com.telematics.feed.R

enum class AlertType constructor(
    val type: String,
    @param:DrawableRes @field:DrawableRes val drawableResId: Int,
    @param:DrawableRes @field:DrawableRes val editedDrawableResId: Int,
    @param:DrawableRes @field:DrawableRes val wrongDrawableResId: Int,
    @param:DrawableRes @field:DrawableRes val editedDialogResId: Int,
    @param:DrawableRes @field:DrawableRes val wrongDialogResId: Int
) {

    UNKNOWN("", 0, 0, 0, 0, 0),
    ACC(
        "acc",
        R.drawable.ic_gray___acceleration,
        R.drawable.ic_yellow___acceleration,
        R.drawable.ic_red_acceleration,
        R.drawable.ic_yellow_popup__acceleration,
        R.drawable.ic_yellow_popup__acceleration
    ),
    DEACC(
        "deacc",
        R.drawable.ic_gray___braking,
        R.drawable.ic_yellow___braking,
        R.drawable.ic_red_braking,
        R.drawable.ic_yellow_popup__deceleration,
        R.drawable.ic_yellow_popup__deceleration
    ),
    TURN(
        "turn",
        R.drawable.ic_gray___turn,
        R.drawable.ic_yellow___turn,
        R.drawable.ic_red___turn,
        R.drawable.ic_yellow_popup__cornering,
        R.drawable.ic_red_popup__cornering
    ),
    PHONE(
        "phone",
        R.drawable.ic_gray___phone,
        R.drawable.ic_yellow___phone,
        R.drawable.ic_red___phone,
        R.drawable.ic_yellow_popup__distraction,
        R.drawable.ic_red_popup__distraction
    );

    override fun toString(): String {
        return when (this) {
            UNKNOWN -> "Other"
            ACC -> "Acceleration"
            DEACC -> "Braking"
            TURN -> "Cornering"
            PHONE -> "PhoneUsage"
        }
    }

    fun getStringRes() = when (this) {
        DEACC -> R.string.trip_details_braking
        ACC -> R.string.trip_details_acceleration
        TURN -> R.string.trip_details_cornering
        UNKNOWN -> 0
        PHONE -> R.string.trip_details_phone_usage
    }


    companion object {

        fun from(type: String?): AlertType {
            for (alertType in values()) {
                if (alertType.type == type) {
                    return alertType
                }
            }
            return UNKNOWN
        }

        @DrawableRes
        fun getDrawableRes(type: String?): Int {
            return from(type).drawableResId
        }

    }

}
