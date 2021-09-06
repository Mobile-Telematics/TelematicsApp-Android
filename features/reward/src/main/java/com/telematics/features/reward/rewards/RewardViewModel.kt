package com.telematics.features.reward.rewards

import androidx.lifecycle.ViewModel
import com.telematics.domain.repository.SessionRepo
import javax.inject.Inject

class RewardViewModel @Inject constructor(
    private val sessionRepo: SessionRepo
) : ViewModel() {

    val isNeedShowRewardsInvite: Boolean
        get() {
            return !sessionRepo.isRewardInviteScreenOpened()
        }

    fun inviteScreenClosed() {

        sessionRepo.saveStateForRewardInviteScreen()
    }

}