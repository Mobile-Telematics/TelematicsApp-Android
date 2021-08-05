package com.telematics.features.leaderboard.ui.leaderboard_summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telematics.leaderboard.R
import com.telematics.leaderboard.databinding.FragmentLeaderboardSummaryBinding

class LeaderboardSummaryFragment : Fragment() {

    lateinit var binding: FragmentLeaderboardSummaryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leaderboardGo.setOnClickListener {
            openLeaderboardDetails()
        }
    }

    private fun openLeaderboardDetails() {

        val bundle = bundleOf(
            "" to ""
        )
        findNavController().navigate(
            R.id.action_leaderboardSummaryFragment_to_leaderboardDetailsFragment,
            bundle
        )
    }
}