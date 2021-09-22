package com.telematics.features.reward.drivecoins.tab_traveling

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_DETAILED_KEY
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_IN_MILES_KEY
import com.telematics.reward.R
import kotlin.math.roundToInt

class TravellingFragment : Fragment(R.layout.fragment_travelling) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getSerializable(DRIVE_COINS_DETAILED_KEY) as DriveCoinsDetailedData?
            ?: DriveCoinsDetailedData()
        val inMiles = arguments?.getBoolean(DRIVE_COINS_IN_MILES_KEY) ?: false

        view.alpha = 0f
        view.animate().setDuration(300).alpha(1f).start()

        view.findViewById<TextView>(R.id.distance_score).apply {
            text = data.travelingMileage.toString()
            setTextColor(getTextColor(data.travelingMileage))
        }
        view.findViewById<TextView>(R.id.duration_score).apply {
            text = data.travelingTimeDriven.toString()
            setTextColor(getTextColor(data.travelingTimeDriven))
        }

        view.findViewById<TextView>(R.id.accelerations_score).apply {
            text = data.travelingAccelerations.toString()
            setTextColor(getTextColor(data.travelingAccelerations))
        }
        view.findViewById<TextView>(R.id.brakings_score).apply {
            text = data.travelingBrakings.toString()
            setTextColor(getTextColor(data.travelingBrakings))
        }
        view.findViewById<TextView>(R.id.cornerings_score).apply {
            text = data.travelingCornerings.toString()
            setTextColor(getTextColor(data.travelingCornerings))
        }
        view.findViewById<TextView>(R.id.phone_usage_score).apply {
            text = data.travelingPhoneUsage.toString()
            setTextColor(getTextColor(data.travelingPhoneUsage))
        }
        view.findViewById<TextView>(R.id.speeding_score).apply {
            text = data.travelingSpeeding.toString()
            setTextColor(getTextColor(data.travelingSpeeding))
        }

        view.findViewById<TextView>(R.id.accelerations_sum).apply {
            text = data.travelingAccelerationCount.toString()
        }
        view.findViewById<TextView>(R.id.brakings_sum).apply {
            text = data.travelingBrakingCount.toString()
        }
        view.findViewById<TextView>(R.id.cornerings_sum).apply {
            text = data.travelingCorneringCount.toString()
        }
        view.findViewById<TextView>(R.id.phone_usage_sum).apply {
            text = outHoursOrMinutes(data.travelingDrivingTime)
        }
        view.findViewById<TextView>(R.id.speeding_sum).apply {
            text = getDistanceValue(data.travelingTotalSpeedingKm, inMiles)
        }

        view.findViewById<TextView>(R.id.distance_sum).text =
            getDistanceValue(data.travelingMileageData, inMiles)
        view.findViewById<TextView>(R.id.duration_sum).text =
            outHoursOrMinutes(data.travelingTimeDrivenData)

    }

    private fun getTextColor(p: Int): Int {
        val rc = when {
            p == 0 -> R.color.colorPrimaryText
            p > 0 -> R.color.colorGreenText
            else -> R.color.colorRedText
        }
        return ContextCompat.getColor(requireContext(), rc)
    }

    private fun outHoursOrMinutes(p: Int): String {

        return if (p >= 60) {
            (p.toFloat() / 60f).roundToInt().toString() + " h"
        } else "$p m"
    }

    private fun getDistanceValue(p: Int, inMiles: Boolean): String {

        val distValue = if (inMiles) R.string.dashboard_new_mi else R.string.dashboard_new_km
        val kmStr = getString(distValue)
        return "$p $kmStr"
    }
}