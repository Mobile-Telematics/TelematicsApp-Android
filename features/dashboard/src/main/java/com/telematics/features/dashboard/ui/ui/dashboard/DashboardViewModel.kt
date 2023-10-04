package com.telematics.features.dashboard.ui.ui.dashboard

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveData
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.mappers.toScoreTypeModelList
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.data.utils.Resource
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandJobState
import com.telematics.domain.model.on_demand.OnDemandState
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.domain.model.reward.StreaksData
import com.telematics.domain.model.statistics.DriveCoins
import com.telematics.domain.model.statistics.StatisticEcoScoringMain
import com.telematics.domain.model.statistics.StatisticEcoScoringTabsData
import com.telematics.domain.model.statistics.StatisticScoringData
import com.telematics.domain.model.statistics.UserStatisticsIndividualData
import com.telematics.domain.model.statistics.UserStatisticsScoreData
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.repository.OnDemandRepo
import com.telematics.domain.repository.RewardRepo
import com.telematics.domain.repository.SettingsRepo
import com.telematics.domain.repository.StatisticRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import java.util.Calendar
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val statisticRepo: StatisticRepo,
    private val trackingUseCase: TrackingUseCase,
    private val settingsRepo: SettingsRepo,
    private val rewardRepo: RewardRepo,
    private val onDemandRepo: OnDemandRepo,
    val measuresFormatter: MeasuresFormatter
) : ViewModel() {

    private val TAG = "DashboardViewModel"

    val driveCoinsData = MutableLiveData<Resource<DriveCoins>>()
    val userIndividualStatisticsData = MutableLiveData<Resource<UserStatisticsIndividualData>>()
    val scoreLiveData = MutableLiveData<Resource<StatisticScoringData>>()
    val mainEcoScoringLiveData = MutableLiveData<Resource<StatisticEcoScoringMain>>()
    val tableEcoScoringLiveData = MutableLiveData<Resource<StatisticEcoScoringTabsData>>()

    fun getDriveCoins() {

        flow {
            emit(statisticRepo.getDriveCoins())
        }
            .flowOn(Dispatchers.IO)
            .setLiveData(driveCoinsData)
            .launchIn(viewModelScope)
    }

    fun getUserIndividualStatistics() {

        flow {
            val data = statisticRepo.getUserStatisticsIndividualData()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveData(userIndividualStatisticsData)
            .launchIn(viewModelScope)
    }

    fun getStatistics() {

        flow {
            val list = statisticRepo.getDrivingDetails()
            val userStatisticsIndividualData = statisticRepo.getUserStatisticsIndividualData()
            val userStatisticsScoreData = statisticRepo.getUserStatisticsScoreData()
            val scoreTypeModelChart = list.toScoreTypeModelList()
            val scoreTypeModelNumbers = userStatisticsScoreData.toScoreTypeModelList()
            val scoreData = StatisticScoringData(
                scoreTypeModelChart,
                userStatisticsIndividualData,
                scoreTypeModelNumbers
            )
            emit(scoreData)
        }
            .flowOn(Dispatchers.IO)
            .setLiveData(scoreLiveData)
            .launchIn(viewModelScope)


    }

    fun getMainEcoScoring() {

        flow {
            emit(statisticRepo.getMainEcoScoring())
        }
            .flowOn(Dispatchers.IO)
            .setLiveData(mainEcoScoringLiveData)
            .launchIn(viewModelScope)
    }

    fun getEcoScoringTable() {

        flow {
            val weekData = statisticRepo.getEcoScoringStatisticsData(Calendar.DAY_OF_WEEK)
            val monthData = statisticRepo.getEcoScoringStatisticsData(Calendar.MONTH)
            val yearData = statisticRepo.getEcoScoringStatisticsData(Calendar.YEAR)
            emit(StatisticEcoScoringTabsData(weekData, monthData, yearData))
        }
            .flowOn(Dispatchers.IO)
            .setLiveData(tableEcoScoringLiveData)
            .launchIn(viewModelScope)
    }


    fun getLastTrip(): LiveData<Result<TripData?>> {

        val lastTripDataState = MutableLiveData<Result<TripData?>>()
        trackingUseCase.getLastTrip()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(lastTripDataState)
            .launchIn(viewModelScope)
        return lastTripDataState
    }

    fun getLastTripImage(token: String): LiveData<Result<Bitmap?>> {

        val lastTripImageState = MutableLiveData<Result<Bitmap?>>()
        trackingUseCase.getTripImage(token)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(lastTripImageState)
            .launchIn(viewModelScope)
        return lastTripImageState
    }

    fun getTelematicsLink(context: Context): String {
        return settingsRepo.getTelematicsLink(context)
    }

    fun getRank(): LiveData<Result<Int>> {

        val rankState = MutableLiveData<Result<Int>>()
        flow {
            val data =
                statisticRepo.getLeaderboard(LeaderboardType.Rate)
                    ?.find { it.isCurrentUser }?.rank ?: 1
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(rankState)
            .launchIn(viewModelScope)
        return rankState
    }

    fun getDrivingStreaks(): LiveData<Result<StreaksData>> {

        val drivingStreakState = MutableLiveData<Result<StreaksData>>()
        flow {
            val data = rewardRepo.getDrivingStreaks()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(drivingStreakState)
            .launchIn(viewModelScope)
        return drivingStreakState
    }

    fun getFormatterDate(dateInString: String): String {

        val date = measuresFormatter.parseFullNewDate(dateInString)
        return measuresFormatter.getDateWithTime(date)
    }

    /** On-demand tracking*/
    private val onDemandLiveData = MutableLiveData<Result<List<DashboardOnDemandJob>>>()
    fun getOnDemandJobLiveData() = onDemandLiveData

    private var onDemandJobList = listOf<DashboardOnDemandJob>()

    private var currentOnDemandDutyState = OnDemandState.OFFLINE
    fun getCurrentDutyState() = currentOnDemandDutyState

    fun getTrackingState(): LiveData<Result<TrackingState>> {

        val state = MutableLiveData<Result<TrackingState>>()
        flow {
            val data = settingsRepo.getTrackingState()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun getDemandDutyState(): LiveData<Result<OnDemandState>> {

        val state = MutableLiveData<Result<OnDemandState>>()
        flow {
            val data = settingsRepo.getDemandDutyState()
            currentOnDemandDutyState = data
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun setDemandDutyState(state: OnDemandState) {

        currentOnDemandDutyState = state
        flow {
            val trackingState = settingsRepo.getTrackingState()
            if (trackingState == TrackingState.DEMAND) {
                if (state != OnDemandState.OFFLINE)
                    trackingUseCase.enableTracking()
                else trackingUseCase.disableTrackingSDK()
            }
            val data = settingsRepo.setDemandDutyState(state)
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun getOnDemandJobs() {

        flow {
            val list = onDemandRepo.getOnDemandJobList()
            list.sortedBy { it.createTime }.reversed().apply {
                onDemandJobList = this
            }
            onDemandLiveData.postValue(Result.success(onDemandJobList))
            emit(onDemandJobList)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun isExistsOnDemandCurrentJobs(): Boolean {

        return onDemandJobList
            .find { it.state == OnDemandJobState.CURRENT.name } != null
    }

    fun setOnDemandCurrentJobToPaused() {

        flow {
            val list = onDemandRepo.getOnDemandJobList()
            val currentJob = list.find { it.state == OnDemandJobState.CURRENT.name }

            if (currentJob != null) {
                handlerForTrackingAPI(null, false)
                onDemandRepo.updateOnDemandJob(
                    currentJob.getOriginName,
                    OnDemandJobState.PAUSED.name
                )
            }
            getOnDemandJobs()
            emit(list)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun handlerForTrackingAPI(enableSDK: Boolean? = null, startTracking: Boolean? = null) {

        enableSDK?.also {
            if (it)
                trackingUseCase.enableTracking()
            else
                trackingUseCase.disableTrackingSDK()
        }

        startTracking?.also {
            if (it) {
                trackingUseCase.startTracking()
            } else {
                trackingUseCase.stopTracking()
            }
        }
    }

    fun getDateForDemandMode(): String {
        return measuresFormatter.getDateForDemandMode(null)
    }

    fun addAcceptedJobs(inputName: String, actionForDisableAdd: (() -> Unit)?) {
        var checkedInputName = inputName
        if (checkedInputName.isBlank() || checkedInputName.isEmpty()) {
            checkedInputName = getDefaultOnDemandJobName()
        }

        if (isExistsOnDemandAcceptedJobs(checkedInputName)) {
            actionForDisableAdd?.invoke()
            return
        }

        flow {
            val list = onDemandRepo.getOnDemandJobList()
            val currentJob = list.find { it.state == OnDemandJobState.CURRENT.name }

            if (currentJob != null) {
                handlerForTrackingAPI(null, false)
                onDemandRepo.updateOnDemandJob(
                    currentJob.getOriginName,
                    OnDemandJobState.PAUSED.name
                )
            }
            getOnDemandJobs()
            emit(list)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        flow {
            onDemandRepo.getOnDemandJobList()
            val job = DashboardOnDemandJob(
                checkedInputName,
                OnDemandJobState.ACCEPTED.name,
                System.currentTimeMillis()
            )
            onDemandRepo.addOnDemandJob(job)
            getOnDemandJobs()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun getDefaultOnDemandJobName(): String {
        return getDateForDemandMode()
    }

    private fun isExistsOnDemandAcceptedJobs(checkName: String): Boolean {

        return onDemandJobList
            .find {
                it.getName == checkName && (
                        it.state == OnDemandJobState.ACCEPTED.name
                                || it.state == OnDemandJobState.PAUSED.name
                                || it.state == OnDemandJobState.CURRENT.name
                        )
            } != null
    }

    fun addAndStartOnDemandJob(
        inputName: String,
        actionForDialog: (() -> Unit)?,
        action: ((isCurrentJobExist: Boolean, newJob: DashboardOnDemandJob) -> Unit)?
    ) {
        var checkedInputName = inputName
        if (checkedInputName.isBlank() || checkedInputName.isEmpty()) {
            checkedInputName = getDefaultOnDemandJobName()
        }

        if (isExistsOnDemandAcceptedJobs(checkedInputName)) {
            actionForDialog?.invoke()
            return
        }

        flow {
            val list = onDemandRepo.getOnDemandJobList()
            val currentTime = System.currentTimeMillis()
            val newJob = DashboardOnDemandJob(
                checkedInputName,
                OnDemandJobState.CURRENT.name,
                currentTime
            )
            if (action == null) {
                handlerForTrackingAPI(null, true)
                onDemandRepo.addOnDemandJob(newJob)
            }
            val currentJob = list.find { it.state == OnDemandJobState.CURRENT.name }
            action?.invoke(currentJob != null, newJob)
            if (currentJob == null) {
                handlerForTrackingAPI(null, true)
                onDemandRepo.addOnDemandJob(newJob)
            }

            getOnDemandJobs()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun setOnDemandCurrentJobToCompleted() {

        flow {
            val list = onDemandRepo.getOnDemandJobList()
            val currentJob = list.find { it.state == OnDemandJobState.CURRENT.name }
            if (currentJob != null) {
                val isCurrentJobExistInCompletedJobs =
                    list.find { it.state == OnDemandJobState.COMPLETE.name && it.getName == currentJob.getName } != null
                handlerForTrackingAPI(null, false)
                if (!isCurrentJobExistInCompletedJobs)
                    onDemandRepo.updateOnDemandJob(
                        currentJob.getOriginName,
                        OnDemandJobState.COMPLETE.name
                    )
                else {
                    onDemandRepo.removeOnDemandJob(currentJob)
                }
            }

            getOnDemandJobs()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun removeOnDemandJob(job: DashboardOnDemandJob) {

        flow {
            //val list = onDemandRepo.getOnDemandJobList()
            trackingUseCase.removeFutureTrackTag(job.getTag)
            handlerForTrackingAPI(null, false)
            onDemandRepo.removeOnDemandJob(job)
            getOnDemandJobs()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun updateOnDemandJob(
        job: DashboardOnDemandJob,
        newState: String,
        action: ((isCurrentJobExist: Boolean) -> Unit)?
    ) {

        flow {
            val list = onDemandRepo.getOnDemandJobList()
            if (newState == OnDemandJobState.CURRENT.name) {

                if (action == null) {
                    handlerForTrackingAPI(null, true)
                    onDemandRepo.updateOnDemandJob(job.getOriginName, newState)
                }

                val currentJobExist =
                    list.find { it.state == OnDemandJobState.CURRENT.name } != null
                action?.invoke(currentJobExist)
                if (!currentJobExist) {
                    handlerForTrackingAPI(null, true)
                    onDemandRepo.updateOnDemandJob(job.getOriginName, newState)
                }
            } else {
                handlerForTrackingAPI(null, false)
                onDemandRepo.updateOnDemandJob(job.getOriginName, newState)
            }
            getOnDemandJobs()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun getIndividualDataByTag(job: DashboardOnDemandJob): MutableLiveData<Result<Pair<UserStatisticsIndividualData, UserStatisticsScoreData>>> {

        val state =
            MutableLiveData<Result<Pair<UserStatisticsIndividualData, UserStatisticsScoreData>>>()
        flow {
            val data = statisticRepo.getOnDemandCompletedData(job)
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun setTrackingTag(jobList: List<DashboardOnDemandJob>?) {

        flow {
            val lastCurrentJob = settingsRepo.getOnDemandLastCurrentJob()
            val lastTag = lastCurrentJob.toString()

            jobList?.find { it.state == OnDemandJobState.CURRENT.name }?.let { job ->
                settingsRepo.setOnDemandLastCurrentJob(job)
                val tag = job.getTag
                if (lastTag != tag) {
                    handlerForTrackingAPI(null, true)
                    trackingUseCase.addFutureTrackTag(tag)
                }
            }

            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}