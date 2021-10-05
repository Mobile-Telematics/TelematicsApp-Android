package com.telematics.features.feed.ui.trip_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.tracking.ChangeTripEvent
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.model.tracking.TripDetailsData
import com.telematics.domain.model.tracking.TripPointData
import com.telematics.features.feed.model.AlertType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TripDetailViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    val formatter: MeasuresFormatter
) : ViewModel() {

    private var currentTripId: String? = null

    fun getTripDetailsByPos(position: Int): LiveData<Result<TripDetailsData?>> {

        val tripDataState = MutableLiveData<Result<TripDetailsData?>>()
        trackingUseCase.getTripDetailsByPos(position)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(tripDataState)
            .map { tripDetailsData ->
                currentTripId = tripDetailsData?.id
            }
            .launchIn(viewModelScope)
        return tripDataState
    }

    fun changeTripTypeTo(tripId: String, tripType: TripData.TripType): LiveData<Result<Boolean>> {

        val changeState = MutableLiveData<Result<Boolean>>()
        trackingUseCase.changeTripType(tripId, tripType)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeState)
            .launchIn(viewModelScope)
        return changeState
    }

    fun changeTripEvent(
        event: TripPointData,
        alertType: AlertType
    ): LiveData<Result<Boolean>> {

        val tripId = currentTripId.orEmpty()

        val changeTripEvent = ChangeTripEvent(
            AlertType.from(event.alertType).toString(),
            event.latitude,
            event.longitude,
            event.fullDate,
            alertType.toString()
        )
        val changeState = MutableLiveData<Result<Boolean>>()
        trackingUseCase.changeTripEvent(tripId, changeTripEvent)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeState)
            .launchIn(viewModelScope)
        return changeState
    }

    fun hideTrip(tripId: String): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        trackingUseCase.hideTrip(tripId)
            .flowOn(Dispatchers.Main)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }

    fun setDeleteStatus(tripId: String): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        trackingUseCase.setDeleteStatus(tripId)
            .flowOn(Dispatchers.Main)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }
}