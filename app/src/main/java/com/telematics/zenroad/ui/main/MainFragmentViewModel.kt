package com.telematics.zenroad.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.domain.model.authentication.User
import com.telematics.domain.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    private val userRepo: UserRepo
) : ViewModel() {

    fun setDeviceTokenForTrackingApi() {
        flow {
            val deviceToken = userRepo.getDeviceToken()
            val value = trackingUseCase.setDeviceToken(deviceToken)
            emit(value)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun checkPermissions(): LiveData<Result<Boolean>> {
        val permissionsState = MutableLiveData<Result<Boolean>>()
        trackingUseCase.checkPermissions()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(permissionsState)
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun startWizard(activity: Activity) {
        trackingUseCase.startWizard(activity)
    }

    fun enableTracking() {
        trackingUseCase.enableTrackingSDK()
    }

    fun setIntentForNotification(intent: Intent) {

        trackingUseCase.setIntentForNotification(intent)
    }

    fun getUser(): LiveData<Result<User>> {

        val userState = MutableLiveData<Result<User>>()
        flow {
            val user = userRepo.getUser()
            emit(user)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(userState)
            .launchIn(viewModelScope)
        return userState
    }

    fun getProfilePicture(context: Context): LiveData<Result<Bitmap?>> {

        val profilePictureState = MutableLiveData<Result<Bitmap?>>()
        flow {
            val bitmapCache = userRepo.getUserPicture(context)
            emit(bitmapCache)
        }
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(profilePictureState)
            .launchIn(viewModelScope)

        return profilePictureState
    }
}