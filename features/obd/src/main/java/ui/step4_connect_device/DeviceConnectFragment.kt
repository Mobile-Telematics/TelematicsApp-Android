package ui.step4_connect_device

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.tracking.ElmDevice
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentObdDeviceConnectBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DeviceConnectFragment : BaseFragment() {

    @Inject
    lateinit var obdViewModel: ObdViewModel
    private lateinit var commonObdViewModel: CommonObdViewModel

    private lateinit var binding: FragmentObdDeviceConnectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdDeviceConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)

        binding.obdDeviceConnectSearchLayout.takingTooLongText.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.obd_help_link)))
            startActivity(browserIntent)
        }
        binding.obdDeviceConnectSearchLayout.root.setOnClickListener { }

        registerEmlManagerLinkingResult()
        initPager()
    }

    private fun initPager() {

        binding.obdDeviceConnectViewPager.adapter = DeviceConnectedWizardAdapter().apply {
            setOnClick(object : DeviceConnectedWizardAdapter.OnSearchClickListener {
                override fun search() {
                    startScan()
                }
            })
        }
        binding.obdDeviceConnectPagesIndicator.setViewPager(binding.obdDeviceConnectViewPager)
    }

    private fun registerEmlManagerLinkingResult() {

        obdViewModel.registerElmManagerLinkingResult().observe(viewLifecycleOwner) { result ->
            result.onSuccess { elmManagerLinkingResult ->
                if (elmManagerLinkingResult?.isScanningComplete == true) {
                    showFoundDevices(elmManagerLinkingResult.foundDevices)
                }
                if (elmManagerLinkingResult?.error != null) {
                    handleError(elmManagerLinkingResult.error)
                }
            }
            result.onFailure {
                handleError(null)
            }
        }
    }

    private fun startScan() {

        if (checkBLE()) {
            showSearchOverlay(true)
            obdViewModel.getElmDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkBLE(): Boolean {

        if (!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showMessage(R.string.obd_ble_error)
            finish()
        }
        val mBluetoothAdapter = obdViewModel.getBluetoothAdapter(requireContext())
        mBluetoothAdapter?.let {
            if (!mBluetoothAdapter.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, obdViewModel.getRequestBluetoothEnableCode())
                return false
            }
        }
        return true
    }

    private fun showSearchOverlay(show: Boolean) {

        binding.obdDeviceConnectSearchLayout.root.isVisible = show
    }

    private fun showFoundDevices(devices: List<ElmDevice>?) {

        showSearchOverlay(false)
        devices?.let {
            commonObdViewModel.setFoundedDevices(devices)
        }

        findNavController().navigate(R.id.action_deviceConnectFragment_to_foundedDevicesFragment)
    }

    private fun handleError(error: String?) {

        when (error) {
            "SERVER_ERROR_NETWORK_CONNECTION_NOT_AVAILABLE",
            "SERVER_ERROR_UNKNOWN" ->
                showMessage(R.string.obd_internet_error)
            "VEHICLE_NOT_SUPPORTED" ->
                findNavController().navigate(R.id.action_deviceConnectFragment_to_vehicleNotSupportedFragment)
            else ->
                findNavController().navigate(R.id.action_deviceConnectFragment_to_couldNotConnectFragment)

        }
    }

    private fun finish() {

        findNavController().navigateUp()
    }
}
