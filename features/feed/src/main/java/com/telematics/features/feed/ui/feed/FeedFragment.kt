package com.telematics.features.feed.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telematics.content.utils.TryOpenLink
import com.telematics.domain.model.tracking.TripData
import com.telematics.features.feed.model.EndlessRecyclerViewScrollListener
import com.telematics.feed.databinding.FragmentFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val TAG = "FeedFragment"

    lateinit var binding: FragmentFeedBinding

    @Inject
    lateinit var feedViewModel: FeedViewModel

    lateinit var feedListAdapter: FeedListAdapter
    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        observeTripList()
    }

    private fun initViews() {

        val recyclerView = binding.feedList
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        feedListAdapter = FeedListAdapter(feedViewModel.getDateFormatter)
        recyclerView.adapter = feedListAdapter

        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getNextPage(totalItemsCount)
            }

            override fun onScroll() {
                //fix issue at bottom of recyclerView list
                val scrollPos = layoutManager.findFirstVisibleItemPosition()
                val isEnable = scrollPos == 0
                if (isEnable != binding.swipeToRefreshEvents.isEnabled)
                    binding.swipeToRefreshEvents.isEnabled = isEnable
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        setListeners()
    }

    private fun setListeners() {

        binding.swipeToRefreshEvents.setOnRefreshListener {
            observeTripList()
        }

        binding.eventsEmptyListLayout.feedEmptyListPermissions.setOnClickListener {
            tryOpenLink()
        }


    }

    private fun observeTripList() {

        showRefresh(true)
        scrollListener.resetState()
        getNextPage(0)
    }

    private fun getNextPage(offset: Int) {

        val isFirstPage = offset == 0

        feedViewModel.getTripList(offset).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                updateList(it, isFirstPage)
            }
            result.onFailure {

            }

            showRefresh(false)
        }
    }

    private fun updateList(list: List<TripData>, isFirstData: Boolean) {

        if (isFirstData) {
            val isEmptyTripList = list.isEmpty()
            showEmptyData(isEmptyTripList)
            feedListAdapter.clearAllData()
        }

        feedListAdapter.addData(list)
    }

    private fun showRefresh(refresh: Boolean) {
        binding.swipeToRefreshEvents.isRefreshing = refresh
    }

    private fun showEmptyData(show: Boolean) {

        binding.eventsEmptyList.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun tryOpenLink() {

        val link = feedViewModel.getTelematicsLink(requireContext())
        TryOpenLink(requireContext()).open(link)
    }
}