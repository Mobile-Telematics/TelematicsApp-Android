package ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.domain.model.tracking.ElmDevice

class CommonObdViewModel : ViewModel() {

    private var selectedCarLiveData = MutableLiveData<Vehicle?>()
    private var selectedImagePathLiveData = MutableLiveData<String?>()
    private var foundedDevicesLiveData = MutableLiveData<List<ElmDevice>?>()
    private var connectedDeviceLiveData = MutableLiveData<Pair<String, String>>()

    /*vehicle data*/
    fun setVehicle(car: Vehicle) {
        selectedCarLiveData.value = car
    }

    fun getVehicle(): Vehicle? = selectedCarLiveData.value

    /*odometer image data*/
    fun setOdometerImage(path: String) {
        selectedImagePathLiveData.value = path
    }

    fun getImagePath(): String? = selectedImagePathLiveData.value

    /*device list data*/
    fun setFoundedDevices(devices: List<ElmDevice>) {

        foundedDevicesLiveData.value = devices
    }

    fun getFoundedDevices(): List<ElmDevice>? = foundedDevicesLiveData.value

    /*connected device data*/
    fun setConnectedDevice(vehicleToken: String?, elmMAC: String?) {

        connectedDeviceLiveData.value = Pair(vehicleToken ?: "", elmMAC ?: "")
    }

    fun getConnectedDevice(): Pair<String, String>? = connectedDeviceLiveData.value
}