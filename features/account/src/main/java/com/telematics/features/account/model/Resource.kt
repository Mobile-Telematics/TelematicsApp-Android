package com.telematics.features.account.model

sealed class Resource<T> {
    class Success<T>(val data: T? = null) : Resource<T>()
    class Failure<T>(val error: Throwable? = null) : Resource<T>()
}