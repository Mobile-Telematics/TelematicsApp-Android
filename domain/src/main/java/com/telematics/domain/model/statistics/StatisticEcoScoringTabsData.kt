package com.telematics.domain.model.statistics

data class StatisticEcoScoringTabsData(
    var week: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var month: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var year: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var inMiles: Boolean = false
) {

    fun toMiles() {

//        inMiles = true
//
//        week.averageSpeed = week.averageSpeed.toMiles()
//        week.maxSpeed = week.maxSpeed.toMiles()
//        week.averageTripDistance = week.averageTripDistance.toMiles()
//
//        month.averageSpeed = month.averageSpeed.toMiles()
//        month.maxSpeed = month.maxSpeed.toMiles()
//        month.averageTripDistance = month.averageTripDistance.toMiles()
//
//        year.averageSpeed = year.averageSpeed.toMiles()
//        year.maxSpeed = year.maxSpeed.toMiles()
//        year.averageTripDistance = year.averageTripDistance.toMiles()
    }
}