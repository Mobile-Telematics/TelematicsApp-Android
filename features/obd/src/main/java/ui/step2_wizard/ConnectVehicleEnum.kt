package ui.step2_wizard

import com.telematics.obd.R

enum class ConnectVehicleEnum(val layout: Int) {
    INSTALL_YOUR_DEVICE(R.layout.obd_first_step),
    TURN_ON_VEHICLE(R.layout.obd_second_step),
    PHOTO_PHOTO_OF_ODOMETR(R.layout.obd_third_step)
}