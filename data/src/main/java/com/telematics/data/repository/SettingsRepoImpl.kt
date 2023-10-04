package com.telematics.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.telematics.data.BuildConfig
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandState
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.domain.repository.SettingsRepo
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepo {

    private val dateMeasureKey = "dateMeasureKey"
    private val distanceMeasureKey = "distanceMeasureKey"
    private val timeMeasureKey = "timeMeasureKey"
    private val trackingStateKey = "trackingStateKey"
    private val onDemandDutyStateKey = "onDemandDutyStateKey"
    private val onDemandLastCurrentJobKey = "onDemandLastCurrentJobKey"
    private val notificationPermissionsKey = "notificationPermissionsKey"

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

    override fun getDateMeasure(): DateMeasure {

        val data = sharedPreferences.getString(dateMeasureKey, DateMeasure.default.value)
        return DateMeasure.parse(data)
    }

    override fun getDistanceMeasure(): DistanceMeasure {

        val data =
            sharedPreferences.getString(distanceMeasureKey, DistanceMeasure.default.value)
        return DistanceMeasure.parse(data)
    }

    override fun getTimeMeasure(): TimeMeasure {

        val data = sharedPreferences.getString(timeMeasureKey, TimeMeasure.default.value)
        return TimeMeasure.parse(data)
    }

    override fun setDateMeasure(dateMeasure: DateMeasure) {

        sharedPreferences.edit()
            .putString(dateMeasureKey, dateMeasure.value)
            .apply()
    }

    override fun setDistanceMeasure(distanceMeasure: DistanceMeasure) {

        sharedPreferences.edit()
            .putString(distanceMeasureKey, distanceMeasure.value)
            .apply()
    }

    override fun setTimeMeasure(timeMeasure: TimeMeasure) {

        sharedPreferences.edit()
            .putString(timeMeasureKey, timeMeasure.value)
            .apply()
    }

    override fun getTrackingState(): TrackingState {

        val data =
            sharedPreferences.getInt(trackingStateKey, TrackingState.AUTO.index)
        return TrackingState.parse(data)
    }

    override fun setTrackingState(mode: TrackingState) {

        sharedPreferences.edit()
            .putInt(trackingStateKey, mode.index)
            .apply()
    }

    override fun getDemandDutyState(): OnDemandState {
        //onDemandDutyStateKey

        val data =
            sharedPreferences.getString(onDemandDutyStateKey, OnDemandState.OFFLINE.toString())
        return OnDemandState.translation(data)
    }

    override fun setDemandDutyState(state: OnDemandState) {

        sharedPreferences.edit()
            .putString(onDemandDutyStateKey, state.toString())
            .apply()
    }

    override fun setOnDemandLastCurrentJob(job: DashboardOnDemandJob) {

        sharedPreferences.edit()
            .putLong(onDemandLastCurrentJobKey, job.createTime)
            .apply()
    }

    override fun getOnDemandLastCurrentJob(): Long {

        return sharedPreferences.getLong(onDemandLastCurrentJobKey, -1L)
    }

    override fun isNotificationPermissionCompleted(): Boolean =
        sharedPreferences.getBoolean(notificationPermissionsKey, !BuildConfig.REQUEST_NOTIFICATION_PERMISSION)

    override fun setNotificationPermissionCompleted() {
        sharedPreferences.edit().putBoolean(notificationPermissionsKey, true).apply()
    }
}