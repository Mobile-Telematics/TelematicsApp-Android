package com.telematics.data.extentions

import android.util.Log
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> com.telematicssdk.auth.external.Task<T>.await(): T =
    suspendCoroutine { continuation ->
        onSuccess {
            Log.d("com.telematicssdk.auth", "TASK: await: onSuccess")
            continuation.resume(it)
        }
        onError {
            Log.d("com.telematicssdk.auth", "TASK: await: onError ${it.message}")
            continuation.resumeWithException(it)
        }
    }