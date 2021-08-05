package com.telematics.features.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.telematics.leaderboard.databinding.FragmentLeaderboardFeatureHostBinding

class LeaderboardFeatureHost : Fragment() {

    lateinit var binding: FragmentLeaderboardFeatureHostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardFeatureHostBinding.inflate(inflater, container, false)
        return binding.root
    }
}