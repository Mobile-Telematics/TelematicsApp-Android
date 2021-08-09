package com.telematics.features.leaderboard.ui.leaderboard_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.features.leaderboard.ui.BaseFragment
import com.telematics.leaderboard.databinding.FragmentLeaderboardDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardDetailsFragment : BaseFragment() {

    companion object {
        const val LEADERBOARD_DETAILS_TYPE_KEY = "LEADERBOARD_DETAILS_TYPE_KEY"
    }

    lateinit var leaderboardDetailsViewModel: LeaderboardDetailsViewModel

    private var detailsType = LeaderboardType.Acceleration

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

        val typeIndex = arguments?.getInt(LEADERBOARD_DETAILS_TYPE_KEY, 1) ?: 1
        detailsType = LeaderboardType.getFromIndex(typeIndex)

        initViews()
    }

    private fun initViews() {

        binding.leaderboardText.text = detailsType.toString()

        setListeners()
    }

    private fun setListeners() {

    }
}