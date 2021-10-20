package ui.step1_vehicles.add_vehicle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentObdAddVehicleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ObdAddVehicleFragment : BaseFragment() {

    @Inject
    lateinit var vehicleViewModel: VehicleViewModel
    private var inputVehicle = Vehicle()
    private var isCanChangeMileage = true
    private var currentManufacturerId = -1
    private var currentModelId = -1

    private lateinit var binding: FragmentObdAddVehicleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdAddVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()

        setListeners()
    }

    private fun setListeners() {

        binding.createButton.setOnClickListener {
            saveVehicle()
        }

        binding.vehicleInitialMileage.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                checkMileageEdit()
            }
        }

        binding.vehicleInitialMileage.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            true
        }

        binding.vehicleInitialMileage.setOnClickListener { v ->
            checkMileageEdit()
        }

        binding.vehicleManufacturer.setOnClickListener {
            observeManufacturers()
        }

        binding.vehicleModel.setOnClickListener {
            observeModels(currentManufacturerId)
        }

        binding.vehicleModelChoose.setOnReturnData(object : OBDVehicleChooseView.OnReturnData {
            override fun onReturn(id: Int, name: String, type: OBDVehicleChooseView.Type?) {
                binding.vehicleModelChoose.hide()
                when (type) {
                    OBDVehicleChooseView.Type.MANUFACTURER -> {
                        if (currentManufacturerId != id) {
                            currentModelId = -1
                            binding.vehicleModel.setText("")
                        }

                        currentManufacturerId = id
                        binding.vehicleManufacturer.setText(name)
                    }
                    OBDVehicleChooseView.Type.MODEL -> {
                        currentModelId = id
                        binding.vehicleModel.setText(name)
                    }
                }
            }
        })

        binding.vehicleCarYear.doOnTextChanged { text, start, count, after ->

            if (text?.isEmpty() == true) {
                return@doOnTextChanged
            }

            val year =
                if (text.toString().isNotBlank())
                    try {
                        text.toString().toInt()
                    } catch (e: Exception) {
                        null
                    }
                else -1

            if (year == null) {
                binding.vehicleCarYear.error = "Invalid year"
            } else {
                when {
                    year > Calendar.getInstance().get(Calendar.YEAR) -> {
                        binding.vehicleCarYear.error = "Invalid year"
                    }
                    year <= 0 -> {
                        binding.vehicleCarYear.error = "Invalid year"
                    }
                }
            }
        }

        binding.vehicleModelChoose.hide()
    }

    private fun saveVehicle() {

        val vehicle = getUpdatedVehicle()
        if (!checkFields(vehicle)) {
            return
        }

        showProgress(true)

        val liveData =
            vehicleViewModel.createVehicle(vehicle)

        liveData.observe(viewLifecycleOwner) { result ->
            showProgress(false)
            result.onSuccess {
                finish()
            }
            result.onFailure {
                showMessage(R.string.error_unknown)
            }
        }
    }

    private fun getUpdatedVehicle(): Vehicle {

        inputVehicle.plateNumber = binding.vehicleLicencePlate.text.toString()
        inputVehicle.vin = binding.vehicleVinNumber.text.toString()
        inputVehicle.manufacturer = binding.vehicleManufacturer.text.toString()
        inputVehicle.manufacturerId = currentManufacturerId
        inputVehicle.model = binding.vehicleModel.text.toString()
        inputVehicle.modelId = currentModelId
        inputVehicle.name = binding.vehicleCarName.text.toString()

        val year =
            if (binding.vehicleCarYear.text.toString().isNotBlank())
                try {
                    binding.vehicleCarYear.text.toString().toInt()
                } catch (e: Exception) {
                    null
                }
            else -1

        if (year == null) {
            binding.vehicleCarYear.error = "Invalid year"
        } else
            inputVehicle.carYear = year

        if (isCanChangeMileage)
            inputVehicle.initialMileage = binding.vehicleInitialMileage.text.toString()
        else
            inputVehicle.initialMileage = null

        return inputVehicle
    }

    private fun deleteVehicle() {

        showProgress(true)
        vehicleViewModel.deleteVehicle(inputVehicle).observe(viewLifecycleOwner) { result ->

            showProgress(false)
            result.onSuccess {
                finish()
            }
            result.onFailure {
                showMessage(R.string.error_unknown)
            }
        }
    }

    private fun observeManufacturers() {

        binding.vehicleManufacturer.error = null

        vehicleViewModel.getManufacturers().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.vehicleModelChoose.setManufacturers(it)
                binding.vehicleModelChoose.show()
            }
        }
    }

    private fun observeModels(id: Int) {

        if (currentManufacturerId == 0 || currentManufacturerId == -1) {
            binding.vehicleManufacturer.error = "Select the manufacturer"
            return
        }

        vehicleViewModel.getModels(id).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.vehicleModelChoose.setModels(it)
                binding.vehicleModelChoose.show()
            }
        }
    }

    /*ui*/
    private fun showProgress(show: Boolean) {

        binding.vehicleLoadingView.isVisible = show
    }

    private fun checkFields(vehicle: Vehicle): Boolean {

        var result = true

        binding.vehicleVinNumber.error = null
        binding.vehicleManufacturer.error = null
        binding.vehicleCarYear.error = null

        val vin = vehicle.vin?.replace(" ", "")
        binding.vehicleVinNumber.setText(vin)
        vehicle.vin = vin
        if (!vehicle.vin.isNullOrBlank() && vehicle.vin?.length != 17) {
            binding.vehicleVinNumber.error = "VIN must have length of 17 symbols"
            result = false
        }
        if (vehicle.manufacturer.isNullOrEmpty()) {
            binding.vehicleManufacturer.error = "Select the manufacturer"
            result = false
        }
        when {
            vehicle.carYear != null
                    && vehicle.carYear ?: 0 > Calendar.getInstance().get(Calendar.YEAR) -> {
                binding.vehicleCarYear.error = "Invalid year"
                result = false
            }
            vehicle.carYear != null && vehicle.carYear ?: 0 <= 0 && vehicle.carYear != -1 -> {
                binding.vehicleCarYear.error = "Invalid year"
                result = false
            }
        }

        return result
    }

    private fun checkMileageEdit() {

        if (!isCanChangeMileage) {
            hideKeyboard()
            showMessage("Mileage can't be changed after adding a car")
            binding.vehicleInitialMileage.isEnabled = false
            binding.vehicleInitialMileage.clearFocus()
            binding.root.clearFocus()
            binding.root.hasFocus()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.vehicleInitialMileage.isEnabled = true
            }, 500)
        }
    }

    private fun finish() {
        onBackPressed()
    }
}