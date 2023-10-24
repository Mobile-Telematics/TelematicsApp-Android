package com.telematics.data.extentions

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

fun getMediaPermissions() =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            arrayOf(CAMERA, READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            arrayOf(CAMERA, READ_MEDIA_IMAGES)
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            arrayOf(CAMERA, READ_EXTERNAL_STORAGE)
        }

        else -> {
            arrayOf()
        }
    }
fun getNotGrantedPermissions(context: Context, permissions: List<String>): ArrayList<String> {
    val notGrantedPermissions = arrayListOf<String>()

    permissions.forEach {
        if (ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notGrantedPermissions.add(it)
        }
    }

    return notGrantedPermissions
}

fun getNotGrantedNotificationPermissions(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getNotGrantedPermissions(
            context,
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        )
    } else {
        listOf()
    }

fun isNotificationGranted(context: Context) =
    getNotGrantedNotificationPermissions(context).isEmpty()

fun isExactAlarmGranted(context: Context) : Boolean =
    when {
        !retrievePermissions(context).contains(Manifest.permission.SCHEDULE_EXACT_ALARM) -> true
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> true
        else -> {
            val alarmManager = context.getSystemService<AlarmManager>()
            alarmManager?.canScheduleExactAlarms() ?: false
        }
    }

@Suppress("DEPRECATION")
fun retrievePermissions(context: Context): Array<String> {
    val pkgName = context.packageName
    return try {
        context
            .packageManager
            .run {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                    getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
                } else {
                    getPackageInfo(pkgName, PackageManager.PackageInfoFlags.of( PackageManager.GET_PERMISSIONS.toLong()))
                }.requestedPermissions
            }

    } catch (e: PackageManager.NameNotFoundException) {
        // Better to throw a custom exception since this should never happen unless the API has changed somehow.
        emptyArray()
    }
}
