package com.telematics.data.model.statistics

import com.google.gson.annotations.SerializedName

data class DriveCoinsRest(
    @SerializedName("TotalCoins") val totalCoins: Int
)