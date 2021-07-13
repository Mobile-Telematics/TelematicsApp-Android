package com.telematics.features.feed.ui.feed

import android.content.Context
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
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    private val dateFormatter: DateFormatter,
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    private val LOADING_COUNT = 20

    val getDateFormatter: DateFormatter
        get() {
            return dateFormatter
        }

    fun getTripList(offset: Int = 0): LiveData<Result<List<TripData>>> {

        val firstDataState = MutableLiveData<Result<List<TripData>>>()
        trackingUseCase.getTrips(offset, LOADING_COUNT)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(firstDataState)
            .launchIn(viewModelScope)
        return firstDataState
    }

    fun getTelematicsLink(context: Context): String {

        return settingsRepo.getTelematicsLink(context)
    }
}