package com.telematics.domain.model.statistics

data class UserStatisticsScoreData(
    val overallScore: Int = 0,
    val accelerationScore: Int = 0,
    val brakingScore: Int = 0,
    val distractedScore: Int = 0,
    val speedingScore: Int = 0,
    val corneringScore: Int = 0
)