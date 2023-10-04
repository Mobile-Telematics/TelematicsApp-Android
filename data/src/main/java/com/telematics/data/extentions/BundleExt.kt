package com.telematics.data.extentions

import android.os.Build
import android.os.Bundle
import java.io.Serializable

fun <T : Serializable?> Bundle.getSerializableCompat(
    key: String?,
    clazz: Class<T>
): T? {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            getSerializable(key, clazz)
        }

        else -> {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            getSerializable(key) as T?
        }
    }
}