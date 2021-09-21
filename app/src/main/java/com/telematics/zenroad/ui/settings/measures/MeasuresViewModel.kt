package com.telematics.zenroad.ui.settings.measures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure
import com.telematics.domain.repository.SettingsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class MeasuresViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    fun getDateMeasure(): LiveData<Result<DateMeasure>> {

        val stateDateMeasure = MutableLiveData<Result<DateMeasure>>()
        settingsRepo.getDateMeasure()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(stateDateMeasure)
            .launchIn(viewModelScope)
        return stateDateMeasure
    }

    fun getDistanceMeasure(): LiveData<Result<DistanceMeasure>> {

        val stateDistanceMeasure = MutableLiveData<Result<DistanceMeasure>>()
        settingsRepo.getDistanceMeasure()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(stateDistanceMeasure)
            .launchIn(viewModelScope)
        return stateDistanceMeasure
    }

    fun getTimeMeasure(): LiveData<Result<TimeMeasure>> {

        val stateTimeMeasure = MutableLiveData<Result<TimeMeasure>>()
        settingsRepo.getTimeMeasure()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(stateTimeMeasure)
            .launchIn(viewModelScope)
        return stateTimeMeasure
    }

    fun setDateMeasure(dateMeasure: DateMeasure) {

        settingsRepo.setDateMeasure(dateMeasure)
    }

    fun setDistanceMeasure(distanceMeasure: DistanceMeasure) {

        settingsRepo.setDistanceMeasure(distanceMeasure)
    }

    fun setTimeMeasure(timeMeasure: TimeMeasure) {

        settingsRepo.setTimeMeasure(timeMeasure)
    }
}