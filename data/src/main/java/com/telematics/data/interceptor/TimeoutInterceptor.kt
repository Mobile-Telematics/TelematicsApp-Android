package com.telematics.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class TimeoutInterceptor(
    private val connectTimeoutMillis: Int? = null,
    private val readTimeoutMillis: Int? = null,
    private val writeTimeoutMillis: Int? = null
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var connectTimeout = chain.connectTimeoutMillis()
        var readTimeout = chain.readTimeoutMillis()
        var writeTimeout = chain.writeTimeoutMillis()

        connectTimeoutMillis?.also { connectTimeout = it }
        readTimeoutMillis?.also { readTimeout = it }
        writeTimeoutMillis?.also { writeTimeout = it }

        return chain
            .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .proceed(request)
    }
}