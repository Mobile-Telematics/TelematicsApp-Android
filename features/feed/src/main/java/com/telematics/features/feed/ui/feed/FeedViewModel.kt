package com.telematics.features.feed.ui.feed

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.model.tracking.DateFormatter
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.repository.SettingsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    private val dateFormatter: DateFormatter,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    private val SAVE_LIST_STATE_BUNDLE_KEY = "SAVE_LIST_STATE_BUNDLE_KEY"

    private val LOADING_COUNT = 20

    val getDateFormatter: DateFormatter
        get() {
            return dateFormatter
        }

    private val currentTripList = mutableListOf<TripData>()
    private val saveStateBundle = MutableLiveData<Bundle>()
    val getSaveStateBundle: LiveData<Bundle>
        get() {
            return saveStateBundle
        }

    fun getTripList(offset: Int = 0): LiveData<Result<List<TripData>>> {

        val tripDataState = MutableLiveData<Result<List<TripData>>>()
        trackingUseCase.getTrips(offset, LOADING_COUNT)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(tripDataState)
            .map { tripList ->
                addTripsToCurrentList(tripList)
            }
            .launchIn(viewModelScope)
        return tripDataState
    }

    fun getPermissionLink(context: Context): String {

        return settingsRepo.getTelematicsLink(context)
    }

    private fun addTripsToCurrentList(tripIds: List<TripData>) {
        currentTripList.addAll(tripIds)
        saveListState()
    }

    fun changeTripTypeTo(tripId: String, tripType: TripData.TripType): LiveData<Result<Boolean>> {

        val changeState = MutableLiveData<Result<Boolean>>()
        trackingUseCase.changeTripType(tripId, tripType)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(changeState)
            .launchIn(viewModelScope)
        return changeState
    }

    private fun saveListState() {
        val bundle = bundleOf(SAVE_LIST_STATE_BUNDLE_KEY to currentTripList)
        saveStateBundle.value = bundle
    }

    fun getSavedListByBundle(bundle: Bundle): List<TripData> {
        return bundle.get(SAVE_LIST_STATE_BUNDLE_KEY) as List<TripData>
    }
}