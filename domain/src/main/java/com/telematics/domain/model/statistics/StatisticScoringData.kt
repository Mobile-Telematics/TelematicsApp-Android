package com.telematics.domain.model.statistics

data class StatisticScoringData(
    val drivingDetailsData: List<ScoreTypeModel> = ScoreTypeModel.empty(),
    val userStatisticsIndividualData: UserStatisticsIndividualData = UserStatisticsIndividualData(),
    val userStatisticsScoreData: List<ScoreTypeModel> = ScoreTypeModel.empty()
)