package com.telematics.features.account.ui.account

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.domain.model.authentication.User
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.features.account.use_case.LoginUseCase
import com.telematics.features.account.use_case.VehicleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class AccountViewModel @Inject constructor(
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

    fun uploadProfilePicture(filePath: String?): LiveData<Result<Unit>> {

        val uploadProfilePictureState = MutableLiveData<Result<Unit>>()
        loginUseCase.uploadProfilePicture(filePath)
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(uploadProfilePictureState)
            .launchIn(viewModelScope)
        return uploadProfilePictureState
    }

    fun getProfilePicture(): LiveData<Result<Bitmap?>> {

        val profilePictureState = MutableLiveData<Result<Bitmap?>>()
        loginUseCase.getProfilePicture()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(profilePictureState)
            .launchIn(viewModelScope)
        return profilePictureState
    }

    fun getVehicles(): LiveData<Result<List<Vehicle>>> {

        val vehiclesState = MutableLiveData<Result<List<Vehicle>>>()
        vehicleUseCase.getVehicles()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(vehiclesState)
            .launchIn(viewModelScope)
        return vehiclesState
    }
}