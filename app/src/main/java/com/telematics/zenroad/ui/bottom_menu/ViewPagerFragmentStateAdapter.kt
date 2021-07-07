package com.telematics.zenroad.ui.bottom_menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.telematics.features.account.ui.AccountFeatureHost
import com.telematics.features.dashboard.ui.ui.dashboard.DashboardFragment
import com.telematics.features.feed.ui.FeedFeatureHost

class ViewPagerFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        FeedFeatureHost(),
        DashboardFragment(),
        AccountFeatureHost()
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int = fragments.size
}