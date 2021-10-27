package com.telematics.features.dashboard.ui.ui.ecoscoring

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.telematics.dashboard.R
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.statistics.StatisticEcoScoringTabsData

class DashboardEcoScoringTabAdapter(
    fm: FragmentManager,
    val data: StatisticEcoScoringTabsData?,
    context: Context,
    private val measuresFormatter: MeasuresFormatter
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = context.resources.getStringArray(R.array.dashboard_ecoscoring_period)

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment = DashboardEcoScoringTabFragment()
        val args = Bundle()
        var averageSpeed = .0
        var maxSpeed = .0
        var averageTripDistance = .0
        val ecoScoringTabData = when (position) {
            0 -> data?.week
            1 -> data?.month
            2 -> data?.year
            else -> null
        }
        if (ecoScoringTabData != null) {
            averageSpeed = measuresFormatter.getDistanceByKm(ecoScoringTabData.averageSpeed)
            maxSpeed = measuresFormatter.getDistanceByKm(ecoScoringTabData.maxSpeed)
            averageTripDistance = measuresFormatter.getDistanceByKm(ecoScoringTabData.averageTripDistance)
        }
        args.putDouble(DashboardEcoScoringTabFragment.AVERAGE_SPEED_KEY, averageSpeed)
        args.putDouble(DashboardEcoScoringTabFragment.MAX_SPEED_KEY, maxSpeed)
        args.putDouble(
            DashboardEcoScoringTabFragment.AVERAGE_TRIP_DISTANCE_KEY,
            averageTripDistance
        )
        val inMiles = when(measuresFormatter.getDistanceMeasureValue()){
            DistanceMeasure.MI -> true
            DistanceMeasure.KM -> false
        }
        args.putBoolean(DashboardEcoScoringTabFragment.IN_MILES, inMiles)
        fragment.arguments = args
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence {

        return tabTitles[position]
    }
}