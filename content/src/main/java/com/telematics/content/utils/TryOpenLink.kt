package com.telematics.content.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil

class TryOpenLink(private val context: Context) {

    fun open(link: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            when {
                URLUtil.isValidUrl(link) -> Uri.parse(link)
                else -> Uri.parse(link)
            }
        )
        context.startActivity(intent)
    }
}