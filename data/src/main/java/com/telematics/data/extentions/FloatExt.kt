package com.telematics.data.extentions

import java.text.DecimalFormat
import java.util.*

fun Float.format(format: String = "0.0"): String {
    Locale.setDefault(Locale.US)
    return DecimalFormat(format).format(this)
}