package ui.step3_photo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.telematics.content.utils.BaseFragment
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentObdConfirmPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmPhotoFragment : BaseFragment() {

    @Inject
    lateinit var obdViewModel: ObdViewModel
    lateinit var commonObdViewModel: CommonObdViewModel

    private lateinit var binding: FragmentObdConfirmPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdConfirmPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.vehicleWizardFragment, false)
                }
            })

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)

        binding.confirmButton.setOnClickListener {
            uploadPhoto()
        }

        observeImage()
    }

    private fun uploadPhoto() {

        loading(true)
        obdViewModel.uploadOdometerPhoto("").observe(viewLifecycleOwner) { result ->
            loading(false)
            result.onSuccess {
                nextStep()
            }
            result.onFailure {
                showMessage("Error occurred")
            }

        }
    }

    private fun observeImage() {

        val imagePath = commonObdViewModel.getImagePath()
        binding.odometerImage.setImageURI(Uri.parse(imagePath))
    }

    private fun loading(show: Boolean = true) {
        if (show) {
            binding.confirmPhotoProgressBar.visibility = View.VISIBLE
            binding.confirmButton.isEnabled = false
        } else {
            binding.confirmPhotoProgressBar.visibility = View.INVISIBLE
            binding.confirmButton.isEnabled = true
        }
    }

    private fun nextStep() {

        findNavController().navigate(R.id.action_confirmPhotoFragment_to_deviceConnectFragment)
    }
}
