package ui.step_error_not_suported

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.telematics.content.utils.BaseFragment
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentObdCarNotSupportedBinding

class VehicleNotSupportedFragment : BaseFragment() {

    private lateinit var binding: FragmentObdCarNotSupportedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdCarNotSupportedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    nextStep()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.phoneNumberText.setOnClickListener {
            callToSupport()
        }
        binding.quitButton.setOnClickListener {
            nextStep()
        }
    }

    private fun callToSupport() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${getString(R.string.odb_1_300_824_227)}")
        startActivity(intent)
    }

    private fun nextStep() {

        findNavController().popBackStack(R.id.vehiclesFragment, false)
    }
}
