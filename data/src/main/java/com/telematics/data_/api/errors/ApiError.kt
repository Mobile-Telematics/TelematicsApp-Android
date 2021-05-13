package com.telematics.data_.api.errors

data class ApiError (
    val errorCode: Int,
    val msg: String? = null,
    //val details: List<ErrorDetailsData>? = null
) : Throwable()