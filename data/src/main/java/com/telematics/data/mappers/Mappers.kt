package com.telematics.data.mappers

import com.telematics.data.R
import com.telematics.data.model.carservice.CarRest
import com.telematics.data.model.carservice.CarUpdateBody
import com.telematics.data.model.carservice.ManufacturerRest
import com.telematics.data.model.carservice.ModelRest
import com.telematics.data.model.company_id.InstanceNameBody
import com.telematics.data.model.rest.ApiResult
import com.telematics.data.model.reward.*
import com.telematics.data.model.statistics.*
import com.telematics.domain.model.RegistrationApiData
import com.telematics.domain.model.SessionData
import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.domain.model.company_id.InstanceName
import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.domain.model.leaderboard.LeaderboardUser
import com.telematics.domain.model.leaderboard.LeaderboardUserItems
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.reward.*
import com.telematics.domain.model.statistics.*
import com.telematics.domain.model.tracking.ElmDevice
import java.text.SimpleDateFormat
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

fun LeaderboardResponse.toLeaderboardData(type: Int): List<LeaderboardMemberData> {

    val mappedType = when (type) {
        1 -> LeaderboardType.Acceleration
        2 -> LeaderboardType.Deceleration
        3 -> LeaderboardType.Distraction
        4 -> LeaderboardType.Speeding
        5 -> LeaderboardType.Turn
        6 -> LeaderboardType.Rate
        7 -> LeaderboardType.Distance
        8 -> LeaderboardType.Trips
        9 -> LeaderboardType.Duration
        else -> LeaderboardType.Rate
    }

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
            mappedType,
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
            type = LeaderboardType.Rate,
            progress = data.usersNumber - data.place.toDouble() + 1,
            place = data.place,
            progressMax = data.usersNumber
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
    LeaderboardType.Rate -> 0
    LeaderboardType.Acceleration -> R.drawable.ic_leaderboard_acceleration
    LeaderboardType.Deceleration -> R.drawable.ic_leaderboard_deceleration
    LeaderboardType.Speeding -> R.drawable.ic_leaderboard_speeding
    LeaderboardType.Distraction -> R.drawable.ic_leaderboard_phone
    LeaderboardType.Turn -> R.drawable.ic_leaderboard_cornering
    LeaderboardType.Trips -> R.drawable.ic_leaderboard_trips
    LeaderboardType.Distance -> R.drawable.ic_leaderboard_mileage
    LeaderboardType.Duration -> R.drawable.ic_leaderboard_time
}

fun LeaderboardType.getStringRes(): Int = when (this) {
    LeaderboardType.Rate -> R.string.leaderboard_rate
    LeaderboardType.Acceleration -> R.string.leaderboard_acceleration
    LeaderboardType.Deceleration -> R.string.leaderboard_deceleration
    LeaderboardType.Speeding -> R.string.leaderboard_speeding
    LeaderboardType.Distraction -> R.string.leaderboard_distraction
    LeaderboardType.Turn -> R.string.leaderboard_turn
    LeaderboardType.Trips -> R.string.leaderboard_total_trips
    LeaderboardType.Distance -> R.string.leaderboard_mileage
    LeaderboardType.Duration -> R.string.leaderboard_time_driven
}

fun DailyLimit?.toDailyLimitData(): DailyLimitData {
    this ?: return DailyLimitData()
    return DailyLimitData(this.dailyLimit)
}

fun DriveCoinsTotal?.toDriveCoinsTotalData(): DriveCoinsTotalData {
    this ?: return DriveCoinsTotalData()
    return DriveCoinsTotalData(
        this.totalEarnedCoins,
        this.acquiredCoins
    )
}

