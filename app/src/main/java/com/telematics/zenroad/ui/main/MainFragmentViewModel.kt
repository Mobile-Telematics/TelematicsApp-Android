package com.telematics.zenroad.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.data.extentions.setLiveDataForResult
import com.telematics.data.tracking.TrackingUseCase
import com.telematics.data.usecase.AlarmPermissionUseCase
import com.telematics.data.usecase.NotificationPermissionUseCase
import com.telematics.domain.model.authentication.User
import com.telematics.domain.repository.SettingsRepo
import com.telematics.domain.repository.UserRepo
import com.telematics.features.account.use_case.LoginUseCase
import com.telematics.zenroad.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    private val trackingUseCase: TrackingUseCase,
    private val userRepo: UserRepo,
    private val loginUseCase: LoginUseCase,
    private val notificationPermissionUseCase: NotificationPermissionUseCase,
    private val alarmPermissionUseCase: AlarmPermissionUseCase,
    private val settingsRepo: SettingsRepo,
) : ViewModel() {

    fun initTrackingApi(context: Context): LiveData<Unit> {
        val state = MutableLiveData<Unit>()
        flow {
            val deviceToken = userRepo.getDeviceToken()
            trackingUseCase.initializeSdk()
            trackingUseCase.setDeviceToken(deviceToken)
            setIntentForNotification(context)
            emit(Unit)
        }
            .flowOn(Dispatchers.IO).onEach {
                state.postValue(it)
            }
            .launchIn(viewModelScope)
        return state
    }

    private fun setIntentForNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        trackingUseCase.setIntentForNotification(intent)
    }

    fun checkPermissions(): LiveData<Result<Boolean>> {
        val permissionsState = MutableLiveData<Result<Boolean>>()
        trackingUseCase.checkPermissions()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(permissionsState)
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun checkAlarmPermissions(): LiveData<Boolean> {
        val permissionsState = MutableLiveData<Boolean>()
        alarmPermissionUseCase()
            .flowOn(Dispatchers.IO)
            .onEach {
                permissionsState.postValue(it)
            }
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun checkNotificationPermissions(): LiveData<Boolean> {
        val permissionsState = MutableLiveData<Boolean>()
        notificationPermissionUseCase()
            .flowOn(Dispatchers.IO)
            .onEach {
                permissionsState.postValue(it)
            }
            .launchIn(viewModelScope)
        return permissionsState
    }

    fun onNotificationPermissionRequested() {
        flow {
            settingsRepo.setNotificationPermissionCompleted()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
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

    fun getProfilePicture(): LiveData<Result<Bitmap?>> {

        val profilePictureState = MutableLiveData<Result<Bitmap?>>()
        loginUseCase.getProfilePicture()
            .flowOn(Dispatchers.IO)
            .setLiveDataForResult(profilePictureState)
            .launchIn(viewModelScope)

        return profilePictureState
    }


    ///

    private val saveStateBundle = MutableLiveData<Bundle>()
    val getSaveStateBundle: LiveData<Bundle>
        get() {
            return saveStateBundle
        }

    fun saveCurrentBottomMenuState(state: Int) {

        val bundle = bundleOf("bottom_state" to state)
        saveStateBundle.value = bundle
    }

    fun bundleToListSize(bundle: Bundle): Int {

        return bundle.getInt("bottom_state", 0)
    }
}