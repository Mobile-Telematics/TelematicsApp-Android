package com.telematics.features.feed.ui.trip_detail

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.telematics.data.utils.PermissionUtils
import com.telematics.domain.model.tracking.TripData
import com.telematics.features.feed.model.BaseFragment
import com.telematics.feed.databinding.FragmentTripDatailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TripDetailFragment : BaseFragment() {

    lateinit var binding: FragmentTripDatailBinding

    private val permissionUtils = PermissionUtils()


    @Inject
    lateinit var tripDetailViewModel: TripDetailViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permissionUtils.registerContract(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTripDatailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()

        val tripData = arguments?.getSerializable("trip") as TripData?

        initViews()
        setListeners()
    }

    private fun setListeners() {

    }

    private fun initViews() {

        askPermissions()
    }

    private fun askPermissions() {

        permissionUtils.setPermissionListener { allIsGranted ->

        }

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        permissionUtils.askPermissions(requireActivity(), permissions)
    }
}