package ui.step6_success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.telematics.content.utils.BaseFragment
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentOdbCongratulationsBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ObdSuccessFragment : BaseFragment() {

    @Inject
    lateinit var obdViewModel: ObdViewModel
    private lateinit var commonObdViewModel: CommonObdViewModel

    private lateinit var binding: FragmentOdbCongratulationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOdbCongratulationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)

        val vehicleToken = commonObdViewModel.getConnectedDevice()?.first ?: ""
        val elmMac = commonObdViewModel.getConnectedDevice()?.second ?: ""

        binding.deviceNumberText.text = getString(R.string.odb_device_number, vehicleToken)
        binding.policyNumberText.text = getString(R.string.odb_policy_number, elmMac)
        binding.finishButton.setOnClickListener {
            onBackPressed()
        }
    }
}
