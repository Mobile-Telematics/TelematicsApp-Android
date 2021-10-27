package com.telematics.data.extentions

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build

fun Resources.color(colorRes: Int) =
    if (Build.VERSION.SDK_INT >= 23) {
        this.getColor(colorRes, null)
    } else {
        this.getColor(colorRes)
    }

fun Resources.drawable(drawableRes: Int, context: Context): Drawable =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.getDrawable(drawableRes, context.theme)
    } else {
        this.getDrawable(drawableRes)
    }