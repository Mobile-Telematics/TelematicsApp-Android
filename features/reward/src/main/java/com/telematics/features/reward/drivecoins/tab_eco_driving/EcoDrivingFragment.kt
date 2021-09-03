package com.telematics.features.reward.drivecoins.tab_eco_driving

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.telematics.data.extentions.getColorByScore
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_DETAILED_KEY
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.reward.R

class EcoDrivingFragment : Fragment(R.layout.fragment_eco_drive) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // disabling seek_bar sliding
        view.findViewById<SeekBar>(R.id.eco_seek_bar).setOnTouchListener { _, _ -> true }
        view.findViewById<SeekBar>(R.id.braked_seek_bar).setOnTouchListener { _, _ -> true }
        view.findViewById<SeekBar>(R.id.fuel_seek_bar).setOnTouchListener { _, _ -> true }
        view.findViewById<SeekBar>(R.id.tyres_seek_bar).setOnTouchListener { _, _ -> true }
        view.findViewById<SeekBar>(R.id.cost_seek_bar).setOnTouchListener { _, _ -> true }

        val data = arguments?.getSerializable(DRIVE_COINS_DETAILED_KEY) as DriveCoinsDetailedData?
            ?: DriveCoinsDetailedData()

        view.findViewById<SeekBar>(R.id.eco_seek_bar).apply {
            setProgressForSeekBar(this, data.ecoScore)
        }
        view.findViewById<SeekBar>(R.id.braked_seek_bar).apply {
            setProgressForSeekBar(this, data.ecoScoreBrakes)
        }
        view.findViewById<SeekBar>(R.id.fuel_seek_bar).apply {
            setProgressForSeekBar(this, data.ecoScoreFuel)
        }
        view.findViewById<SeekBar>(R.id.tyres_seek_bar).apply {
            setProgressForSeekBar(this, data.ecoScoreTyres)
        }
        view.findViewById<SeekBar>(R.id.cost_seek_bar).apply {
            setProgressForSeekBar(this, data.ecoScoreCostOfOwnership)
        }

        view.findViewById<TextView>(R.id.eco_score).apply {
            setTextColor(getTextColor(data.ecoDrivingEcoScore))
            text = data.ecoDrivingEcoScore.toString()
        }
        view.findViewById<TextView>(R.id.brake_score).apply {
            setTextColor(getTextColor(data.ecoDrivingBrakes))
            text = data.ecoDrivingBrakes.toString()
        }
        view.findViewById<TextView>(R.id.fuel_score).apply {
            setTextColor(getTextColor(data.ecoDrivingFuel))
            text = data.ecoDrivingFuel.toString()
        }
        view.findViewById<TextView>(R.id.tyres_score).apply {
            setTextColor(getTextColor(data.ecoDrivingTires))
            text = data.ecoDrivingTires.toString()
        }
        view.findViewById<TextView>(R.id.cost_score).apply {
            setTextColor(getTextColor(data.ecoDrivingCostOfOwnership))
            text = data.ecoDrivingCostOfOwnership.toString()
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

    private fun getProgressTextColor(p: Int): ColorStateList? {
        val res = p.getColorByScore()
        return ContextCompat.getColorStateList(requireContext(), res)
    }

    private fun setProgressForSeekBar(seekBar: SeekBar, p: Int) {

        val vl = ValueAnimator.ofInt(0, p)
        vl.duration = 1000
        vl.addUpdateListener {
            val v = it.animatedValue as Int
            seekBar.progress = v
        }
        vl.start()
        seekBar.progressTintList = getProgressTextColor(p)
        seekBar.thumbTintList = ContextCompat.getColorStateList(
            requireContext(),
            android.R.color.transparent
        )//getProgressTextColor(p)
        seekBar.splitTrack = false
    }
}