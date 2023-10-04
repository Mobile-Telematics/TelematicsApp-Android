package com.telematics.data.usecase

import android.content.Context
import android.os.Build
import com.telematics.data.extentions.isNotificationGranted
import com.telematics.domain.repository.SettingsRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class NotificationPermissionUseCase @Inject constructor(
    private val settingsRepo: SettingsRepo,
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(): Flow<Boolean> {
        return flow {
            emit(
                when {
                    isNotificationGranted(context)
                            || settingsRepo.isNotificationPermissionCompleted() -> {
                        false
                    }

                    Build.VERSION.SDK_INT >= 33 -> {
                        true
                    }

                    else -> {
                        false
                    }
                }
            )
        }
    }
}