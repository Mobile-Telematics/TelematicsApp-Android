package com.telematics.data.usecase

import android.content.Context
import com.telematics.data.extentions.isExactAlarmGranted
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class AlarmPermissionUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(): Flow<Boolean> {
        return flow {
            emit(
                isExactAlarmGranted(context)
            )
        }
    }
}