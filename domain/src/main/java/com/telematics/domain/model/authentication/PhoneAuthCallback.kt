package com.telematics.domain.model.authentication

class PhoneAuthCred<T>(val credential: T, val phone: String)

interface PhoneAuthCallback {
    fun onCodeSend(verificationId: String)
    fun onCompleted(result: PhoneAuthCred<*>)
    fun onTimeout(){}
    fun onFailure(e: Exception)
}