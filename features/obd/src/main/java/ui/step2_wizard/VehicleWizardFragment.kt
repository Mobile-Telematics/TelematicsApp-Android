package ui.step2_wizard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.content.utils.BaseFragment
import com.telematics.data.utils.PermissionUtils
import com.telematics.data.utils.PhotoUtils
import com.telematics.obd.R
import com.telematics.obd.databinding.FragmentObdVehicleWizardBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel
import ui.step2_wizard.crop.CropFragment
import javax.inject.Inject

@AndroidEntryPoint
class VehicleWizardFragment : BaseFragment() {

    companion object {
        const val CROP_KEY = "crop_key"
        const val CROP_FILE_PATH_KEY = "crop_file_path_key"
    }

    @Inject
    lateinit var obdViewModel: ObdViewModel
    private lateinit var commonObdViewModel: CommonObdViewModel

    private val permissionUtils = PermissionUtils()
    private val odometerPictureName = "odometerPicture.png"

    private lateinit var binding: FragmentObdVehicleWizardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdVehicleWizardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        commonObdViewModel =
            ViewModelProvider(requireActivity()).get(CommonObdViewModel::class.java)

        binding.obdWizardViewPager.adapter = ObdWizardPagerAdapter().apply {
            setOnClick(object : ObdWizardPagerAdapter.OnClick {
                override fun onClickNext() {
                    askPermissions()
                }
            })
        }
        binding.obdPagesIndicator.setViewPager(binding.obdWizardViewPager)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permissionUtils.registerContract(this)
    }

    private fun askPermissions() {

        permissionUtils.setPermissionListener { allIsGranted ->
            if (allIsGranted)
                showPickupDialog()
            else showPermissionError()
        }

        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        permissionUtils.askPermissions(requireActivity(), permissions)
    }

    private fun showPickupDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.app_name)
            .setMessage(R.string.settings_view_set_image_dialog_msg)
            .setPositiveButton(R.string.settings_view_take_a_picture) { _, _ -> showCamera() }
            .setNegativeButton(R.string.settings_view_from_gallery) { _, _ -> showGallery() }
            .setOnCancelListener { d ->
                d.dismiss()
            }
            .show()
    }

    private fun showCamera() {

        PhotoUtils.setCallback(object : PhotoUtils.Callback {
            override fun openCropScreen(fileFrom: String?, fileTo: String?) {
                openCrop(fileFrom, fileTo)
            }
        })
        PhotoUtils.openCamera(this, odometerPictureName)
    }

    private fun showGallery() {

        PhotoUtils.setCallback(object : PhotoUtils.Callback {
            override fun openCropScreen(fileFrom: String?, fileTo: String?) {
                openCrop(fileFrom, fileTo)
            }
        })
        PhotoUtils.openGallery(this, odometerPictureName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        PhotoUtils.onActivityResult(this, requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openCrop(fileFrom: String?, fileTo: String?) {

        val bundle = bundleOf(
            CropFragment.CROP_INPUT_FILE_KEY to fileFrom,
            CropFragment.CROP_RESULT_FILE_KEY to fileTo
        )
        findNavController().navigate(R.id.action_vehicleWizardFragment_to_cropPhotoFragment, bundle)
    }

    private fun showPermissionError() {

        val snackBar = Snackbar.make(
            binding.root,
            R.string.account_access_permission_msg,
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction(R.string.retry) {
            askPermissions()
        }
        snackBar.show()
    }
}
