package com.telematics.zenroad.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.telematics.authentication.extention.observeOnce
import com.telematics.content.utils.BaseFragment
import com.telematics.features.account.AccountFeatureHost
import com.telematics.features.account.ui.account.AccountFragment
import com.telematics.features.dashboard.ui.ui.dashboard.DashboardFragment
import com.telematics.features.feed.FeedFeatureHost
import com.telematics.features.leaderboard.LeaderboardFeatureHost
import com.telematics.features.reward.RewardFeatureHost
import com.telematics.zenroad.MainActivity
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.MainFragmentBinding
import com.telematics.zenroad.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : BaseFragment() {

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
        setGreenNavigationBar()

        val navTo = arguments?.getString(SettingsFragment.NAV_TO_KEY)

        initTrackingApi()
        initToolbar()
        initBottomMenu(navTo)
    }

    private fun initBottomMenu(navTo: String?) {

        val bottomNavigationView = binding.mainBottomNav
        bottomNavigationView.itemTextAppearanceActive = R.style.bottom_selected_text
        bottomNavigationView.itemTextAppearanceInactive = R.style.bottom_normal_text

        var previousItemId = 0

        bottomNavigationView.setOnNavigationItemSelectedListener {

            mainFragmentViewModel.saveCurrentBottomMenuState(it.itemId)

            if (previousItemId == it.itemId) {
                return@setOnNavigationItemSelectedListener true
            }
            previousItemId = it.itemId

            when (it.itemId) {
                R.id.nav_feed -> {
                    navToFeed()
                }
                R.id.nav_leaderboard -> {
                    navToLeaderboard()
                }
                R.id.nav_dashboard -> {
                    navToDashboard()
                }
                R.id.nav_reward -> {
                    navToReward()
                }
                R.id.nav_profile -> {
                    navToAccount()
                }
                else -> {
                    navToDashboard()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        //first state
        navTo?.let {
            when (it) {
                SettingsFragment.NAV_TO_ACCOUNT ->
                    bottomNavigationView.selectedItemId = R.id.nav_profile
            }
        } ?: run {

            observeSavedBottomMenuState {
                if (it == 0) {
                    bottomNavigationView.selectedItemId = R.id.nav_dashboard
                } else {
                    bottomNavigationView.selectedItemId = it
                }
            }
        }

        DashboardFragment.setOnNavigationToFeed {
            bottomNavigationView.selectedItemId = R.id.nav_feed
        }
        DashboardFragment.setOnNavigationToLeaderboard {
            bottomNavigationView.selectedItemId = R.id.nav_leaderboard
        }
        DashboardFragment.setOnNavigationToReward { toStreaks ->
            bottomNavigationView.selectedItemId = R.id.nav_reward
            if (toStreaks)
                navToReward(true)
        }
        AccountFragment.setOnNavigationToSettings {
            openSettings()
        }
    }

    private fun observeSavedBottomMenuState(action: (bottomMenuState: Int) -> Unit) {

        if (mainFragmentViewModel.getSaveStateBundle.value == null) {
            action(0)
            return
        }

        mainFragmentViewModel.getSaveStateBundle.observe(viewLifecycleOwner) { bundle ->
            val bottomMenuState = mainFragmentViewModel.bundleToListSize(bundle)
            action(bottomMenuState)
            mainFragmentViewModel.getSaveStateBundle.removeObservers(viewLifecycleOwner)
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
        binding.mainToolbar.findViewById<View>(R.id.toolbar_settings).setOnClickListener {
            openSettings()
        }
    }

    private fun observeUser() {

        mainFragmentViewModel.getUser().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.mainToolbar.findViewById<TextView>(com.telematics.dashboard.R.id.toolbar_user_name)
                    .apply {
                        val name = "${it.firstName.orEmpty()} ${it.lastName.orEmpty()}"
                        text = name
                    }
            }
        }

        mainFragmentViewModel.getProfilePicture()
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

    private fun navToFeed() {

        showToolbar()
        openFragment(FeedFeatureHost())
    }

    private fun navToLeaderboard() {

        showToolbar()
        openFragment(LeaderboardFeatureHost())
    }

    private fun navToDashboard() {

        showToolbar()
        observeUser()
        openFragment(DashboardFragment())
    }

    private fun navToAccount() {

        hideToolbar()
        openFragment(AccountFeatureHost())
    }

    private fun navToReward(toStreaks: Boolean = false) {

        showToolbar()
        observeUser()
        openFragment(RewardFeatureHost.createFragment(toStreaks))
    }

    private fun openFragment(fragment: Fragment) {

        val container = R.id.main_fragment_container
        val manager: FragmentManager = childFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(container, fragment)
        transaction.commit()
    }

    private fun openSettings() {

        findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
    }

    private fun showToolbar() {
        binding.mainToolbar.isVisible = true
    }

    private fun hideToolbar() {
        binding.mainToolbar.isVisible = false
    }
}