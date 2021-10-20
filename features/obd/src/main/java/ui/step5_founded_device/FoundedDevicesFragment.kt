package ui.step5_founded_device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.tracking.ElmDevice
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentOdbDevicesBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FoundedDevicesFragment : BaseFragment() {

    @Inject
    lateinit var obdViewModel: ObdViewModel
    private lateinit var commonObdViewModel: CommonObdViewModel

    private lateinit var adapter: FoundedDevicesAdapter

    private lateinit var binding: FragmentOdbDevicesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOdbDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)

        binding.nextButton.setOnClickListener {
            if (adapter.itemCount != 0) {
                val device = adapter.getSelected()
                connectSelectedDevice(device)
            }
        }
        binding.bottomText.setOnClickListener {
            onBackPressed()
        }

        registerEmlManagerLinkingResult()
        initRV()
    }

    private fun initRV() {

        adapter = FoundedDevicesAdapter()

        binding.devicesRV.layoutManager = LinearLayoutManager(requireContext())
        binding.devicesRV.adapter = adapter
        adapter.setData(commonObdViewModel.getFoundedDevices() ?: listOf())
    }

    private fun registerEmlManagerLinkingResult() {

        obdViewModel.registerElmManagerLinkingResult().observe(viewLifecycleOwner) { result ->
            showLoading(false)
            result.onSuccess { elmManagerLinkingResult ->
                if (elmManagerLinkingResult?.isLinkingComplete == true) {
                    nextStep(
                        elmManagerLinkingResult.vehicleToken,
                        elmManagerLinkingResult.elmMAC
                    )
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

    private fun connectSelectedDevice(device: ElmDevice) {

        showLoading(true)

        val token = commonObdViewModel.getVehicle()?.token
        obdViewModel.connectSelectedDevice(device, token ?: "")
    }

    private fun handleError(error: String?) {

        when (error) {
            "SERVER_ERROR_NETWORK_CONNECTION_NOT_AVAILABLE",
            "SERVER_ERROR_UNKNOWN" ->
                showMessage(R.string.obd_internet_error)
            "VEHICLE_NOT_SUPPORTED" ->
                findNavController().navigate(R.id.action_foundedDevicesFragment_to_vehicleNotSupportedFragment)
            else ->
                findNavController().navigate(R.id.action_foundedDevicesFragment_to_couldNotConnectFragment)
        }
    }

    private fun showLoading(show: Boolean) {

        binding.foundedDeviceProgressBar.isVisible = show
    }

    private fun nextStep(vehicleToken: String?, elmMAC: String?) {

        commonObdViewModel.setConnectedDevice(vehicleToken, elmMAC)
        findNavController().navigate(R.id.action_foundedDevicesFragment_to_obdSuccessFragment)
    }
}
