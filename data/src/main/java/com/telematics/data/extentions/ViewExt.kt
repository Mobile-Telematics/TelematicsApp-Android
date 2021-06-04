package com.telematics.data.extentions

import android.content.res.ColorStateList
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.telematics.data.R

fun ProgressBar.setProgressWithColor(p: Int) {

    val newP = if (p > 100) 100 else if (p < 0) 0 else p
    progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, p.getColorByScore()))
    progress = newP
}

fun Int.getColorByScore(): Int {
    return when (if (this > 100) 100 else if (this < 0) 0 else this) {
        in 81..100 -> {
            R.color.colorSpeedTypeNormal
        }
        in 61..80 -> {
            R.color.colorSpeedTypeMid
        }
        in 40..60 -> {
            R.color.colorSpeedTypeMid2
        }
        in 0..39 -> {
            R.color.colorSpeedTypeHigh
        }
        else -> R.color.colorSpeedTypeNormal
    }
}