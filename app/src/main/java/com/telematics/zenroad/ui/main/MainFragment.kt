package com.telematics.zenroad.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    private fun initBottomMenu() {

        val viewPager = binding.mainViewPager
        val bottomNavigationView = binding.mainBottomNav

        viewPager.apply {
            isUserInputEnabled = false
            //offscreenPageLimit = 4
        }
        viewPager.adapter = ViewPagerFragmentStateAdapter(requireActivity())

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    viewPager.setCurrentItem(0, false)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    viewPager.setCurrentItem(1, false)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    viewPager.setCurrentItem(0, false)
                    return@setOnNavigationItemSelectedListener true
                }
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