fun DriveCoinsDetailedData.setCompleteData(
    individualData: UserStatisticsIndividualRest?,
    coinsDetailedList: List<DriveCoinsDetailed>?,
    driveCoinsDetailed2: DriveCoinsDetailed2?,
    driveCoinsScoreEco: DriveCoinsScoreEco?
): DriveCoinsDetailedData {

    val detailedData = this

    detailedData.travelingTimeDrivenData = individualData?.drivingTime?.roundToInt()
        ?: 0
    detailedData.travelingMileageData = individualData?.mileageKm?.roundToInt() ?: 0

    var midSpeedingMileage = 0
    var highSpeedingMileage = 0

    coinsDetailedList?.forEach { driveCoinsDetailed ->
        when (driveCoinsDetailed.coinFactor.toLowerCase()) {
            "EcoScore".toLowerCase() -> detailedData.ecoDrivingEcoScore =
                driveCoinsDetailed.coinsSum
            "EcoScoreBrakes".toLowerCase() -> detailedData.ecoDrivingBrakes =
                driveCoinsDetailed.coinsSum
            "EcoScoreDepreciation".toLowerCase() -> detailedData.ecoDrivingCostOfOwnership =
                driveCoinsDetailed.coinsSum
            "EcoScoreFuel".toLowerCase() -> detailedData.ecoDrivingFuel =
                driveCoinsDetailed.coinsSum
            "EcoScoreTyres".toLowerCase() -> detailedData.ecoDrivingTires =
                driveCoinsDetailed.coinsSum

            "Mileage".toLowerCase() -> detailedData.travelingMileage = driveCoinsDetailed.coinsSum
            "DurationSec".toLowerCase() -> detailedData.travelingTimeDriven =
                driveCoinsDetailed.coinsSum
            "AccelerationCount".toLowerCase() -> detailedData.travelingAccelerations =
                driveCoinsDetailed.coinsSum
            "BrakingCount".toLowerCase() -> detailedData.travelingBrakings =
                driveCoinsDetailed.coinsSum
            "PhoneUsage".toLowerCase() -> detailedData.travelingPhoneUsage =
                driveCoinsDetailed.coinsSum
            "CorneringCount".toLowerCase() -> detailedData.travelingCornerings =
                driveCoinsDetailed.coinsSum
            "MidSpeedingMileage".toLowerCase() -> midSpeedingMileage = driveCoinsDetailed.coinsSum
            "HighSpeedingMileage".toLowerCase() -> highSpeedingMileage = driveCoinsDetailed.coinsSum

            "SafeScore".toLowerCase() -> detailedData.safeDrivingCoinsTotal =
                driveCoinsDetailed.coinsSum
        }
    }
    detailedData.travelingSpeeding = midSpeedingMileage + highSpeedingMileage


    detailedData.ecoScore = driveCoinsScoreEco?.ecoScore
        ?.roundToInt() ?: 0
    detailedData.ecoScoreBrakes = driveCoinsScoreEco?.ecoScoreBrakes
        ?.roundToInt() ?: 0
    detailedData.ecoScoreFuel = driveCoinsScoreEco?.ecoScoreFuel
        ?.roundToInt() ?: 0
    detailedData.ecoScoreTyres = driveCoinsScoreEco?.ecoScoreTyres
        ?.roundToInt() ?: 0
    detailedData.ecoScoreCostOfOwnership = driveCoinsScoreEco?.ecoScoreDepreciation
        ?.roundToInt() ?: 0

    detailedData.travellingSum =
        detailedData.travelingMileage + detailedData.travelingTimeDriven + detailedData.travelingAccelerations + detailedData.travelingBrakings + detailedData.travelingCornerings + detailedData.travelingPhoneUsage + detailedData.travelingSpeeding
    detailedData.safeDrivingSum = detailedData.safeDrivingCoinsTotal
    detailedData.ecoDrivingSum =
        detailedData.ecoDrivingEcoScore + detailedData.ecoDrivingBrakes + detailedData.ecoDrivingFuel + detailedData.ecoDrivingTires + detailedData.ecoDrivingCostOfOwnership

    detailedData.travelingAccelerationCount = driveCoinsDetailed2?.accelerationCount
        ?: 0
    detailedData.travelingBrakingCount = driveCoinsDetailed2?.brakingCount ?: 0
    detailedData.travelingCorneringCount = driveCoinsDetailed2?.corneringCount ?: 0
    detailedData.travelingTotalSpeedingKm = driveCoinsDetailed2?.totalSpeedingKm?.roundToInt()
        ?: 0
    detailedData.travelingDrivingTime = driveCoinsDetailed2?.phoneUsage?.roundToInt()
        ?: 0

    return this
}

