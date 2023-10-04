package com.telematics.content.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil

class TryOpenLink(private val context: Context) {
    fun open(link: String): Boolean {

        if (!URLUtil.isValidUrl(link)) return false

        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            setDataAndType(Uri.parse(link), "text/html")
        }

        return try {
            context.startActivity(openIntent)
            true
        } catch (ex: Exception) {
            try {
                openIntent.addCategory(Intent.CATEGORY_BROWSABLE)
                context.startActivity(openIntent)
                true
            } catch (ex: Exception) {
                false
            }
        }
    }
}