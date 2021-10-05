package com.telematics.features.account.ui.account.vehicle

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.authentication.User
import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.features.account.use_case.LoginUseCase
import com.telematics.features.account.use_case.VehicleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class VehicleViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val vehicleUseCase: VehicleUseCase
) : ViewModel() {

    fun getUser(): LiveData<Result<User>> {

        val userState = MutableLiveData<Result<User>>()
        loginUseCase.getUser()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(userState)
            .launchIn(viewModelScope)
        return userState
    }

    fun getProfilePicture(): LiveData<Result<Bitmap?>> {

        val profilePictureState = MutableLiveData<Result<Bitmap?>>()
        loginUseCase.getProfilePicture()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(profilePictureState)
            .launchIn(viewModelScope)
        return profilePictureState
    }

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