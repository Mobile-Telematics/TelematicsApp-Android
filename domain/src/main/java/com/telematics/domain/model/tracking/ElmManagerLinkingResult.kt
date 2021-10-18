package com.telematics.domain.model.tracking

class ElmManagerLinkingResult(
    val isLinkingComplete: Boolean,
    val vehicleToken: String?,
    val elmMAC: String?,

    val isScanningComplete: Boolean,
    val foundDevices: List<ElmDevice>?,

    val error: String?
)