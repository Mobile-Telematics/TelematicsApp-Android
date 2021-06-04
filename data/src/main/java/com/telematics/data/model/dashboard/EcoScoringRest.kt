package com.telematics.data.model.dashboard

import com.google.gson.annotations.SerializedName

data class EcoScoringRest(
        @SerializedName("EcoScoringFuel")
        val fuel: Double,
        @SerializedName("EcoScoringTyres")
        val tyres: Double,
        @SerializedName("EcoScoringDepreciation")
        val depreciation: Double,
        @SerializedName("EcoScoringBrakes")
        val brakes: Double,
        @SerializedName("EcoScoring")
        val score: Double
)