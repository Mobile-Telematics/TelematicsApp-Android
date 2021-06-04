package com.telematics.authentication.extention

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException

suspend fun <T> Task<T>.await(): T? {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException(
                    "Task $this was cancelled normally."
                )
            } else {
                result
            }
        } else {
            throw e
        }
    } else throw CancellationException(
        "Task $this was cancelled normally."
    )
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}