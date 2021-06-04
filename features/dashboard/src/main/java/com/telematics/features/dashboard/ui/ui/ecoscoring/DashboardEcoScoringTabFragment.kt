package com.telematics.features.dashboard.ui.ui.ecoscoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.telematics.dashboard.R

class DashboardEcoScoringTabFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.layoit_tab_eco_scoring, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val args: Bundle? = arguments
        val inMiles = args?.getBoolean(IN_MILES) ?: false
        val stringResKmH = if (!inMiles) R.string.template_km_h else R.string.template_mi_h
        val stringResKm = if (!inMiles) R.string.template_km else R.string.template_mi

        val averageSpeed = mapToString(stringResKmH, args?.getDouble(AVERAGE_SPEED_KEY))
        val maxSpeed = mapToString(stringResKmH, args?.getDouble(MAX_SPEED_KEY))
        val averageTripDistance = mapToString(stringResKm, args?.getDouble(AVERAGE_TRIP_DISTANCE_KEY))

        (view.findViewById(R.id.tab_eco_scoring_text1) as TextView).text = averageSpeed
        (view.findViewById(R.id.tab_eco_scoring_text2) as TextView).text = maxSpeed
        (view.findViewById(R.id.tab_eco_scoring_text3) as TextView).text = averageTripDistance
    }

    private fun mapToString(strRes: Int, p: Double?): String =
            p?.let {
                if (it < 0) "?" else getString(strRes, String.format("%.0f", it))
            } ?: run {
                String.format("%.0f", .0)
            }

    companion object {
        const val AVERAGE_SPEED_KEY = "AVERAGE_SPEED_KEY"
        const val MAX_SPEED_KEY = "MAX_SPEED_KEY"
        const val AVERAGE_TRIP_DISTANCE_KEY = "AVERAGE_TRIP_DISTANCE_KEY"
        const val IN_MILES = "IN_MILES"
    }
}