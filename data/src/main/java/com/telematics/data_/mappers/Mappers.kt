package com.telematics.data_.mappers

import com.telematics.data_.model.SessionData
import com.telematics.data_.model.rest.ApiResult
import java.util.*

fun ApiResult.toSessionData(): SessionData {
    return SessionData(
        this.deviceToken,
        "",
        this.refreshToken,
        Date().time + this.accessToken.expiresIn
    )
}