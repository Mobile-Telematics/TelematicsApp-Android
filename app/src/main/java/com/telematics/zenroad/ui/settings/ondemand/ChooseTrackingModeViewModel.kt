package com.telematics.zenroad.ui.settings.ondemand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandJobState
import com.telematics.domain.model.on_demand.OnDemandState
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.domain.repository.OnDemandRepo
import com.telematics.domain.repository.SettingsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class ChooseTrackingModeViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    private val settingsRepo: SettingsRepo,
    private val onDemandRepo: OnDemandRepo
) : ViewModel() {

    fun getTrackingState(): MutableLiveData<Result<TrackingState>> {

        val trackingState = MutableLiveData<Result<TrackingState>>()
        flow {
            val data = settingsRepo.getTrackingState()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(trackingState)
            .launchIn(viewModelScope)
        return trackingState
    }

    fun getOnDemandJobList(): MutableLiveData<Result<List<DashboardOnDemandJob>>> {

        val state = MutableLiveData<Result<List<DashboardOnDemandJob>>>()

        flow {
            val data = onDemandRepo.getOnDemandJobList()
            emit(data)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)

        return state

    }

    fun setOnDemandCurrentJobToCompleted(tempState: TrackingState): LiveData<Result<Unit>> {

        val state = MutableLiveData<Result<Unit>>()
        flow {
            onDemandRepo.removeOnDemandJobByState(OnDemandJobState.ACCEPTED)
            onDemandRepo.removeOnDemandJobByState(OnDemandJobState.PAUSED)
            onDemandRepo.removeOnDemandJobByState(OnDemandJobState.CURRENT)

            val list = onDemandRepo.getOnDemandJobList()
            val newList = mutableListOf<DashboardOnDemandJob>()

            list.find { it.state == OnDemandJobState.CURRENT.name }?.let { currentJob ->
                trackingUseCase.removeFutureTrackTag(currentJob.getTag)
            }
            list.filter {
                it.state == OnDemandJobState.CURRENT.name ||
                        it.state == OnDemandJobState.PAUSED.name
            }.forEach { currentPausedJobs ->
                val canAddToCompleteList =
                    list.find { it.state == OnDemandJobState.COMPLETE.name && it.getOriginName == currentPausedJobs.getOriginName }
                if (canAddToCompleteList == null) {
                    newList.add(
                        DashboardOnDemandJob(
                            currentPausedJobs.name,
                            OnDemandJobState.COMPLETE.name,
                            currentPausedJobs.createTime
                        )
                    )
                }
            }
            list.filter {
                it.state == OnDemandJobState.COMPLETE.name
            }.apply {
                newList.addAll(this)
            }

            onDemandRepo.putOnDemandJobList(newList.toList())

            saveTrackingState(tempState)
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(state)
            .launchIn(viewModelScope)
        return state
    }

    fun saveTrackingState(trackingState: TrackingState) {

        flow {
            settingsRepo.setTrackingState(trackingState)
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        flow {
            settingsRepo.setDemandDutyState(OnDemandState.OFFLINE)
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        when (trackingState) {
            TrackingState.DEMAND -> {
                trackingUseCase.disableTrackingSDK()
                trackingUseCase.stopTracking()
            }

            TrackingState.AUTO -> {
                trackingUseCase.enableTrackingSDK()
                trackingUseCase.stopTracking()
                trackingUseCase.enableTracking()
            }

            TrackingState.DISABLE -> {
                trackingUseCase.disableTrackingSDK()
                trackingUseCase.stopTracking()
            }
        }
    }
}