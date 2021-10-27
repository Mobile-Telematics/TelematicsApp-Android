package com.telematics.data.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.*
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

        val body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), body)
        val request = Request.Builder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .url(url)
            .post(body)
            .build()
        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code() in (400..500)) {
                        continuation.resumeWith(Result.success(null))
                        return
                    }
                    val options = BitmapFactory.Options()
                    options.inMutable = true
                    val b =
                        BitmapFactory.decodeStream(response.body()!!.byteStream(), null, options)
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