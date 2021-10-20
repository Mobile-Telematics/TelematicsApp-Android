package com.telematics.features.leaderboard.ui.leaderboard_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.features.leaderboard.ui.leaderboard_details.page.LeaderboardDetailsPageFragment

class LeaderboardDetailsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 9
    }

    override fun createFragment(position: Int): Fragment {

        val leaderboardType = LeaderboardType.getFromIndex(position + 1)

        return LeaderboardDetailsPageFragment().apply {
            arguments = Bundle().apply {
                putSerializable(
                    LeaderboardDetailsPageFragment.LEADERBOARD_DETAILS_PAGE_TYPE,
                    leaderboardType
                )
            }
        }
    }
}