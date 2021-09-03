package com.telematics.data.model.reward

import com.google.gson.annotations.SerializedName

data class DailyLimit(
    @SerializedName("DailyLimit") val dailyLimit: Int
)