package com.telematics.data.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionUtils {

    private val TAG = "PermissionUtils"

    private var permissions = emptyArray<String>()

    private lateinit var requestMultiplePermissionsContract: ActivityResultContracts.RequestMultiplePermissions
    private var multiplePermissionActivityResultLauncher: ActivityResultLauncher<Array<String>>? =
        null

    private var listener: (allIsGranted: Boolean) -> Unit = {}

    fun setPermissionListener(listener: (allIsGranted: Boolean) -> Unit = {}) {
        this.listener = listener
    }

    fun registerContract(fragment: Fragment) {

        requestMultiplePermissionsContract = ActivityResultContracts.RequestMultiplePermissions()
        multiplePermissionActivityResultLauncher =
            fragment.registerForActivityResult(requestMultiplePermissionsContract) { map ->
                val allIsGranted = map.map { it.value }.find { it == false } == null
                listener(allIsGranted)
            }
    }


    fun askPermissions(context: Context, permissions: Array<String>) {

        this.permissions = permissions

        if (!hasPermissions(context)) {
            multiplePermissionActivityResultLauncher!!.launch(permissions)
        } else {
            listener(true)
        }
    }

    private fun hasPermissions(context: Context): Boolean {

        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}