fun StreaksRest?.toStreakList(dateMeasure: DateMeasure): List<Streak> {

    fun getDistance(d: Double) = "${d.roundToInt()} km"
    fun getDate(s: String): String? {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(s) ?: return null

        val format = when (dateMeasure) {
            DateMeasure.MM_DD -> "MM.dd.yy"
            DateMeasure.DD_MM -> "dd.MM.yy"
        }
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        return simpleDateFormat.format(date)
    }

    val min = "m"

    this ?: return emptyList()
    val data = this

    return listOf(
        Streak(
            StreakCarType.Acceleration,
            getDistance(data.StreakAccelerationCurrentDistanceKm) + " | " + (data.StreakAccelerationCurrentDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakAccelerationCurrentFromDate) + " to " + getDate(data.StreakAccelerationCurrentToDate),
            data.StreakAccelerationCurrentStreak.toString(),

            getDistance(data.StreakAccelerationBestDistanceKm) + " | " + (data.StreakAccelerationBestDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakAccelerationBestFromDate) + " to " + getDate(data.StreakAccelerationBestToDate),
            data.StreakAccelerationBest.toString()
        ),
        Streak(
            StreakCarType.Braking,
            getDistance(data.StreakBrakingCurrentDistanceKm) + " | " + (data.StreakBrakingCurrentDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakBrakingCurrentFromDate) + " to " + getDate(data.StreakBrakingCurrentToDate),
            data.StreakBrakingCurrentStreak.toString(),

            getDistance(data.StreakBrakingBestDistanceKm) + " | " + (data.StreakBrakingBestDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakBrakingBestFromDate) + " to " + getDate(data.StreakBrakingBestToDate),
            data.StreakBrakingBest.toString()
        ),
        Streak(
            StreakCarType.Cornering,
            getDistance(data.StreakCorneringCurrentDistanceKm) + " | " + (data.StreakCorneringCurrentDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakCorneringCurrentFromDate) + " to " + getDate(data.StreakCorneringCurrentToDate),
            data.StreakCorneringCurrentStreak.toString(),

            getDistance(data.StreakCorneringBestDistanceKm) + " | " + (data.StreakCorneringBestDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakCorneringBestFromDate) + " to " + getDate(data.StreakCorneringBestToDate),
            data.StreakCorneringBest.toString()
        ),
        Streak(
            StreakCarType.Speeding,
            getDistance(data.StreakOverSpeedCurrentDistanceKm) + " | " + (data.StreakOverSpeedCurrentDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakOverSpeedCurrentFromDate) + " to " + getDate(data.StreakOverSpeedCurrentToDate),
            data.StreakOverSpeedCurrentStreak.toString(),

            getDistance(data.StreakOverSpeedBestDistanceKm) + " | " + (data.StreakOverSpeedBestDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakOverSpeedBestFromDate) + " to " + getDate(data.StreakOverSpeedBestToDate),
            data.StreakOverSpeedBest.toString()
        ),
        Streak(
            StreakCarType.PhoneUsage,
            getDistance(data.StreakPhoneUsageCurrentDistanceKm) + " | " + (data.StreakPhoneUsageCurrentDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakPhoneUsageCurrentFromDate) + " to " + getDate(data.StreakPhoneUsageCurrentToDate),
            data.StreakPhoneUsageCurrentStreak.toString(),

            getDistance(data.StreakPhoneUsageBestDistanceKm) + " | " + (data.StreakPhoneUsageBestDurationSec.toFloat() / 60f).roundToInt()
                .toString() + " $min",
            getDate(data.StreakPhoneUsageBestFromDate) + " to " + getDate(data.StreakPhoneUsageBestToDate),
            data.StreakPhoneUsageBest.toString()
        )
    )
}

