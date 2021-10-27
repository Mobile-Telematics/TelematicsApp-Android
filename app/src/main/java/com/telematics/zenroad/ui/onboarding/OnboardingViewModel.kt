package com.telematics.zenroad.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telematics.domain.repository.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    fun onboardingScreenComplete() {

        flow {
            emit(userRepo.setNeedOnboarding(false))
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}