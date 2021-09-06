package com.telematics.features.reward.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.telematics.content.utils.BaseFragment
import com.telematics.features.reward.RewardFeatureHost
import com.telematics.features.reward.drivecoins.DrivecoinsFragment
import com.telematics.features.reward.streaks.StreaksFragment
import com.telematics.reward.R
import com.telematics.reward.databinding.FragmentRewardBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RewardFragment : BaseFragment() {

    @Inject
    lateinit var rewardViewModel: RewardViewModel

    private var lastOpenFragment = 0

    private lateinit var binding: FragmentRewardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {

        var toStreaks: Boolean
        findNavController().graph.arguments.apply {
            toStreaks = this[RewardFeatureHost.NAV_TO_STREAKS]?.defaultValue as Boolean? ?: false
        }

        initTabs()
        initInviteScreen()

        openDriveCoinsFragment()

        if (toStreaks)
            binding.headerTabs.getTabAt(1)?.select()
    }

    private fun initInviteScreen() {
        if (rewardViewModel.isNeedShowRewardsInvite) {
            binding.rewardsInvite.root.isVisible = true
            binding.rewardsInvite.rewardsInviteStartBtn.setOnClickListener { closeInviteScreen() }
        }
    }

    private fun closeInviteScreen() {
        binding.rewardsInvite.root.isVisible = false
        rewardViewModel.inviteScreenClosed()
    }


    private fun initTabs() {

        val tabLayout = binding.headerTabs
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> openDriveCoinsFragment()
                    1 -> openStreaksFragment()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun openDriveCoinsFragment() {
        lastOpenFragment = 0
        openFragment(DrivecoinsFragment())
    }

    private fun openStreaksFragment() {
        lastOpenFragment = 1
        openFragment(StreaksFragment())
    }

    private fun openFragment(fragment: Fragment) {

        val container = R.id.more_drive_coins_container
        val manager: FragmentManager = parentFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(container, fragment)
        transaction.commit()
    }
}