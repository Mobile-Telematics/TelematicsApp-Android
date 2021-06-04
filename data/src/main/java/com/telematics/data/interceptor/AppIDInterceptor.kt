package com.telematics.data.interceptor

import com.telematics.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AppIDInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().addHeader("AppId", BuildConfig.APP_ID).build()
        return chain.proceed(request)
    }
}