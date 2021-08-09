package com.telematics.data.mappers

import com.telematics.data.R
import com.telematics.data.model.rest.ApiResult
import com.telematics.data.model.statistics.*
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.domain.model.leaderboard.LeaderboardUser
import com.telematics.domain.model.leaderboard.LeaderboardUserItems
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
        score = response.score.toInt(),
        fuel = response.fuel.toInt(),
        brakes = response.brakes.toInt(),
        tires = response.tyres.toInt(),
        cost = response.depreciation.toInt()
    )
}

fun UserStatisticsIndividualRest.toDashboardEcoScoringTabData(): StatisticEcoScoringTabData {
    val response = this
    val averageTripDistance = (response.mileageKm) / (response.tripsCount)
    return StatisticEcoScoringTabData(
        response.averageSpeedKmh,
        response.maxSpeedKmh,
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

fun LeaderboardResponse.toLeaderboardData(type: LeaderboardType): List<LeaderboardMemberData> {

    val listLeaderboardMemberRest = this.users

    if (listLeaderboardMemberRest.isNullOrEmpty()) return emptyList()

    val listFriendsMembersData = ArrayList<LeaderboardMemberData>(listLeaderboardMemberRest.size)
    listLeaderboardMemberRest.indices.mapTo(listFriendsMembersData) {
        val user = listLeaderboardMemberRest[it]
        LeaderboardMemberData(
            user.deviceToken ?: "",
            user.firstName ?: "",
            user.lastName ?: "",
            user.place ?: 1,
            user.trips ?: 0,
            user.image ?: "",
            user.isCurrentUser ?: false,
            type,
            user.nickname ?: "",
            user.distance ?: 0.0,
            user.duration ?: 0.0,
            user.value ?: 0.0,
            user.valuePerc ?: 0.0
        )
    }
    return listFriendsMembersData
}

fun LeaderboardUserResponse?.toLeadetboardUser(): LeaderboardUser {

    val user = this ?: run {
        return LeaderboardUser()
    }

    return LeaderboardUser(
        user.accelerationPerc ?: 0.0,
        user.accelerationPlace ?: 0,
        user.accelerationScore ?: 0.0,
        user.decelerationPerc ?: 0.0,
        user.decelerationPlace ?: 0,
        user.decelerationScore ?: 0.0,
        user.distance ?: 0.0,
        user.distractionPerc ?: 0.0,
        user.distractionPlace ?: 0,
        user.distractionScore ?: 0.0,
        user.duration ?: 0.0,
        user.distractionPerc ?: 0.0,
        user.place ?: 0,
        user.score ?: 0.0,
        user.speedingPerc ?: 0.0,
        user.speedingPlace ?: 0,
        user.speedingScore ?: 0.0,
        user.trips ?: 0,
        user.turnPerc ?: 0.0,
        user.turnPlace ?: 0,
        user.turnScore ?: 0.0,
        user.usersNumber ?: 0,
        user.tripsPlace ?: 0,
        user.durationPlace ?: 0,
        user.distancePlace ?: 0
    )
}

fun LeaderboardUser.toListofLeaderboardUserItems(): List<LeaderboardUserItems> {
    val data = this
    return mutableListOf(
        LeaderboardUserItems(
            LeaderboardType.Rate,
            data.usersNumber - data.place.toDouble() + 1,
            data.place,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Acceleration,
            data.usersNumber - data.accelerationPlace.toDouble() + 1,
            data.accelerationPlace,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Deceleration,
            data.usersNumber - data.decelerationPlace.toDouble() + 1,
            data.decelerationPlace,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Speeding,
            data.usersNumber - data.speedingPlace.toDouble() + 1,
            data.speedingPlace,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Distraction,
            data.usersNumber - data.distractionPlace.toDouble() + 1,
            data.distractionPlace,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Turn,
            data.usersNumber - data.turnPlace.toDouble() + 1,
            data.turnPlace,
            data.usersNumber
        ),
        LeaderboardUserItems(),
        LeaderboardUserItems(
            LeaderboardType.Trips,
            data.usersNumber - data.tripsPlace.toDouble() + 1,
            data.tripsPlace,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Distance,
            data.usersNumber - data.distancePlace.toDouble() + 1,
            data.distancePlace,
            data.usersNumber
        ),
        LeaderboardUserItems(
            LeaderboardType.Duration,
            data.usersNumber - data.durationPlace.toDouble() + 1,
            data.durationPlace,
            data.usersNumber
        )
    )
}

fun LeaderboardType.getIconRes(): Int = when (this) {
    LeaderboardType.Acceleration -> R.drawable.ic_leaderboard_acceleration
    LeaderboardType.Deceleration -> R.drawable.ic_leaderboard_deceleration
    LeaderboardType.Distraction -> R.drawable.ic_leaderboard_phone
    LeaderboardType.Speeding -> R.drawable.ic_leaderboard_speeding
    LeaderboardType.Turn -> R.drawable.ic_leaderboard_cornering
    LeaderboardType.Rate -> 0
    LeaderboardType.Distance -> R.drawable.ic_leaderboard_mileage
    LeaderboardType.Trips -> R.drawable.ic_leaderboard_trips
    LeaderboardType.Duration -> R.drawable.ic_leaderboard_time
}

fun LeaderboardType.getStringRes(): Int = when (this) {
    LeaderboardType.Acceleration -> R.string.leaderboard_acceleration
    LeaderboardType.Deceleration -> R.string.leaderboard_deceleration
    LeaderboardType.Distraction -> R.string.leaderboard_distraction
    LeaderboardType.Speeding -> R.string.leaderboard_speeding
    LeaderboardType.Turn -> R.string.leaderboard_turn
    LeaderboardType.Rate -> R.string.leaderboard_rate
    LeaderboardType.Distance -> R.string.leaderboard_mileage
    LeaderboardType.Trips -> R.string.leaderboard_total_trips
    LeaderboardType.Duration -> R.string.leaderboard_time_driven
}