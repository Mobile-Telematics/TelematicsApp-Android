package com.telematics.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.telematics.domain.repository.SettingsRepo

class SettingsRepoImpl : SettingsRepo {

    override fun getTelematicsLink(context: Context): String {
        return telematicsSettingsLink(context)
    }

    private fun telematicsSettingsLink(ctx: Context): String {
        if (isMIUI(ctx)) return "http://help.telematicssdk.com/en/articles/3732818-android-9-settings-guide-miui-11-xiaomi"
        return when (android.os.Build.VERSION.SDK_INT) {
            android.os.Build.VERSION_CODES.Q -> "http://help.telematicssdk.com/en/articles/3732972-android-10-settings-guide"
            android.os.Build.VERSION_CODES.R -> "https://help.telematicssdk.com/en/articles/4885122-android-11-settings-guide"
            else -> "http://help.telematicssdk.com/en/articles/3728524-android-9-settings-guide"
        }
    }

    private fun isMIUI(ctx: Context): Boolean {
        return isIntentResolved(
            ctx,
            Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT)
        )
                || isIntentResolved(
            ctx,
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            )
        )
                || isIntentResolved(
            ctx, Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST").addCategory(
                Intent.CATEGORY_DEFAULT
            )
        )
                || isIntentResolved(
            ctx,
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.powercenter.PowerSettings"
                )
            )
        )
    }

    private fun isIntentResolved(ctx: Context, intent: Intent?): Boolean {
        return intent != null && ctx.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null
    }
}