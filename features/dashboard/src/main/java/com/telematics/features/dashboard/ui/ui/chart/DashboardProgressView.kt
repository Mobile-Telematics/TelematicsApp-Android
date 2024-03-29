package com.telematics.features.dashboard.ui.ui.chart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.telematics.dashboard.R
import com.telematics.dashboard.databinding.NewDashboardItemBinding

class DashboardProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = NewDashboardItemBinding.inflate(LayoutInflater.from(context), this)

    fun setProgress(progress: Int) = with(binding) {
        when (progress) {
            -1 -> {
                dashboardZeroText.setText(R.string.dashboard_new_start_driving_to_reveal_your_score_alt)
                dashboardText.visibility = View.INVISIBLE
                dashboardZeroText.visibility = View.VISIBLE
            }

            0 -> {
                dashboardZeroText.setText(R.string.dashboard_new_start_driving_to_reveal_your_score)
                dashboardText.visibility = View.INVISIBLE
                dashboardZeroText.visibility = View.VISIBLE
            }

            else -> {
                dashboardText.visibility = View.VISIBLE
                dashboardZeroText.visibility = View.INVISIBLE
            }
        }

        if (progress <= 0) return
        dashboardProgress.progress = progress

        when (progress) {
            in 0..62 -> {
                dashboardIcon.setImageResource(R.drawable.ic_bad_score)
            }

            in 63..80 -> {
                dashboardIcon.setImageResource(R.drawable.ic_not_bad_score)
            }

            in 81..89 -> {
                dashboardIcon.setImageResource(R.drawable.ic_good_score)
            }

            else -> {
                dashboardIcon.setImageResource(R.drawable.ic_great_score)
            }
        }

        dashboardText.text = progress.toString()
    }

    fun setType(type: String = "Total score") = with(binding) {
        dashboardType.text = type
    }
}