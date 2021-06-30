package com.telematics.features.account.ui.account

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.authentication.User
import com.telematics.features.account.use_case.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val trackingUseCase: TrackingUseCase
) : ViewModel() {

    fun logout(): LiveData<Result<Boolean>> {

        val logoutState = MutableLiveData<Result<Boolean>>()
        loginUseCase.logout()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(logoutState)
            .launchIn(viewModelScope)

        trackingUseCase.logout()

        return logoutState
    }

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
}