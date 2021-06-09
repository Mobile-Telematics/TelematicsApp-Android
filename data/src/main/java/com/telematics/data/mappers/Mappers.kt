package com.telematics.data.mappers

import com.telematics.data.model.dashboard.DrivingDetailsRest
import com.telematics.data.model.dashboard.EcoScoringRest
import com.telematics.data.model.dashboard.UserStatisticsIndividualRest
import com.telematics.data.model.dashboard.UserStatisticsScoreRest
import com.telematics.data.model.rest.ApiResult
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.statistics.*
import java.util.*
import kotlin.math.roundToInt

fun ApiResult?.toSessionData(): SessionData {
    return SessionData(
        this?.accessToken?.token ?: "",
        this?.refreshToken ?: "",
        this?.accessToken?.expiresIn?.let { Date().time + it } ?: Date().time
    )
}

fun ApiResult?.toRegistrationApiData(): RegistrationApiData {
    return RegistrationApiData(
        this?.deviceToken ?: "",
        this?.accessToken?.token ?: "",
        this?.refreshToken ?: "",
        this?.accessToken?.expiresIn?.let { Date().time + it } ?: Date().time
    )
}

fun UserStatisticsIndividualRest.toUserStatisticsIndividualData(): UserStatisticsIndividualData {
    return UserStatisticsIndividualData(
        this.tripsCount.roundToInt(),
        this.mileageKm,
        this.mileageMile,
        this.drivingTime.roundToInt()
    )
}

fun DrivingDetailsRest.toDrivingDetailsData(): DrivingDetailsData {

    return DrivingDetailsData(
        "",
        this.deviceToken,
        .0,
        .0,
        0,
        this.score,
        this.scoreAcceleration,
        this.scoreDate,
        this.scoreDeceleration,
        this.scoreDistraction,
        this.scoreSpeeding,
        this.scoreTurn,
        0
    )
}

fun UserStatisticsScoreRest.toUserStatisticsScoreData(): UserStatisticsScoreData {
    return UserStatisticsScoreData(
        this.overallScore.toInt(),
        this.accelerationScore.toInt(),
        this.brakingScore.toInt(),
        this.distractedScore.toInt(),
        this.speedingScore.toInt(),
        this.corneringScore.toInt()
    )
}

fun List<DrivingDetailsData>.toScoreTypeModelList(): List<ScoreTypeModel> {

    val data = this

    val res = mutableListOf(
        ScoreTypeModel(ScoreType.OVERALL, data.last().score, mutableListOf()),
        ScoreTypeModel(ScoreType.ACCELERATION, data.last().scoreAcceleration, mutableListOf()),
        ScoreTypeModel(ScoreType.BREAKING, data.last().scoreDeceleration, mutableListOf()),
        ScoreTypeModel(ScoreType.PHONE_USAGE, data.last().scoreDistraction, mutableListOf()),
        ScoreTypeModel(ScoreType.SPEEDING, data.last().scoreSpeeding, mutableListOf()),
        ScoreTypeModel(ScoreType.CORNERING, data.last().scoreTurn, mutableListOf())
    )
    val mins: Array<Int?> = Array(res.size) { null }
    val indexes: Array<Int?> = Array(res.size) { null }
    for (i in data.indices) {
        val index = data[i].scoreDate.indexOf("T")
        val date = if (index >= 0) data[i].scoreDate.substring(index - 2, index).toInt() else i

        res[0].data.add(Pair(date, data[i].score))
        if (data[i].score != 100 && data[i].score > 20) {
            if (mins[0] == null || mins[0]!! > data[i].score) {
                mins[0] = data[i].score
                indexes[0] = i
            }
        }

        res[1].data.add(Pair(date, data[i].scoreAcceleration))
        if (data[i].scoreAcceleration != 100 && data[i].scoreAcceleration > 20) {
            if (mins[1] == null || mins[1]!! > data[i].scoreAcceleration) {
                mins[1] = data[i].scoreAcceleration
                indexes[1] = i
            }
        }

        res[2].data.add(Pair(date, data[i].scoreDeceleration))
        if (data[i].scoreDeceleration != 100 && data[i].scoreDeceleration > 20) {
            if (mins[2] == null || mins[2]!! > data[i].scoreDeceleration) {
                mins[2] = data[i].scoreDeceleration
                indexes[2] = i
            }
        }

        res[3].data.add(Pair(date, data[i].scoreDistraction))
        if (data[i].scoreDistraction != 100 && data[i].scoreDistraction > 20) {
            if (mins[3] == null || mins[3]!! > data[i].scoreDistraction) {
                mins[3] = data[i].scoreDistraction
                indexes[3] = i
            }
        }
        res[4].data.add(Pair(date, data[i].scoreSpeeding))
        if (data[i].scoreSpeeding != 100 && data[i].scoreSpeeding > 20) {
            if (mins[4] == null || mins[4]!! > data[i].scoreSpeeding) {
                mins[4] = data[i].scoreSpeeding
                indexes[4] = i
            }
        }
        res[5].data.add(Pair(date, data[i].scoreTurn))
        if (data[i].scoreTurn != 100 && data[i].scoreTurn > 20) {
            if (mins[5] == null || mins[5]!! > data[i].scoreTurn) {
                mins[5] = data[i].scoreTurn
                indexes[5] = i
            }
        }

    }

    for (i in indexes.indices) {
        indexes[i]?.let {
            res[i].data[it] = res[i].data[it].copy(res[i].data[it].first, res[i].data[it].second)
        }
    }

    return res
}

fun EcoScoringRest.toDashboardEcoScoringMain(): StatisticEcoScoringMain {
    val response = this
    return StatisticEcoScoringMain(
        score = response?.score?.toInt() ?: 0,
        fuel = response?.fuel?.toInt() ?: 0,
        brakes = response?.brakes?.toInt() ?: 0,
        tires = response?.tyres?.toInt() ?: 0,
        cost = response?.depreciation?.toInt() ?: 0
    )
}

fun UserStatisticsIndividualRest.toDashboardEcoScoringTabData(): StatisticEcoScoringTabData {
    val response = this
    val averageTripDistance = if (response == null && response?.tripsCount == .0) .0 else {
        ((response?.mileageKm ?: .0) / (response?.tripsCount ?: 1.0))
    }
    return StatisticEcoScoringTabData(
        response?.averageSpeedKmh ?: .0,
        response?.maxSpeedKmh ?: .0,
        averageTripDistance
    )
}

fun UserStatisticsScoreData.toScoreTypeModelList(): List<ScoreTypeModel> {
    return listOf(
        ScoreTypeModel(ScoreType.OVERALL, overallScore),
        ScoreTypeModel(ScoreType.ACCELERATION, accelerationScore),
        ScoreTypeModel(ScoreType.BREAKING, brakingScore),
        ScoreTypeModel(ScoreType.PHONE_USAGE, distractedScore),
        ScoreTypeModel(ScoreType.SPEEDING, speedingScore),
        ScoreTypeModel(ScoreType.CORNERING, corneringScore)
    )
}