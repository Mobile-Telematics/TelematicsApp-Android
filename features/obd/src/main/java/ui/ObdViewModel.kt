package ui

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.domain.model.tracking.ElmDevice
import com.telematics.domain.model.tracking.ElmManagerLinkingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import use_case.VehicleUseCase
import javax.inject.Inject

class ObdViewModel @Inject constructor(
    private val vehicleUseCase: VehicleUseCase,
    private val trackingUseCase: TrackingUseCase
) : ViewModel() {

    fun getVehicles(): LiveData<Result<List<Vehicle>>> {

        val vehiclesState = MutableLiveData<Result<List<Vehicle>>>()
        vehicleUseCase.getVehicles()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(vehiclesState)
            .launchIn(viewModelScope)
        return vehiclesState
    }

    fun getLastSession(): LiveData<Result<Long>> {

        val sessionState = MutableLiveData<Result<Long>>()
        trackingUseCase.getLastSession()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(sessionState)
            .launchIn(viewModelScope)
        return sessionState
    }

    fun uploadOdometerPhoto(filePath: String): LiveData<Result<Unit>> {

        val uploadState = MutableLiveData<Result<Unit>>()
        flow {
            delay(2000)
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(uploadState)
            .launchIn(viewModelScope)
        return uploadState
    }

    fun getBluetoothAdapter(context: Context): BluetoothAdapter? =
        trackingUseCase.getBluetoothAdapter(context)

    fun getRequestBluetoothEnableCode() = trackingUseCase.getRequestBluetoothEnableCode()

    fun registerElmManagerLinkingResult(): LiveData<Result<ElmManagerLinkingResult?>> {

        val elmDeviceState = MutableLiveData<Result<ElmManagerLinkingResult?>>()
        trackingUseCase.getElmManagerLinkingResult()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(elmDeviceState)
            .launchIn(viewModelScope)

        return elmDeviceState
    }


    fun getElmDevices() {

        trackingUseCase.getElmDevice()
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun connectSelectedDevice(
        device: ElmDevice,
        token: String
    ) {

        trackingUseCase.connectSelectedDevice(device, token)
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}