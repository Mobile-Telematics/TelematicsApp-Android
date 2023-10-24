package com.telematics.sdkhelper

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationManagerCompat
import com.telematics.core.sdkhelper.R

private const val SDK_SERVICE_NOTIFICATION_CHANNEL = "Tracker service ID"
private const val NOTIFICATION_SETTINGS_KEY = "notificationSettingsKey"
private fun isPersistentSdkNotificationAllowed(context: Context): Boolean {
    val manager = NotificationManagerCompat.from(context)

    if (!manager.areNotificationsEnabled()) {
        return false
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            manager.getNotificationChannel(SDK_SERVICE_NOTIFICATION_CHANNEL)
        return channel == null || channel.importance != NotificationManager.IMPORTANCE_NONE
    }
    return true
}

private fun isPersistentSdkNotificationChannelEnabled(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = NotificationManagerCompat.from(context)
        val channel =
            manager.getNotificationChannel(SDK_SERVICE_NOTIFICATION_CHANNEL)
        channel != null
    } else {
        false
    }
}

private fun sendIntent(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val i: Intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, SDK_SERVICE_NOTIFICATION_CHANNEL)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        context.startActivity(i)
    }
}

private fun showDialog(context: Context) {

    val spannableTitle =
        SpannableString(context.getString(R.string.dialog_sdk_persistent_notification_title)).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    val spannableButton = SpannableString(context.getString(R.string.dialog_sdk_settings)).apply {
        setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    val builder = AlertDialog.Builder(context)
    builder
        .setTitle(spannableTitle)
        .setMessage(R.string.dialog_sdk_persistent_notification_instructions)
        .setPositiveButton(spannableButton) { _, _ ->
            sendIntent(context)
        }
        .setNegativeButton(R.string.dialog_sdk_skip) { _, _ ->

        }
        .setCancelable(true)
    val dialog = builder.create()
    dialog.show()
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    val applicationLabel = context.applicationInfo.loadLabel(context.packageManager).toString()
    return context.getSharedPreferences(
        "${applicationLabel}_damoov_sdk_utils_shared_prefs",
        Context.MODE_PRIVATE
    )
}

private fun isNotificationSettingsCompleted(context: Context) =
    provideSharedPreferences(context).getBoolean(NOTIFICATION_SETTINGS_KEY, false)

private fun setNotificationSettingsCompleted(context: Context) {
    provideSharedPreferences(context).edit().putBoolean(NOTIFICATION_SETTINGS_KEY, true)
        .apply()
}

fun hidePersistentSdkNotifications(context: Context) {
    if (
        isPersistentSdkNotificationChannelEnabled(context)
        && !isNotificationSettingsCompleted(context)
        && isPersistentSdkNotificationAllowed(context)
    ) {
        showDialog(context)
        setNotificationSettingsCompleted(context)
    }
}