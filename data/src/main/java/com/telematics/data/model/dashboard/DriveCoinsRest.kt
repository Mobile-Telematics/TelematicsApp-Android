package com.telematics.data.model.dashboard

import com.google.gson.annotations.SerializedName

data class DriveCoinsRest(
    @SerializedName("TotalCoins") val totalCoins: Int
)