package com.telematics.domain_.model.dashboard

data class DashboardEcoScoringMain(
    val score: Int = 0,
    val fuel: Int = 0,
    val tires: Int = 0,
    val brakes: Int = 0,
    val cost: Int = 0
)