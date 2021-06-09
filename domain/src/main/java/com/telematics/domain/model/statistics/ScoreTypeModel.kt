package com.telematics.domain.model.statistics

data class ScoreTypeModel(
    val type: ScoreType = ScoreType.OVERALL,
    val score: Int = -1,
    val data: MutableList<Pair<Int, Int>> = mutableListOf()
) {
    companion object {
        fun empty() = mutableListOf(
            ScoreTypeModel(ScoreType.OVERALL, -2),
            ScoreTypeModel(ScoreType.ACCELERATION, -2),
            ScoreTypeModel(ScoreType.BREAKING, -2),
            ScoreTypeModel(ScoreType.PHONE_USAGE, -2),
            ScoreTypeModel(ScoreType.SPEEDING, -2),
            ScoreTypeModel(ScoreType.CORNERING, -2)
        )
    }
}