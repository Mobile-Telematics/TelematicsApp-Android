package com.telematics.domain_.listener

import com.telematics.domain_.error.ErrorCode
import com.telematics.domain_.model.SessionData

interface AuthenticationListener {
    fun onLoginSuccess(sessionData: SessionData) {}
    fun onLoginFailure(errorCode: ErrorCode) {}
    fun onLogout() {}
}