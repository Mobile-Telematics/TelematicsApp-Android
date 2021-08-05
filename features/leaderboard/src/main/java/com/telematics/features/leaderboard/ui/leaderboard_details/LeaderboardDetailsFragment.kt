package com.telematics.features.leaderboard.ui.leaderboard_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.telematics.features.leaderboard.ui.BaseFragment
import com.telematics.leaderboard.databinding.FragmentLeaderboardDetailsBinding

class LeaderboardDetailsFragment : BaseFragment() {

    lateinit var binding: FragmentLeaderboardDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()


    }
}