fun StreaksRest?.toStreakData(): StreaksData {

    this ?: return StreaksData()
    val data = this

    return StreaksData(
        data.StreakAccelerationBest,
        data.StreakAccelerationBestDurationSec,
        data.StreakAccelerationBestDistanceKm,
        data.StreakAccelerationBestFromDate,
        data.StreakAccelerationBestToDate,
        data.StreakAccelerationCurrentStreak,
        data.StreakAccelerationCurrentDurationSec,
        data.StreakAccelerationCurrentDistanceKm,
        data.StreakAccelerationCurrentFromDate,
        data.StreakAccelerationCurrentToDate,

        data.StreakBrakingBest,
        data.StreakBrakingBestDurationSec,
        data.StreakBrakingBestDistanceKm,
        data.StreakBrakingBestFromDate,
        data.StreakBrakingBestToDate,
        data.StreakBrakingCurrentStreak,
        data.StreakBrakingCurrentDurationSec,
        data.StreakBrakingCurrentDistanceKm,
        data.StreakBrakingCurrentFromDate,
        data.StreakBrakingCurrentToDate,

        data.StreakCorneringBest,
        data.StreakCorneringBestDurationSec,
        data.StreakCorneringBestDistanceKm,
        data.StreakCorneringBestFromDate,
        data.StreakCorneringBestToDate,
        data.StreakCorneringCurrentStreak,
        data.StreakCorneringCurrentDurationSec,
        data.StreakCorneringCurrentDistanceKm,
        data.StreakCorneringCurrentFromDate,
        data.StreakCorneringCurrentToDate,

        data.StreakOverSpeedBest,
        data.StreakOverSpeedBestDurationSec,
        data.StreakOverSpeedBestDistanceKm,
        data.StreakOverSpeedBestFromDate,
        data.StreakOverSpeedBestToDate,
        data.StreakOverSpeedCurrentStreak,
        data.StreakOverSpeedCurrentDurationSec,
        data.StreakOverSpeedCurrentDistanceKm,
        data.StreakOverSpeedCurrentFromDate,
        data.StreakOverSpeedCurrentToDate,

        data.StreakPhoneUsageBest,
        data.StreakPhoneUsageBestDurationSec,
        data.StreakPhoneUsageBestDistanceKm,
        data.StreakPhoneUsageBestFromDate,
        data.StreakPhoneUsageBestToDate,
        data.StreakPhoneUsageCurrentStreak,
        data.StreakPhoneUsageCurrentDurationSec,
        data.StreakPhoneUsageCurrentDistanceKm,
        data.StreakPhoneUsageCurrentFromDate,
        data.StreakPhoneUsageCurrentToDate
    )
}

fun InstanceNameBody?.toInstanceName(): InstanceName {

    this ?: return InstanceName(null, false)

    return InstanceName(
        this.instanceName,
        true
    )
}

fun CarRest?.toVehicle(): Vehicle {
    this ?: return Vehicle()
    val car = this

    val mileage = car.initialMilage?.toDoubleOrNull()?.toInt()?.toString()
    return Vehicle(
        car.plateNumber,
        car.vin,
        car.manufacturer,
        car.manufacturerId,
        car.model,
        car.modelId,
        car.name,
        car.carYear,
        mileage,
        car.token,
        car.activated,
        car.company,
        car.type,
        car.specialMarks,
        car.nvic,
        car.vehicleIdString
    )
}

fun Vehicle.toCarUpdateBody(): CarUpdateBody {

    val vehicle = this
    return CarUpdateBody(
        plateNumber = vehicle.plateNumber.orEmpty(),
        vin = vehicle.vin.orEmpty(),
        manufacturer = vehicle.manufacturer,
        manufacturerId = vehicle.manufacturerId,
        model = vehicle.model,
        modelId = vehicle.modelId,
        type = vehicle.type.orEmpty(),
        name = vehicle.name.orEmpty(),
        carYear = vehicle.carYear,
        initialMileage = vehicle.initialMileage
    )
}

fun ManufacturerRest.toManufacturerData(): ManufacturerData {

    val manufacturerRest = this
    return ManufacturerData(manufacturerRest.id, manufacturerRest.name)
}

fun ModelRest.toModelData(): ModelData {

    val modelRest = this
    return ModelData(modelRest.id, modelRest.name)
}

fun com.raxeltelematics.v2.sdk.services.main.elm.ElmDevice.toElmDevice(): ElmDevice {
    return ElmDevice(
        this.connectedState,
        this.device,
        this.deviceMacAddress,
        this.deviceName,
        this.rssi
    )
}