package com.telematics.data.extentions

import java.text.DecimalFormat
import java.util.*

fun Double.format(format: String = "0.0"): String {
    Locale.setDefault(Locale.US)
    return DecimalFormat(format).format(this)
}

fun Double.toMiles(): Double {
    return this / 1.609f
}