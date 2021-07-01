package com.telematics.zenroad.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.telematics.authentication.extention.observeOnce
import com.telematics.zenroad.MainActivity
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.MainFragmentBinding
import com.telematics.zenroad.ui.bottom_menu.ViewPagerFragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var mainFragmentViewModel: MainFragmentViewModel

    lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBottomMenu()
        initTrackingApi()
        initToolbar()
    }

    private fun initBottomMenu() {

        val viewPager = binding.mainViewPager
        val bottomNavigationView = binding.mainBottomNav

        viewPager.apply {
            isUserInputEnabled = false
            //offscreenPageLimit = 4
        }
        viewPager.adapter = ViewPagerFragmentStateAdapter(requireActivity())

        fun navToDashboard() {
            showToolbar()
            observeUser()
            viewPager.setCurrentItem(0, false)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    navToDashboard()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    hideToolbar()
                    viewPager.setCurrentItem(1, false)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    navToDashboard()
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun initToolbar() {

        observeUser()

        binding.mainToolbar.findViewById<View>(R.id.toolbar_user_name).setOnClickListener {
            binding.mainBottomNav.selectedItemId = R.id.nav_profile
        }
        binding.mainToolbar.findViewById<View>(R.id.toolbar_avatar).setOnClickListener {
            binding.mainBottomNav.selectedItemId = R.id.nav_profile
        }
    }

    private fun showToolbar() {
        binding.mainToolbar.visibility = View.VISIBLE
    }

    private fun hideToolbar() {
        binding.mainToolbar.visibility = View.GONE
    }

    private fun observeUser() {

        mainFragmentViewModel.getUser().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.mainToolbar.findViewById<TextView>(com.telematics.dashboard.R.id.toolbar_user_name)
                    .apply {
                        val name = "${it.firstName} ${it.lastName}"
                        text = name
                    }
            }
        }

        mainFragmentViewModel.getProfilePicture(requireContext())
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    binding.mainToolbar.findViewById<ImageView>(com.telematics.dashboard.R.id.toolbar_avatar)
                        .apply {
                            setImageBitmap(it)
                        }
                }
                result.onFailure {
                    Log.d("MainFragment", "observeUser: ${it.printStackTrace()}")
                }
            }
    }

    private fun initTrackingApi() {

        mainFragmentViewModel.checkPermissions().observeOnce(viewLifecycleOwner) { result ->
            result.onSuccess { allPermissionsGranted ->
                if (!allPermissionsGranted) {
                    startWizard()
                } else {
                    mainFragmentViewModel.enableTracking()
                }
            }
            result.onFailure {
                startWizard()
            }
        }

        mainFragmentViewModel.setDeviceTokenForTrackingApi()
        setIntentForNotification()
    }

    private fun startWizard() {

        mainFragmentViewModel.startWizard(requireActivity())
    }

    private fun setIntentForNotification() {

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        mainFragmentViewModel.setIntentForNotification(intent)
    }
}