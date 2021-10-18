package com.telematics.features.feed.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telematics.content.utils.BaseFragment
import com.telematics.content.utils.TryOpenLink
import com.telematics.domain.model.tracking.TripData
import com.telematics.features.feed.model.ChangeDriverTypeDialog
import com.telematics.features.feed.model.EndlessRecyclerViewScrollListener
import com.telematics.features.feed.ui.trip_detail.TripDetailFragment
import com.telematics.feed.R
import com.telematics.feed.databinding.FragmentFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FeedFragment : BaseFragment() {

    private val TAG = "FeedFragment"

    @Inject
    lateinit var feedViewModel: FeedViewModel

    private lateinit var binding: FragmentFeedBinding
    lateinit var feedListAdapter: FeedListAdapter
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var changeDriverTypeDialog: ChangeDriverTypeDialog

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
        observeSavedTripList()
    }

    /** init UIs */
    private fun initViews() {

        initRecyclerView()
        initChangeDriverTypeDialog()
        setListeners()
    }

    private fun initRecyclerView() {

        val recyclerView = binding.feedList
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        feedListAdapter = FeedListAdapter(feedViewModel.getMeasuresFormatter)
        feedListAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        recyclerView.adapter = feedListAdapter

        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                getListNextPage(totalItemsCount)
            }

            override fun onScroll() {
                val scrollPos = layoutManager.findFirstVisibleItemPosition()
                val isEnable = scrollPos == 0
                if (isEnable != binding.swipeToRefreshEvents.isEnabled)
                    binding.swipeToRefreshEvents.isEnabled = isEnable
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun initChangeDriverTypeDialog() {

        changeDriverTypeDialog = ChangeDriverTypeDialog(requireContext())
    }

    private fun setListeners() {

        binding.swipeToRefreshEvents.setOnRefreshListener {
            observeTripList(false)
        }

        binding.eventsEmptyListLayout.feedEmptyListPermissions.setOnClickListener {
            tryOpenLink()
        }

        feedListAdapter.setOnClickListener(object : FeedListAdapter.ClickListeners {
            override fun onItemClick(tripData: TripData, listItemPosition: Int) {
                openTripDetail(tripData, listItemPosition)
            }

            override fun onItemChangeTypeClick(tripData: TripData, listItemPosition: Int) {
                val tripType = tripData.type
                changeDriverTypeDialog.showDialog(tripType) { type ->
                    changeTripTypeTo(tripData, type, listItemPosition)
                }
            }

            override fun onItemHide(tripData: TripData, listItemPosition: Int) {
                hideTrip(tripData, listItemPosition)
            }

            override fun onItemDelete(tripData: TripData, listItemPosition: Int) {
                setDeleteStatus(tripData, listItemPosition)
            }
        })
    }

    /** observe/handle list */
    private fun observeSavedTripList() {

        if (feedViewModel.getSaveStateBundle.value == null) {
            observeTripList(true)
            return
        }
        feedViewModel.getSaveStateBundle.observe(viewLifecycleOwner) { bundle ->
            val savedListSize = feedViewModel.bundleToListSize(bundle)
            if (savedListSize == 0) {
                observeTripList(true)
            } else {
                restoreList(savedListSize)
            }

            feedViewModel.getSaveStateBundle.removeObservers(viewLifecycleOwner)
        }
    }

    private fun observeTripList(isCreateEvent: Boolean) {

        if (isCreateEvent)
            showProgress(true)
        else
            showRefresh(true)
        scrollListener.resetState()
        getListNextPage(0)
    }

    private fun getListNextPage(offset: Int) {

        val isFirstPage = offset == 0

        feedViewModel.getTripList(offset).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                updateList(it, isFirstPage)
            }
            result.onFailure {

            }

            showRefresh(false)
            showProgress(false)
        }
    }

    private fun restoreList(count: Int) {

        showProgress(true)

        feedViewModel.getTripList(0, count).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                updateList(it, true)
            }
            result.onFailure {

            }

            showRefresh(false)
            showProgress(false)
        }
    }

    private fun updateList(list: List<TripData>, isFirstData: Boolean) {

        if (isFirstData) {
            val isEmptyTripList = list.isEmpty()
            showEmptyData(isEmptyTripList)
            feedListAdapter.clearAllData()
        }

        feedListAdapter.addData(list)
        feedViewModel.saveCurrentListSize(feedListAdapter.itemCount)
    }

    private fun setDeleteStatus(tripData: TripData, listItemPosition: Int) {

        showAnswerDialog(
            R.string.dialog_events_delete_trip,
            onPositive = {
                feedViewModel.setDeleteStatus(tripData).observe(viewLifecycleOwner) { result ->
                    result.onSuccess {
                        feedListAdapter.removeItem(listItemPosition)
                    }
                    result.onFailure {
                        showMessage(R.string.server_error_error)
                    }
                }
            }
        )
    }

    private fun hideTrip(tripData: TripData, listItemPosition: Int) {

        showAnswerDialog(
            R.string.dialog_events_hide_trip,
            onPositive = {
                feedViewModel.hideTrip(tripData).observe(viewLifecycleOwner) { result ->
                    result.onSuccess {
                        feedListAdapter.removeItem(listItemPosition)
                    }
                    result.onFailure {
                        showMessage(R.string.server_error_error)
                    }
                }
            }
        )
    }

    /** show dialog for change trip type */
    private fun changeTripTypeTo(
        tripData: TripData,
        toType: TripData.TripType,
        listItemPosition: Int
    ) {

        val tripId = tripData.id!!

        showProgress(true)
        feedViewModel.changeTripTypeTo(tripId, toType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    if (it) {
                        tripData.type = toType
                    }
                    feedListAdapter.updateItemByPos(toType, listItemPosition)
                    //bindTripType(tripData)
                }
                result.onFailure {
                    feedListAdapter.updateItemByPos(toType, listItemPosition)
                    //bindTripType(tripData)
                }
                showProgress(false)
            }
    }


    /** additional UI */
    private fun showRefresh(refresh: Boolean) {
        binding.swipeToRefreshEvents.isRefreshing = refresh
    }

    private fun showProgress(show: Boolean) {
        binding.feedLoadingView.isVisible = show
        binding.feedLoadingView.setOnClickListener { }
    }

    private fun showEmptyData(show: Boolean) {

        binding.eventsEmptyList.alpha = if (show) 1f else 0f
    }

    private fun tryOpenLink() {

        val link = feedViewModel.getPermissionLink(requireContext())
        TryOpenLink(requireContext()).open(link)
    }

    /** go to tripDetails*/
    private fun openTripDetail(tripData: TripData, listItemPosition: Int) {

        val bundle = bundleOf(
            TripDetailFragment.TRIP_DETAILS_TRIP_DATA_KEY to tripData,
            TripDetailFragment.TRIP_DETAILS_TRIP_POS_KEY to listItemPosition
        )
        findNavController().navigate(R.id.action_feedFragment_to_tripDetailFragment, bundle)
    }
}