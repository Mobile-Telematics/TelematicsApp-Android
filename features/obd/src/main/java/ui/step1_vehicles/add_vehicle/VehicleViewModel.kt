package ui.step1_vehicles.add_vehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.domain.model.carservice.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import use_case.VehicleUseCase
import javax.inject.Inject

class VehicleViewModel @Inject constructor(
    private val vehicleUseCase: VehicleUseCase
) : ViewModel() {

    fun updateVehicle(vehicle: Vehicle): LiveData<Result<Unit>> {

        val saveState = MutableLiveData<Result<Unit>>()
        vehicleUseCase.updateVehicle(vehicle)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(saveState)
            .launchIn(viewModelScope)
        return saveState
    }

    fun createVehicle(vehicle: Vehicle): LiveData<Result<Unit>> {

        val createState = MutableLiveData<Result<Unit>>()
        vehicleUseCase.createVehicle(vehicle)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(createState)
            .launchIn(viewModelScope)
        return createState
    }

    fun deleteVehicle(vehicle: Vehicle): LiveData<Result<Unit>> {

        val deleteState = MutableLiveData<Result<Unit>>()
        vehicleUseCase.deleteVehicle(vehicle)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(deleteState)
            .launchIn(viewModelScope)
        return deleteState
    }

    fun getManufacturers(): LiveData<Result<List<ManufacturerData>>> {

        val manufacturerState = MutableLiveData<Result<List<ManufacturerData>>>()
        vehicleUseCase.getManufacturers()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(manufacturerState)
            .launchIn(viewModelScope)
        return manufacturerState
    }

    fun getModels(manufacturerId: Int): LiveData<Result<List<ModelData>>> {

        val modelState = MutableLiveData<Result<List<ModelData>>>()
        vehicleUseCase.getModels(manufacturerId)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(modelState)
            .launchIn(viewModelScope)
        return modelState
    }
}