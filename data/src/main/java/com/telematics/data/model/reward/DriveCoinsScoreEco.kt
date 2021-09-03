package com.telematics.data.model.reward

import com.google.gson.annotations.SerializedName

data class DriveCoinsScoreEco(
    @SerializedName("EcoScoreFuel") val ecoScoreFuel: Double,
    @SerializedName("EcoScoreTyres") val ecoScoreTyres: Double,
    @SerializedName("EcoScoreBrakes") val ecoScoreBrakes: Double,
    @SerializedName("EcoScoreDepreciation") val ecoScoreDepreciation: Double,
    @SerializedName("EcoScore") val ecoScore: Double
)