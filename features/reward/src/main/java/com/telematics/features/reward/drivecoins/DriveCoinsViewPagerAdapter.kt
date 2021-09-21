package com.telematics.features.reward.drivecoins

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.features.reward.drivecoins.tab_eco_driving.EcoDrivingFragment
import com.telematics.features.reward.drivecoins.tab_safe_driving.SafeDriveFragment
import com.telematics.features.reward.drivecoins.tab_traveling.TravellingFragment

class DriveCoinsViewPagerAdapter(
    fm: Fragment,
    private var data: DriveCoinsDetailedData?,
    private val measuresFormatter: MeasuresFormatter
) :
    FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fr = when (position) {
            1 -> SafeDriveFragment()
            2 -> EcoDrivingFragment()
            else -> TravellingFragment()
        }
        val inMiles = when (measuresFormatter.getDistanceMeasureValue()) {
            DistanceMeasure.KM -> false
            DistanceMeasure.MI -> true
        }
        fr.arguments = bundleOf(
            DRIVE_COINS_DETAILED_KEY to data,
            DRIVE_COINS_IN_MILES_KEY to inMiles
        )

        return fr
    }

    companion object {
        const val DRIVE_COINS_DETAILED_KEY = "DRIVE_COINS_DETAILED_KEY"
        const val DRIVE_COINS_IN_MILES_KEY = "DRIVE_COINS_IN_MILES_KEY"
    }

    fun updateData(data: DriveCoinsDetailedData) {
        this.data = data
        notifyDataSetChanged()
    }
}