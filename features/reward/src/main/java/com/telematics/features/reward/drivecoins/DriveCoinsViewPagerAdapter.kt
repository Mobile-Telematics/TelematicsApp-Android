package com.telematics.features.reward.drivecoins

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.features.reward.drivecoins.tab_eco_driving.EcoDrivingFragment
import com.telematics.features.reward.drivecoins.tab_safe_driving.SafeDriveFragment
import com.telematics.features.reward.drivecoins.tab_traveling.TravellingFragment

class DriveCoinsViewPagerAdapter(fm: FragmentManager, private var data: DriveCoinsDetailedData?) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val DRIVE_COINS_DETAILED_KEY = "DRIVE_COINS_DETAILED_KEY"
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        val fr = when (position) {
            1 -> SafeDriveFragment()
            2 -> EcoDrivingFragment()
            else -> TravellingFragment()
        }
        fr.arguments = bundleOf(DRIVE_COINS_DETAILED_KEY to data)
        return fr
    }

    fun updateData(data: DriveCoinsDetailedData) {
        this.data = data
        notifyDataSetChanged()
    }
}