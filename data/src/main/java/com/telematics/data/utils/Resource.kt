package com.telematics.data.utils

sealed class Resource<T> {

    class Success<T>(val data: T? = null) : Resource<T>()

    class Loading<T> : Resource<T>()

    class Failure<T>(val error: Throwable? = null) : Resource<T>()
}