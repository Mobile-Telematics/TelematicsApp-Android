package com.telematics.features.account.ui.account

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.telematics.content.utils.BaseFragment
import com.telematics.data.utils.PermissionUtils
import com.telematics.data.utils.PhotoUtils
import com.telematics.domain.model.authentication.User
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentAccountBinding
import com.telematics.features.account.ui.account.vehicle.VehicleFragment
import com.telematics.features.account.ui.crop.CropFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AccountFragment : BaseFragment() {

    private val TAG = "AccountFragment"

    companion object {

        private var onNavToSettings: (() -> Unit)? = null

        fun setOnNavigationToSettings(action: () -> Unit) {
            onNavToSettings = action
        }

        const val ACCOUNT_USER_KEY = "account_user_key"
        const val ACCOUNT_USER_BUNDLE_KEY = "account_user_bundle_key"

        const val CROP_KEY = "crop_key"
        const val CROP_FILE_PATH_KEY = "crop_file_path_key"
    }

    @Inject
    lateinit var accountViewModel: AccountViewModel

    private val permissionUtils = PermissionUtils()

    private lateinit var binding: FragmentAccountBinding

    private val profilePictureName = "profilePicture.png"

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permissionUtils.registerContract(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setListeners()
        observeUser()
        observeVehicles()
    }

    private fun setListeners() {

        binding.userInfoCard.accountDocumentItem.setOnClickListener {
            openProfileFragment()
        }

        binding.accountAvatar.setOnClickListener {
            askPermissions()
        }

        binding.accountSettings.setOnClickListener {
            onNavToSettings?.invoke()
        }

        //listener for update picture
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            CROP_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            val filePath = result.getString(CROP_FILE_PATH_KEY)
            filePath?.let {
                uploadProfilePic(filePath)
            } ?: run {
                showFilePathError()
            }
        }
    }

    private fun observeUser() {

        accountViewModel.getUser().observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                bindUser(user)
            }
            result.onFailure {
                bindUser(User())
            }
        }
    }

    private fun bindUser(user: User) {

        val userName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}"
        binding.accountUserName.text = userName

        if (user.phone.isNullOrBlank()) {
            binding.accountLoginField.text = user.email
            binding.accountLoginType.setText(R.string.account_email)
        } else {
            binding.accountLoginField.text = user.phone
            binding.accountLoginType.setText(R.string.account_phone)
        }

        if (user.isCompleted()) {
            binding.accountInfoArea.visibility = View.VISIBLE
            binding.accountCompleteBtn.visibility = View.GONE
        } else {
            binding.accountInfoArea.visibility = View.GONE
            binding.accountCompleteBtn.visibility = View.VISIBLE
        }

        val birthdayStr =
            if (user.birthday.isNullOrEmpty()) resources.getString(R.string.account_not_specified) else user.birthday
        binding.userInfoCard.birthDay.text = birthdayStr

        val addressStr =
            if (user.address.isNullOrEmpty()) resources.getString(R.string.account_not_specified) else user.address
        binding.userInfoCard.address.text = addressStr

        observeProfilePicture()
    }

    private fun uploadProfilePic(filePath: String?) {

        Log.d(TAG, "updateProfilePic: file path $filePath")
        accountViewModel.uploadProfilePicture(filePath).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d(TAG, "updateProfilePic: success")
                observeProfilePicture()
            }
            result.onFailure {
                Log.d(TAG, "updateProfilePic: error ${it.printStackTrace()}")
            }
        }
    }

    private fun observeProfilePicture() {

        accountViewModel.getProfilePicture().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d(TAG, "bindUser: profile picture onSuccess")
                binding.accountAvatar.setImageBitmap(it)
            }
            result.onFailure {
                Log.d(TAG, "bindUser: profile picture ${it.printStackTrace()}")
            }
        }
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
        PhotoUtils.openCamera(this, profilePictureName)
    }

    private fun showGallery() {

        PhotoUtils.setCallback(object : PhotoUtils.Callback {
            override fun openCropScreen(fileFrom: String?, fileTo: String?) {
                openCrop(fileFrom, fileTo)
            }
        })
        PhotoUtils.openGallery(this, profilePictureName)
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
        findNavController().navigate(R.id.action_accountFragment_to_cropFragment, bundle)
    }

    private fun openProfileFragment() {
        findNavController().navigate(R.id.action_accountFragment_to_profileFragment)
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

    private fun showFilePathError() {
        showMessage(getString(R.string.something_went_wrong))
    }


    /*vehicles*/
    private fun observeVehicles() {

        accountViewModel.getVehicles().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                bindVehicleList(it)
            }
            result.onFailure {
                showEmptyVehicle(true)
            }
        }

        /*listeners*/
        binding.accountVehicles.accountVehicleAddParent.setOnClickListener {
            openAddVehicleFragment()
        }
        binding.accountVehicles.accountNewCarDocumentAddIcon.setOnClickListener {
            openAddVehicleFragment()
        }
    }

    private fun bindVehicleList(list: List<Vehicle>) {

        if (list.isEmpty()) {
            showEmptyVehicle(true)
            return
        }

        showEmptyVehicle(false)

        val adapter = VehicleListAdapter(list)
        adapter.setOnClickListener(object : VehicleListAdapter.ClickListeners {
            override fun onItemClick(vehicle: Vehicle, listItemPosition: Int) {
                openVehicleFragment(vehicle)
            }
        })
        binding.accountVehicles.recyclerViewAccountVehicleList.layoutManager =
            LinearLayoutManager(requireContext())
        binding.accountVehicles.recyclerViewAccountVehicleList.adapter = adapter
    }

    private fun showEmptyVehicle(show: Boolean) {

        if (show) {
            binding.accountVehicles.accountNewCarDocumentAddIcon.isVisible = false
            binding.accountVehicles.recyclerViewAccountVehicleList.isVisible = false
            binding.accountVehicles.accountVehicleAddParent.isVisible = true
            binding.accountVehicles.accountVehicleAddParent.alpha = 0f
            binding.accountVehicles.accountVehicleAddParent.animate().setDuration(200).alpha(1f)
                .start()
        } else {
            binding.accountVehicles.accountVehicleAddParent.isVisible = false
            binding.accountVehicles.recyclerViewAccountVehicleList.isVisible = true
            binding.accountVehicles.accountNewCarDocumentAddIcon.isVisible = true
            binding.accountVehicles.recyclerViewAccountVehicleList.alpha = 0f
            binding.accountVehicles
                .recyclerViewAccountVehicleList.animate().setDuration(300).alpha(1f)
            binding.accountVehicles.accountNewCarDocumentAddIcon.alpha = 0f
            binding
                .accountVehicles.accountNewCarDocumentAddIcon.animate().setDuration(200).alpha(1f)
        }
    }

    private fun openAddVehicleFragment() {

        openVehicleFragment(null)
    }

    private fun openVehicleFragment(vehicle: Vehicle?) {

        val bundle = bundleOf(
            VehicleFragment.VEHICLE_FRAGMENT_VEHICLE_KEY to vehicle
        )
        findNavController().navigate(R.id.action_accountFragment_to_vehicleFragment, bundle)
    }
}