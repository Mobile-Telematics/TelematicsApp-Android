package com.telematics.zenroad.ui.bottom_menu

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.telematics.features.account.ui.AccountFragment
import com.telematics.features.dashboard.ui.ui.DashboardFragment

class ViewPagerFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        DashboardFragment(),
        AccountFragment()
    )

    override fun createFragment(position: Int): Fragment {
        return fragments[position].apply {
            arguments = bundleOf(
                "position" to position
            )
        }
    }

    override fun getItemCount(): Int = fragments.size
}