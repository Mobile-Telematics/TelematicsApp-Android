package com.telematics.features.leaderboard.ui.leaderboard_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.leaderboard.R
import com.telematics.leaderboard.databinding.FragmentLeaderboardDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardDetailsFragment : BaseFragment() {

    companion object {
        const val LEADERBOARD_DETAILS_TYPE_KEY = "LEADERBOARD_DETAILS_TYPE_KEY"
    }

    lateinit var leaderboardDetailsViewModel: LeaderboardDetailsViewModel

    private var detailsType = LeaderboardType.Acceleration
    private lateinit var adapter: LeaderboardDetailsAdapter

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

        val typeIndex = arguments?.getInt(LEADERBOARD_DETAILS_TYPE_KEY, 0) ?: 0
        detailsType = LeaderboardType.getFromIndex(typeIndex)

        initViews()
    }

    private fun initViews() {

        initViewPager()
        setListeners()
    }

    private fun initViewPager() {

        binding.leaderboardViewPager.apply {
            isUserInputEnabled = true
            offscreenPageLimit = 10
        }
        adapter = LeaderboardDetailsAdapter(requireActivity())
        binding.leaderboardViewPager.adapter = adapter

        val firstPos = detailsType.index - 1
        binding.leaderboardViewPager.setCurrentItem(firstPos, false)
        setHeaderIconText(firstPos)
    }

    private fun setListeners() {

        binding.leaderboardDetailBack.setOnClickListener {
            onBackPressed()
        }

        binding.leaderboardBackParent.setOnClickListener { }

        binding.leaderboardViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setHeaderIconText(position)
            }
        })

        binding.nextButton.setOnClickListener {
            nextPage()
        }

        binding.prevButton.setOnClickListener {
            prevPage()
        }
    }

    private fun setHeaderIconText(position: Int) {

        val title = when (position) {
            0 -> getString(R.string.leaderboard_rate)

            1 -> getString(R.string.leaderboard_acceleration)
            2 -> getString(R.string.leaderboard_deceleration)
            3 -> getString(R.string.leaderboard_speeding)
            4 -> getString(R.string.leaderboard_distraction)
            5 -> getString(R.string.leaderboard_turn)

            6 -> getString(R.string.leaderboard_total_trips)
            7 -> getString(R.string.leaderboard_mileage)
            8 -> getString(R.string.leaderboard_time_driven)
            else -> ""
        }

        val icon = when (position) {
            0 -> 0

            1 -> R.drawable.ic_leaderboard_acceleration
            2 -> R.drawable.ic_leaderboard_deceleration
            3 -> R.drawable.ic_leaderboard_speeding
            4 -> R.drawable.ic_leaderboard_phone
            5 -> R.drawable.ic_leaderboard_cornering

            6 -> R.drawable.ic_leaderboard_trips
            7 -> R.drawable.ic_leaderboard_mileage
            8 -> R.drawable.ic_leaderboard_time
            else -> 0
        }

        binding.leaderboardTypeImage.setImageResource(icon)
        binding.leaderboardTypeHeader.text = title
    }

    private fun nextPage() {
        val nextPagePos = binding.leaderboardViewPager.currentItem + 1
        binding.leaderboardViewPager.setCurrentItem(nextPagePos, true)
    }

    private fun prevPage() {
        val prevPagePos = binding.leaderboardViewPager.currentItem - 1
        binding.leaderboardViewPager.setCurrentItem(prevPagePos, true)
    }
}