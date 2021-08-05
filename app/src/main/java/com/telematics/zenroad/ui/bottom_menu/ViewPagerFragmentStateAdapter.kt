package com.telematics.zenroad.ui.bottom_menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.telematics.features.account.AccountFeatureHost
import com.telematics.features.dashboard.ui.ui.dashboard.DashboardFragment
import com.telematics.features.feed.FeedFeatureHost
import com.telematics.features.leaderboard.LeaderboardFeatureHost

class ViewPagerFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        FeedFeatureHost(),
        LeaderboardFeatureHost(),
        DashboardFragment(),
        AccountFeatureHost()
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int = fragments.size
}