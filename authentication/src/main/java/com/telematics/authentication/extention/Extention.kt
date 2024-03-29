package com.telematics.authentication.extention

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.telematics.authentication.exception.AuthErrorCode
import com.telematics.authentication.exception.AuthException
import com.telematics.authentication.mapper.Mapper
import com.telematics.authentication.model.UserDatabase
import com.telematics.domain.model.authentication.PhoneAuthResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
    addOnCompleteListener { task ->

        Log.d("FIREBASE await", "isSuccessful: ${task.isSuccessful} task: ${task.exception}")

        if (task.isSuccessful) {
            continuation.resume(task.result as T)
        } else {
            val error = task.exception
            val errorCode = Mapper.getErrorCodeByException(error)
            continuation.resumeWithException(AuthException(errorCode))
        }
    }
}

suspend fun DatabaseReference.await(): UserDatabase = suspendCoroutine { continuation ->
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FIREBASE Database await", "onDataChange")
            try {
                val userDatabase = snapshot.getValue(UserDatabase::class.java)
                userDatabase?.deviceToken?.let { deviceToken ->
                    Log.d("FIREBASE Database await", "onDataChange deviceToken:${deviceToken}")
                    continuation.resume(userDatabase)
                } ?: run {
                    Log.d("FIREBASE Database await", "onDataChange deviceToken null")
//                continuation.resumeWithException(AuthException(AuthErrorCode.EMPTY_DEVICE_TOKEN))
                    continuation.resume(UserDatabase())
                }
            } catch (e: Exception) {
                continuation.resume(UserDatabase())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("FIREBASE Database await", "onCancelled error: ${error.message}")
            continuation.resumeWithException(AuthException(AuthErrorCode.EMPTY_DEVICE_TOKEN))
        }
    })
}

suspend fun PhoneAuthOptions.Builder.await(): Pair<PhoneAuthResult, Any?> =
    suspendCoroutine { continuation ->
        setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                val result = Pair(PhoneAuthResult.TIMEOUT, null)
                continuation.resume(result)
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                val result = Pair(PhoneAuthResult.TIMEOUT, null)
                continuation.resume(result)
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val result = Pair(PhoneAuthResult.TIMEOUT, p0)
                continuation.resume(result)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                val errorCode = Mapper.getErrorCodeByException(p0)
                val authException = AuthException(errorCode)
                continuation.resumeWithException(authException)
            }
        })
    }

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}
