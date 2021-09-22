package com.telematics.zenroad.ui.settings.measures

import androidx.lifecycle.ViewModel
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure
import com.telematics.domain.repository.SettingsRepo
import javax.inject.Inject

class MeasuresViewModel @Inject constructor(
    private val settingsRepo: SettingsRepo
) : ViewModel() {

    fun getDateMeasure(): DateMeasure {

        return settingsRepo.getDateMeasure()
    }

    fun getDistanceMeasure(): DistanceMeasure {

        return settingsRepo.getDistanceMeasure()
    }

    fun getTimeMeasure(): TimeMeasure {

        return settingsRepo.getTimeMeasure()
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