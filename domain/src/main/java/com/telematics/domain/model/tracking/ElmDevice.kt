package com.telematics.domain.model.tracking

data class ElmDevice(
    var connectedState: Boolean,
    var device: android.bluetooth.BluetoothDevice?,
    var deviceMacAddress: String?,
    var deviceName: String?,
    var rssi: Int
)
