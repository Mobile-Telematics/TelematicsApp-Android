package com.telematics.features.leaderboard.ui.leaderboard_summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.features.leaderboard.ui.leaderboard_details.LeaderboardDetailsFragment
import com.telematics.leaderboard.R
import com.telematics.leaderboard.databinding.FragmentLeaderboardSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LeaderboardSummaryFragment : BaseFragment() {

    @Inject
    lateinit var leaderboardViewModel: LeaderboardSummaryViewModel

    private lateinit var adapter: LeaderboardSummaryAdapter

    private lateinit var binding: FragmentLeaderboardSummaryBinding

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

        initViews()
        observeLeaderboardList()
    }

    private fun initViews() {

        initRecyclerView()
        setListeners()
    }

    private fun initRecyclerView() {

        adapter = LeaderboardSummaryAdapter(object : LeaderboardSummaryAdapter.ClickListener {
            override fun onClick(type: LeaderboardType) {
                openLeaderboardDetails(type)
            }
        })

        val recyclerView = binding.leaderboardList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
    }

    private fun setListeners() {

        binding.leaderboardSwipeToRefresh.setOnRefreshListener {
            observeLeaderboardList()
        }
    }

    private fun observeLeaderboardList() {

        showRefresh(true)
        leaderboardViewModel.observeLeaderboardList().observe(viewLifecycleOwner) { result ->
            result.onSuccess { leaderboardUser ->
                adapter.submitList(leaderboardUser)
                showNoDataView(false)
            }
            result.onFailure {
                showNoDataView(true)
            }

            showRefresh(false)
        }
    }

    private fun showNoDataView(show: Boolean) {

        binding.leaderboarNoData.isVisible = show
        binding.leaderboardList.isVisible = !show
    }

    private fun showRefresh(show: Boolean) {

        binding.leaderboardSwipeToRefresh.isRefreshing = show
    }

    private fun openLeaderboardDetails(type: LeaderboardType) {

        val bundle = bundleOf(
            LeaderboardDetailsFragment.LEADERBOARD_DETAILS_TYPE_KEY to type.index
        )
        findNavController().navigate(
            R.id.action_leaderboardSummaryFragment_to_leaderboardDetailsFragment,
            bundle
        )
    }
}