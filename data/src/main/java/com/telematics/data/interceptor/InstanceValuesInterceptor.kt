package com.telematics.data.interceptor

import com.telematics.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class InstanceValuesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("InstanceId", BuildConfig.INSTANCE_ID)
            .addHeader("InstanceKey", BuildConfig.INSTANCE_KEY)
            .build()
        return chain.proceed(request)
    }
}