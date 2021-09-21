package com.telematics.domain.model.statistics

data class StatisticEcoScoringTabsData(
    var week: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var month: StatisticEcoScoringTabData = StatisticEcoScoringTabData(),
    var year: StatisticEcoScoringTabData = StatisticEcoScoringTabData()
)