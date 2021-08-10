package com.telematics.features.leaderboard.ui.leaderboard_details.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.leaderboard.databinding.FragmentLeaderboardDetailsPageBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LeaderboardDetailsPageFragment : Fragment() {

    companion object {
        const val LEADERBOARD_DETAILS_PAGE_TYPE = "LEADERBOARD_DETAILS_PAGE_TYPE"
    }

    @Inject
    lateinit var leaderboardDetailsPageViewModel: LeaderboardDetailsPageViewModel

    private lateinit var adapter: LeaderboardDetailsPageAdapter

    private lateinit var binding: FragmentLeaderboardDetailsPageBinding

    private var leaderboardType = LeaderboardType.Rate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardDetailsPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leaderboardType =
            arguments?.getSerializable(LEADERBOARD_DETAILS_PAGE_TYPE) as LeaderboardType

        initViews()
        observeLeaderboardUserList(leaderboardType)
    }

    private fun initViews() {

        initLeaderboardUserList()
        setListeners()
    }

    private fun setListeners() {

        binding.leaderboardSwipeToRefresh.setOnRefreshListener {
            observeLeaderboardUserList(leaderboardType)
        }
    }

    private fun initLeaderboardUserList() {

        val recyclerView = binding.leaderboardRecyclerView
        adapter = LeaderboardDetailsPageAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
    }

    private fun observeLeaderboardUserList(type: LeaderboardType) {

        val mappedType = when (type) {
            LeaderboardType.Rate -> 6
            LeaderboardType.Acceleration -> 1
            LeaderboardType.Deceleration -> 2
            LeaderboardType.Speeding -> 4
            LeaderboardType.Distraction -> 3
            LeaderboardType.Turn -> 5
            LeaderboardType.Trips -> 8
            LeaderboardType.Distance -> 7
            LeaderboardType.Duration -> 9
        }

        showRefresh(true)
        leaderboardDetailsPageViewModel.getUserListByType(mappedType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess { data ->
                    adapter.setData(data)
                }
                result.onFailure {

                }
                showRefresh(false)
            }
    }

    private fun showRefresh(show: Boolean) {

        binding.leaderboardSwipeToRefresh.isRefreshing = show
    }
}