package com.telematics.data.extentions

import com.raxeltelematics.v2.sdk.services.main.elm.ElmDevice
import com.raxeltelematics.v2.sdk.services.main.elm.managers.ElmLinkingError
import com.raxeltelematics.v2.sdk.services.main.elm.managers.ElmLinkingListener
import com.raxeltelematics.v2.sdk.services.main.elm.managers.VehicleElmManager
import com.telematics.data.mappers.toElmDevice
import com.telematics.domain.model.tracking.ElmManagerLinkingResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun VehicleElmManager.awaitLinkingListener(): ElmManagerLinkingResult =
    suspendCoroutine { continuation ->

        registerLinkingListener(object : ElmLinkingListener {
            override fun onLinkingComplete(vehicleToken: String, elmMAC: String) {
                ElmManagerLinkingResult(
                    isLinkingComplete = true,
                    vehicleToken,
                    elmMAC,
                    isScanningComplete = false,
                    null,
                    null
                ).apply {
                    continuation.resume(this)
                }
            }

            override fun onLinkingFailed(error: ElmLinkingError) {
                ElmManagerLinkingResult(
                    isLinkingComplete = false,
                    null,
                    null,
                    isScanningComplete = false,
                    null,
                    error.name
                ).apply {
                    continuation.resume(this)
                }
            }

            override fun onScanningComplete(foundDevices: List<ElmDevice>) {
                ElmManagerLinkingResult(
                    isLinkingComplete = false,
                    null,
                    null,
                    isScanningComplete = true,
                    foundDevices.map { it.toElmDevice() },
                    null
                ).apply {
                    continuation.resume(this)
                }
            }

            override fun onScanningFailed(error: ElmLinkingError) {
                ElmManagerLinkingResult(
                    isLinkingComplete = false,
                    null,
                    null,
                    isScanningComplete = false,
                    null,
                    error.name
                ).apply {
                    continuation.resume(this)
                }
            }
        })
    }