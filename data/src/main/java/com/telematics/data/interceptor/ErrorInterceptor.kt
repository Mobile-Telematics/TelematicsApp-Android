package com.telematics.data.interceptor

import com.telematics.data.api.errors.ApiError
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ErrorInterceptor @Inject constructor(
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val code = response.code
        if (code != 200) {
            throw ApiError(code, response.message)
        }
        return response
    }
}