package com.telematics.domain_.model.dashboard

import android.util.Log

data class DashboardEcoScoringTabsData(
    var week: DashboardEcoScoringTabData = DashboardEcoScoringTabData(),
    var month: DashboardEcoScoringTabData = DashboardEcoScoringTabData(),
    var year: DashboardEcoScoringTabData = DashboardEcoScoringTabData(),
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