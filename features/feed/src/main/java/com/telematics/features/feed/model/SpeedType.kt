package com.telematics.features.feed.model

import androidx.annotation.ColorRes
import com.telematics.feed.R

enum class SpeedType constructor(
    val type: String,
    @param:ColorRes @field:ColorRes val colorResId: Int
) {

    UNKNOWN("", R.color.colorSpeedTypeNormal),
    NORMAL("norm", R.color.colorSpeedTypeNormal),
    MID("mid", R.color.colorSpeedTypeMid),
    HIGH("high", R.color.colorSpeedTypeHigh);

    companion object {

        fun from(type: String?): SpeedType {
            for (speedType in values()) {
                if (speedType.type == type) {
                    return speedType
                }
            }
            return UNKNOWN
        }

        @ColorRes
        fun getColor(type: String?): Int {
            return from(type).colorResId
        }
    }

}