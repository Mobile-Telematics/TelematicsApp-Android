package com.telematics.domain_.model.dashboard

data class DashboardScoringData(
    val drivingDetailsData: List<ScoreTypeModel> = ScoreTypeModel.empty(),
    val userStatisticsIndividualData: UserStatisticsIndividualData = UserStatisticsIndividualData(),
    val userStatisticsScoreData: List<ScoreTypeModel> = ScoreTypeModel.empty()
)