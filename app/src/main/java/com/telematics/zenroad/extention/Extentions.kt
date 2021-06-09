package com.telematics.zenroad.extention

import android.util.Patterns
import android.view.View
import com.telematics.domain.model.SessionData

fun View.setVisible(visible: Boolean?) {
    this.visibility = when (visible) {
        true -> View.VISIBLE
        false -> View.INVISIBLE
        null -> View.GONE
    }
}

fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun SessionData.isEmpty(): Boolean {
    return accessToken.isBlank()
}
