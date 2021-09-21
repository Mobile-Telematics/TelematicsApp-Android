package com.telematics.features.reward.drivecoins.tab_safe_driving

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_DETAILED_KEY
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.reward.R

class SafeDriveFragment : Fragment(R.layout.fragment_safe_drive) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getSerializable(DRIVE_COINS_DETAILED_KEY) as DriveCoinsDetailedData?
                ?: DriveCoinsDetailedData()

        view.findViewById<TextView>(R.id.safe_total_coins).apply {
            text = data.safeDrivingCoinsTotal.toString()
            setTextColor(getTextColor(data.safeDrivingCoinsTotal))
        }
    }

    private fun getTextColor(p: Int): Int {
        val rc = when {
            p == 0 -> R.color.colorPrimaryText
            p > 0 -> R.color.colorGreenText
            else -> R.color.colorRedText
        }
        return ContextCompat.getColor(requireContext(), rc)
    }
}