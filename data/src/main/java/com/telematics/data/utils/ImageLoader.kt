package com.telematics.data.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImageLoader {

    suspend fun loadImage(url: String, body: String, token: String): Bitmap? {

        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor(/*fileLogger*/)
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addNetworkInterceptor(httpLoggingInterceptor)
        val client = builder.build()

        val newBody = body.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
        val request = Request.Builder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .url(url)
            .post(newBody)
            .build()
        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code in (400..500)) {
                        continuation.resumeWith(Result.success(null))
                        return
                    }
                    val options = BitmapFactory.Options()
                    options.inMutable = true
                    val b =
                        BitmapFactory.decodeStream(response.body?.byteStream(), null, options)
                    // update last trip token
                    try {
                        continuation.resumeWith(Result.success(b))
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }


}