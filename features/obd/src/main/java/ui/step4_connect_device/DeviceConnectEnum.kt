package ui.step4_connect_device

import com.telematics.obd.R


enum class DeviceConnectEnum(val pageResId : Int) {
   PREPARE(R.layout.odb_prepare),
   TURN_ON_BLUETOOTH(R.layout.odb_turn_on_bluetooth)
}