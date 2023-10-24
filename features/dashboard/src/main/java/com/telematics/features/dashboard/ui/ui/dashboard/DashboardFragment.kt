package com.telematics.features.dashboard.ui.ui.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.telematics.dashboard.R
import com.telematics.dashboard.databinding.FragmentDashboardBinding
import com.telematics.data.BuildConfig
import com.telematics.data.extentions.format
import com.telematics.data.extentions.getColorByScore
import com.telematics.data.extentions.setProgressWithColor
import com.telematics.data.extentions.toMiles
import com.telematics.data.utils.Resource
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.on_demand.DashboardOnDemandJob
import com.telematics.domain.model.on_demand.OnDemandJobState
import com.telematics.domain.model.on_demand.OnDemandState
import com.telematics.domain.model.on_demand.TrackingState
import com.telematics.domain.model.statistics.DriveCoins
import com.telematics.domain.model.statistics.ScoreType
import com.telematics.domain.model.statistics.ScoreTypeModel
import com.telematics.domain.model.statistics.StatisticEcoScoringMain
import com.telematics.domain.model.statistics.StatisticEcoScoringTabData
import com.telematics.domain.model.statistics.StatisticEcoScoringTabsData
import com.telematics.domain.model.statistics.StatisticScoringData
import com.telematics.domain.model.statistics.UserStatisticsIndividualData
import com.telematics.features.dashboard.ui.ui.chart.DashboardTypePagerAdapter
import com.telematics.features.dashboard.ui.ui.ecoscoring.DashboardEcoScoringTabAdapter
import com.telematics.sdkhelper.hidePersistentSdkNotifications
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.schedule
import kotlin.math.roundToInt

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    companion object {
        private const val TAG = "DashboardFragment"
        private const val DISTANCE_LIMIT = BuildConfig.DASHBOARD_DISTANCE_LIMIT

        private var onNavToFeed: (() -> Unit)? = null
        private var onRankClickListener: (() -> Unit)? = null
        private var onRewardClickListner: ((toStreaks: Boolean) -> Unit)? = null

        fun setOnNavigationToFeed(action: () -> Unit) {
            onNavToFeed = action
        }

        fun setOnNavigationToLeaderboard(action: () -> Unit) {
            onRankClickListener = action
        }

        fun setOnNavigationToReward(action: (toStreaks: Boolean) -> Unit) {
            onRewardClickListner = action
        }
    }


    @Inject
    lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var binding: FragmentDashboardBinding

    private val scoringAdapter: DashboardTypePagerAdapter by lazy {
        DashboardTypePagerAdapter(
            mutableListOf()
        )
    }
    private var prevPageSelected: Int = -1
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val item = scoringAdapter.getItem(position)
            if (prevPageSelected != -1) initializeChart(item.data, true)
            prevPageSelected = position
        }
    }
    private val xAxisValues = mutableListOf<String>()


    override fun onStart() {
        super.onStart()
        binding.drivingScoresPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onStop() {
        super.onStop()
        binding.drivingScoresPager.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getInt("position")

        binding.drivingScoresPager.offscreenPageLimit = 6
        binding.drivingScoresPager.adapter = scoringAdapter
        binding.progressIndicator.setViewPager(binding.drivingScoresPager)

        /*binding.dashboardEmptyLastTrip.dashboardEmptyLastTripPermissions.setOnClickListener {
            openLinkTelematicsLink()
        }*/
        scoringAdapter.registerAdapterDataObserver(binding.progressIndicator.adapterDataObserver)

        binding.include4.layoutItemEcoScoringFuel.itemEcoScoringImage.setImageResource(R.drawable.ic_dash_fuel)
        binding.include4.layoutItemEcoScoringBrakes.itemEcoScoringImage.setImageResource(R.drawable.ic_dash_brakes)
        binding.include4.layoutItemEcoScoringTires.itemEcoScoringImage.setImageResource(R.drawable.ic_dash_tyres)
        binding.include4.layoutItemEcoScoringCost.itemEcoScoringImage.setImageResource(R.drawable.ic_dash_depreciation)

        binding.include4.layoutItemEcoScoringFuel.itemEcoScoringText.setText(R.string.dashboard_eco_fuel)
        binding.include4.layoutItemEcoScoringBrakes.itemEcoScoringText.setText(R.string.dashboard_eco_brakes)
        binding.include4.layoutItemEcoScoringTires.itemEcoScoringText.setText(R.string.dashboard_eco_tyres)
        binding.include4.layoutItemEcoScoringCost.itemEcoScoringText.setText(R.string.dashboard_eco_cost)

        setListener()
        init()
        hidePersistentSdkNotifications(requireContext())
    }

    private fun setListener() {

        binding.leftArrow.setOnClickListener {
            val position =
                if (binding.drivingScoresPager.currentItem == 0) scoringAdapter.itemCount - 1 else binding.drivingScoresPager.currentItem - 1
            binding.drivingScoresPager.setCurrentItem(position, true)
        }
        binding.rightArrow.setOnClickListener {
            val position =
                if (binding.drivingScoresPager.currentItem == scoringAdapter.itemCount - 1) 0 else binding.drivingScoresPager.currentItem + 1
            binding.drivingScoresPager.setCurrentItem(position, true)
        }

        /*binding.dashboardEmptyLastTrip.dashboardEmptyLastTripLayout.setOnClickListener {
            navToFeed()
        }*/

        /*binding.include3.lastTripParent.setOnClickListener {
            navToFeed()
        }*/

        binding.include.rankValue.setOnClickListener {
            navToLeaderboard()
        }

        binding.include.dashboardDriveCoins.setOnClickListener {
            navToReward(false)
        }

        binding.dashboardDriveCoinsLayout.dashDriveCoinsLearnMore.setOnClickListener {
            navToReward(true)
        }
    }

    private fun init() {

        observeDriveCoins()
        observeRank()
        observeUserIndividualStatistics()
        observeDrivingStreaks()
        initOnDemandTracking()
    }

    private fun observeDriveCoins() {

        fun showDriveCoins(coins: Int) {
            binding.include.coinsValue.text = coins.toString()
        }

        dashboardViewModel.driveCoinsData.removeObservers(this)
        dashboardViewModel.driveCoinsData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    showDriveCoins(0)
                }

                is Resource.Loading -> {
                    showDriveCoins(0)
                }

                is Resource.Success<DriveCoins> -> {
                    showDriveCoins(it.data?.totalCoins ?: 0)
                }
            }
        }
        dashboardViewModel.getDriveCoins()
    }

    private fun observeRank() {

        Log.d(TAG, "observeRank: start")

        fun setRank(rank: String) = with(binding) {
            Log.d(TAG, "setRank: rank $rank")

            val spannable = SpannableString(rank)
            spannable.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGreenText
                    )
                ),
                rank.indexOf("#"), rank.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            include.rankValue.text = spannable
        }

        dashboardViewModel.getRank().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d(TAG, "observeRank: onSuccess r: $it")
                val s = resources.getString(R.string.dashboard_new_rank) + " #${it}"
                setRank(s)
            }
            result.onFailure {
                Log.d(TAG, "observeRank: onFailure e: ${it.printStackTrace()}")
                val s = resources.getString(R.string.dashboard_new_rank) + " #—"
                setRank(s)
            }
        }
    }

    private fun observeUserIndividualStatistics() {

        fun showUserIndividualStatistics(data: UserStatisticsIndividualData?) {
            if ((data?.mileageKm ?: .0) >= DISTANCE_LIMIT) {
                binding.dashboardEmpty.visibility = View.GONE
                observeDrivingDetails()
                observeLastTrip()
                observeEcoScoring()
            } else {
                showEmptyDashboard(data)
            }
        }

        dashboardViewModel.userIndividualStatisticsData.removeObservers(this)
        dashboardViewModel.userIndividualStatisticsData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    showEmptyDashboard(null)
                }

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    showUserIndividualStatistics(it.data)
                }
            }
        }
        dashboardViewModel.getUserIndividualStatistics()
    }

    private fun observeDrivingStreaks() {

        dashboardViewModel.getDrivingStreaks().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val speeding = (it.StreakOverSpeedCurrentStreak).toString() + " trips"
                binding.dashboardDriveCoinsLayout.dashDriveCoinsSpeedingTrips.text = speeding
                val phoneUsage = (it.StreakPhoneUsageCurrentStreak).toString() + " trips"
                binding.dashboardDriveCoinsLayout.dashDriveCoinsPhoneUsageTrips.text =
                    phoneUsage
            }
        }

        //hide streaks
        binding.dashboardDriveCoinsLayout.root.isVisible = false
    }

    private fun showEmptyDashboard(inData: UserStatisticsIndividualData?) {

        binding.progressBar2.visibility = View.GONE

        val data = inData ?: UserStatisticsIndividualData()

        //top data
        binding.progressValue.max = DISTANCE_LIMIT
        binding.progressValue.progress = data.mileageKm.roundToInt()

        //scoring
        val chartList = mutableListOf(
            Pair(0, 58),
            Pair(1, 60),
            Pair(2, 65),
            Pair(3, 55),
            Pair(4, 30),
            Pair(5, 35),
            Pair(6, 30),
            Pair(7, 40),
            Pair(8, 50),
            Pair(9, 52),
            Pair(10, 50),
            Pair(11, 52),
            Pair(12, 55),
            Pair(13, 50),
            Pair(14, 50)
        )
        initializeChart(chartList, true)
        val list = listOf(
            ScoreTypeModel(ScoreType.OVERALL, -1, chartList),
            ScoreTypeModel(ScoreType.ACCELERATION, -1, chartList),
            ScoreTypeModel(ScoreType.BREAKING, -1, chartList),
            ScoreTypeModel(ScoreType.PHONE_USAGE, -1, chartList),
            ScoreTypeModel(ScoreType.SPEEDING, -1, chartList),
            ScoreTypeModel(ScoreType.CORNERING, -1, chartList)
        )
        val emptyList = listOf(
            ScoreTypeModel(ScoreType.OVERALL, -1),
            ScoreTypeModel(ScoreType.ACCELERATION, -1),
            ScoreTypeModel(ScoreType.BREAKING, -1),
            ScoreTypeModel(ScoreType.PHONE_USAGE, -1),
            ScoreTypeModel(ScoreType.SPEEDING, -1),
            ScoreTypeModel(ScoreType.CORNERING, -1)
        )
        fillData(list, emptyList)

        //three panels
        fillTripData(UserStatisticsIndividualData())

        //last trip
        //binding.dashboardEmptyLastTrip.root.visibility = View.VISIBLE
        /*binding.dashboardEmptyLastTrip.dashboardEmptyLastTripPermissions.setOnClickListener {
            //todo open permissions
        }*/
        /* binding.dashboardEmptyLastTrip.root.setOnClickListener {
             navToFeed()
         }*/

        //eco_scoring
        binding.include4.layoutItemEcoScoringFuel.itemEcoScoringProgress.apply {
            setProgressWithColor(90)
        }
        binding.include4.layoutItemEcoScoringTires.itemEcoScoringProgress.apply {
            setProgressWithColor(80)
        }
        binding.include4.layoutItemEcoScoringBrakes.itemEcoScoringProgress.apply {
            setProgressWithColor(60)
        }
        binding.include4.layoutItemEcoScoringCost.itemEcoScoringProgress.apply {
            setProgressWithColor(39)
        }
        binding.include4.ecoScoringMainScore.text = "?"
        binding.include4.ecoScoringMainScore.backgroundTintList =
            ColorStateList.valueOf(Color.LTGRAY)

        val distanceString = dashboardViewModel.measuresFormatter.getDistanceMeasureValue().let {
            return@let when (it) {
                DistanceMeasure.KM -> getString(R.string.dashboard_new_km)
                DistanceMeasure.MI -> getString(R.string.dashboard_new_mi)
            }
        }

        val distanceValue =
            dashboardViewModel.measuresFormatter.getDistanceByKm(data.mileageKm).roundToInt()
        val distanceLimitValue =
            dashboardViewModel.measuresFormatter.getDistanceByKm(DISTANCE_LIMIT).roundToInt()
        val distanceText = "$distanceValue $distanceString / $distanceLimitValue $distanceString"
        binding.dashboardDistanceValue.text = distanceText


        val outputData = StatisticEcoScoringTabsData(
            StatisticEcoScoringTabData(-1.0, -1.0, -1.0),
            StatisticEcoScoringTabData(-1.0, -1.0, -1.0),
            StatisticEcoScoringTabData(-1.0, -1.0, -1.0)
        )
        //todo outputData toMiles
        binding.include4.pager.adapter = DashboardEcoScoringTabAdapter(
            this.childFragmentManager,
            outputData,
            this.requireContext(),
            dashboardViewModel.measuresFormatter
        )
        binding.include4.tabLayout.setupWithViewPager(binding.include4.pager)

        //show all
        binding.dashboardEmpty.visibility = View.VISIBLE
    }

    private fun fillData(data: List<ScoreTypeModel>, scoreData: List<ScoreTypeModel>) {
        scoringAdapter.updateData(data, scoreData)
        val index = try {
            binding.drivingScoresPager.currentItem
        } catch (e: Exception) {
            0
        }
        if (index in 0..5) {
            initializeChart(scoringAdapter.getItem(index).data, false)
        }
    }

    private fun initializeChart(values: List<Pair<Int, Int>>, needAnimate: Boolean = false) {

        val chart = binding.chart

        chart.clear()
        val entries = mutableListOf<Entry>()
        xAxisValues.clear()

        values.withIndex().forEach {
            entries.add(Entry((it.index).toFloat(), it.value.second.toFloat()))
            xAxisValues.add(it.value.first.toString())
        }

        val dataSet = generateDataSet(entries)

        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)
        lineData.isHighlightEnabled = false

        chart.data = lineData
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        chart.axisLeft.setLabelCount(5, true)
        chart.axisLeft.granularity = 25f
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.axisMaximum = 100f
        chart.axisLeft.yOffset = -5f

        chart.setVisibleYRangeMinimum(110f, YAxis.AxisDependency.LEFT)

        chart.axisRight.setDrawGridLines(false)
        chart.axisRight.isEnabled = false

        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.labelCount = xAxisValues.size
        chart.xAxis.granularity = 1f
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        if (needAnimate) chart.animateXY(200, 200)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.invalidate()
    }

    private fun generateDataSet(entries: List<Entry>): LineDataSet {
        val dataSet = LineDataSet(entries, "")
        dataSet.color = Color.rgb(84, 199, 81)
        dataSet.setDrawCircles(false)
        dataSet.fillColor = Color.rgb(209, 231, 202)
        dataSet.setDrawFilled(true)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.lineWidth = 3f
        return dataSet
    }

    private fun fillTripData(individualData: UserStatisticsIndividualData) {

        val stringRes = dashboardViewModel.measuresFormatter.getDistanceMeasureValue().let {
            return@let when (it) {
                DistanceMeasure.KM -> R.string.dashboard_new_km
                DistanceMeasure.MI -> R.string.dashboard_new_mi
            }
        }
        binding.include2.milage.bottomText.text = getString(stringRes)
        binding.include2.milage.middleText.text = getString(R.string.dashboard_new_mileage)
        binding.include2.milage.topText.text =
            dashboardViewModel.measuresFormatter.getDistanceByKm(individualData.mileageKm).format()

        binding.include2.trips.topText.text = (individualData.tripsCount).toString()
        binding.include2.trips.middleText.text = getString(R.string.dashboard_new_total_trips)
        binding.include2.trips.bottomText.text = getString(R.string.dashboard_new_quantity)

        val durationInHours = (individualData.drivingTime) / 60 // minutes
        binding.include2.time.topText.text = durationInHours.toString()
        binding.include2.time.middleText.text = getString(R.string.dashboard_new_time_driven)
        binding.include2.time.bottomText.text = getString(R.string.dashboard_new_hours)
    }

    private fun observeDrivingDetails() {

        fun showContent(dashboardData: StatisticScoringData) {
            fillData(
                dashboardData.drivingDetailsData,
                dashboardData.userStatisticsScoreData
            )
            fillTripData(dashboardData.userStatisticsIndividualData)
            binding.progressBar2.visibility = View.GONE
        }

        dashboardViewModel.scoreLiveData.removeObservers(this)
        dashboardViewModel.scoreLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    showEmptyDashboard(null)
                }

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    val dashboardData = it.data ?: StatisticScoringData()
                    showContent(dashboardData)
                }

            }
        }

        dashboardViewModel.getStatistics()
    }

    private fun observeLastTrip() {

        dashboardViewModel.getLastTrip().observe(viewLifecycleOwner) { result ->
            result.onSuccess { tripData ->

                if (tripData == null) return@onSuccess
                //binding.dashboardEmptyLastTrip.dashboardEmptyLastTripParent.isVisible = false
                //binding.include3.lastTripParent.isVisible = true


                binding.include3.eventTripDateStart.text =
                    dashboardViewModel.getFormatterDate(tripData.timeStart!!)
                binding.include3.eventTripDateFinish.text =
                    dashboardViewModel.getFormatterDate(tripData.timeEnd!!)
                binding.include3.eventTripOverallScore.text =
                    tripData.rating.roundToInt().toString()
                val overallScoreColor = when (tripData.rating.roundToInt()) {
                    in 0..40 -> ContextCompat.getColor(requireContext(), R.color.colorRedText)
                    in 41..60 -> ContextCompat.getColor(requireContext(), R.color.colorOrangeText)
                    in 61..80 -> ContextCompat.getColor(requireContext(), R.color.colorYellowText)
                    in 80..100 -> ContextCompat.getColor(requireContext(), R.color.colorGreenText)
                    else -> ContextCompat.getColor(requireContext(), R.color.colorGreenText)
                }
                binding.include3.eventTripOverallScore.setTextColor(
                    overallScoreColor
                )

                binding.include3.eventTripMileage.text =
                    dashboardViewModel.measuresFormatter.getDistanceByKm(tripData.dist).format()
                val stringRes = dashboardViewModel.measuresFormatter.getDistanceMeasureValue().let {
                    return@let when (it) {
                        DistanceMeasure.KM -> R.string.dashboard_new_km
                        DistanceMeasure.MI -> R.string.dashboard_new_mi
                    }
                }
                binding.include3.mileageMeasureText.text = getString(stringRes)

                getLastTripImage(tripData.id)
            }
        }
    }

    private fun getLastTripImage(token: String?) {

        token ?: return

        dashboardViewModel.getLastTripImage(token).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                //binding.include3.lastTripImage.setImageBitmap(it)
            }
            result.onFailure {

            }
        }
    }

    private fun observeEcoScoring() {

        observeMainEcoScoring()
        initEcoScoringTable()
    }

    private fun observeMainEcoScoring() {

        fun showMainEcoScoring(data: StatisticEcoScoringMain) {
            val ecoScoring = binding.include4
            ecoScoring.layoutItemEcoScoringFuel.itemEcoScoringProgress.setProgressWithColor(data.fuel)
            ecoScoring.layoutItemEcoScoringTires.itemEcoScoringProgress.setProgressWithColor(data.tires)
            ecoScoring.layoutItemEcoScoringBrakes.itemEcoScoringProgress.setProgressWithColor(data.brakes)
            ecoScoring.layoutItemEcoScoringCost.itemEcoScoringProgress.setProgressWithColor(data.cost)

            val color = data.score.getColorByScore()
            binding.include4.ecoScoringMainScore.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), color))
            binding.include4.ecoScoringMainScore.text = data.score.toString()

            //todo add rotation for arrow
            /*            if (oldScore != null && score < oldScore.oldScore) {
                            scoScoringArrow.rotation = 180f
                            scoScoringArrow.imageTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    this.requireContext(),
                                    R.color.colorSpeedTypeHigh
                                )
                            )
                        }
                        if (score != oldScore?.oldScore) {
                            if (oldScore == null)
                                viewModel.saveOldEcoScoringArrowState(score, true)
                            else {
                                val arrowStateUp = score > oldScore.oldScore
                                viewModel.saveOldEcoScoringArrowState(score, arrowStateUp)
                            }
                        } else {
                            scoScoringArrow.rotation = if (oldScore.lastArrowStateUp) 0f else 180f
                            scoScoringArrow.imageTintList = ColorStateList.valueOf(
                                if (oldScore.lastArrowStateUp) ContextCompat.getColor(
                                    this.requireContext(),
                                    R.color.colorDefButton
                                ) else Color.RED
                            )
                        }*/
        }

        dashboardViewModel.mainEcoScoringLiveData.removeObservers(this)
        dashboardViewModel.mainEcoScoringLiveData.removeObservers(viewLifecycleOwner)
        dashboardViewModel.mainEcoScoringLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                }

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    showMainEcoScoring(it.data ?: StatisticEcoScoringMain())
                }
            }
        }
        dashboardViewModel.getMainEcoScoring()
    }

    private fun initEcoScoringTable() {

        fun showContent(data: StatisticEcoScoringTabsData?) = with(binding) {

            val pagerAdapter = DashboardEcoScoringTabAdapter(
                childFragmentManager,
                data,
                requireContext(),
                dashboardViewModel.measuresFormatter
            )
            include4.pager.adapter = pagerAdapter
            include4.tabLayout.setupWithViewPager(include4.pager)
        }

        dashboardViewModel.tableEcoScoringLiveData.removeObservers(this@DashboardFragment)
        dashboardViewModel.tableEcoScoringLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {

                }

                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    showContent(it.data)
                }
            }
        }

        dashboardViewModel.getEcoScoringTable()
    }

    private fun openLinkTelematicsLink() {

        val link = dashboardViewModel.getTelematicsLink(requireContext())
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                when {
                    URLUtil.isValidUrl(link) -> Uri.parse(link)
                    else -> Uri.parse(link)
                }
            )
        )
    }

    private fun navToFeed() {

        onNavToFeed?.invoke()
    }

    private fun navToLeaderboard() {

        onRankClickListener?.invoke()
    }

    private fun navToReward(toStreaks: Boolean) {

        onRewardClickListner?.invoke(toStreaks)
    }


    /** On-demand tracking*/

    private var updateIncomingDateTimer: Timer? = null
    private fun initOnDemandTracking() {

        //state
        dashboardViewModel.getTrackingState().observe(viewLifecycleOwner) { result ->
            result.onSuccess { currentTrackingMode ->
                binding.onDemand.root.isVisible = currentTrackingMode == TrackingState.DEMAND
            }
        }

        dashboardViewModel.getDemandDutyState().observe(viewLifecycleOwner) { result ->
            result.onSuccess { onDemandState ->
                setDutyState(onDemandState)
            }
        }

        binding.onDemand.onDemandSetState.setOnClickListener {
            showDialogForCompleteCurrentJob {
                dashboardViewModel.setOnDemandCurrentJobToPaused()
                val newState = when (dashboardViewModel.getCurrentDutyState()) {
                    OnDemandState.OFFLINE -> OnDemandState.ONLINE
                    OnDemandState.ONLINE -> OnDemandState.OFFLINE
                    OnDemandState.ON_DUTY -> OnDemandState.OFFLINE
                }
                setDutyState(newState)
            }
        }


        //init date editText
        val setIncomingDate = Handler(Looper.getMainLooper()) {
            try {
                binding.onDemand.onDemandIncomingJobEdit.setText(dashboardViewModel.getDateForDemandMode())
            } catch (e: Exception) {
                updateIncomingDateTimer?.cancel()
                updateIncomingDateTimer = null
            }
            return@Handler true
        }

        setIncomingDate.sendEmptyMessage(0)

        updateIncomingDateTimer = Timer()
        updateIncomingDateTimer?.schedule(1000, 1000) {
            setIncomingDate.sendEmptyMessage(0)
        }

        binding.onDemand.onDemandIncomingJobEdit.setOnFocusChangeListener { _, _ ->
            updateIncomingDateTimer?.cancel()
        }

        //add new onDemand job onClick
        binding.onDemand.onDemandIncomingJobAccept.setOnClickListener {
            val name =
                binding.onDemand.onDemandIncomingJobEdit.text.toString()
            dashboardViewModel
            dashboardViewModel.addAcceptedJobs(name) {
                showAlertForExistOnDemandJobName()
            }
        }

        //add and start new onDemand job onClick
        binding.onDemand.onDemandIncomingJobStart.setOnClickListener {
            val name =
                binding.onDemand.onDemandIncomingJobEdit.text.toString()
            dashboardViewModel.addAndStartOnDemandJob(name, {
                showAlertForExistOnDemandJobName()
            }, { isCurrentJobExist, newJob ->
                activity?.runOnUiThread {
                    if (isCurrentJobExist) {
                        showDialogOnUpdateCurrentJob(newJob, true)
                    } else {
                        setDutyState(OnDemandState.ON_DUTY)
                    }
                }
            })
        }

        //complete job onClick
        binding.onDemand.onDemandComplete.setOnClickListener {
            dashboardViewModel.setOnDemandCurrentJobToCompleted()
        }

        //paused job onClick
        binding.onDemand.onDemandPause.setOnClickListener {
            dashboardViewModel.setOnDemandCurrentJobToPaused()
        }

        //init list
        dashboardViewModel.getOnDemandJobLiveData().removeObservers(viewLifecycleOwner)
        dashboardViewModel.getOnDemandJobLiveData().observe(viewLifecycleOwner) { result ->
            result.onSuccess { jobList ->
                updateViewsOnDemandLayout(jobList)
                visibleDemandPanels(jobList)
                dashboardViewModel.setTrackingTag(jobList)
            }
        }
        dashboardViewModel.getOnDemandJobs()
    }

    private fun destroyOnDemand() {
        updateIncomingDateTimer?.cancel()
        updateIncomingDateTimer = null
    }

    private fun setDutyState(state: OnDemandState) {

        dashboardViewModel.setDemandDutyState(state)
        val enableColor = ContextCompat.getColor(requireContext(), R.color.colorDefButton)
        when (state) {
            OnDemandState.ONLINE -> {
                binding.onDemand.onDemandStatusImg.imageTintList =
                    ColorStateList.valueOf(enableColor)
                binding.onDemand.onDemandState.setText(R.string.on_demand_online)
                binding.onDemand.onDemandSetState.setTextColor(Color.rgb(154, 154, 154))
                binding.onDemand.onDemandSetState.setText(R.string.on_demand_go_offline)
            }

            OnDemandState.ON_DUTY -> {
                binding.onDemand.onDemandStatusImg.imageTintList =
                    ColorStateList.valueOf(enableColor)
                binding.onDemand.onDemandState.setText(R.string.on_demand_on_duty)
                binding.onDemand.onDemandSetState.setTextColor(Color.rgb(154, 154, 154))
                binding.onDemand.onDemandSetState.setText(R.string.on_demand_go_offline)
            }

            OnDemandState.OFFLINE -> {
                binding.onDemand.onDemandStatusImg.imageTintList = null
                binding.onDemand.onDemandState.setText(R.string.on_demand_offline)
                binding.onDemand.onDemandSetState.setTextColor(enableColor)
                binding.onDemand.onDemandSetState.setText(R.string.on_demand_go_online)
            }
        }
    }

    private fun updateViewsOnDemandLayout(list: List<DashboardOnDemandJob>?) {

        val currentJobText = binding.onDemand.onDemandCurrentJobName
        currentJobText.setText(R.string.on_demand_no_current_job)
        currentJobText.alpha = 0.5f
        currentJobText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        val acceptedParentLayout = binding.onDemand.onDemandAcceptedJobsLayout
        acceptedParentLayout.removeAllViews()

        val completedParentLayout = binding.onDemand.onDemandCompletedLayout
        completedParentLayout.removeAllViews()

        list ?: return

        val currentJob = list.firstOrNull { it.state == OnDemandJobState.CURRENT.name }
        currentJob?.let {
            currentJobText.text = currentJob.getName
            currentJobText.alpha = 1f
            currentJobText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        }

        //accept (current | paused) list
        list.filter {
            it.state == OnDemandJobState.ACCEPTED.name || it.state == OnDemandJobState.CURRENT.name || it.state == OnDemandJobState.PAUSED.name
        }.forEach { job ->

            layoutInflater.inflate(
                R.layout.on_demand_accepted_job_view,
                acceptedParentLayout,
                false
            ).apply {

                val declineBtn = findViewById<View>(R.id.on_demand_view_accepted_decline)
                val acceptBtn = findViewById<Button>(R.id.on_demand_view_accepted_start)

                //set decline btm
                declineBtn?.setOnClickListener {
                    dashboardViewModel.removeOnDemandJob(job)
                }

                //set accept btn
                when (job.state) {
                    OnDemandJobState.ACCEPTED.name -> {
                        acceptBtn.setText(R.string.on_demand_start)
                        acceptBtn.setOnClickListener {

                            dashboardViewModel.updateOnDemandJob(
                                job,
                                OnDemandJobState.CURRENT.name
                            ) { isCurrentJobExist ->
                                if (isCurrentJobExist)
                                    showDialogOnUpdateCurrentJob(job)
                            }
                            setDutyState(OnDemandState.ON_DUTY)
                        }
                        acceptBtn.setBackgroundResource(R.drawable.selector_default_btn)
                        acceptBtn.setTextColor(Color.WHITE)
                        declineBtn.isVisible = true
                    }

                    OnDemandJobState.PAUSED.name -> {
                        acceptBtn.setText(R.string.on_demand_resume)
                        acceptBtn.setOnClickListener {
                            dashboardViewModel.updateOnDemandJob(
                                job,
                                OnDemandJobState.CURRENT.name
                            ) { isCurrentJobExist ->
                                if (isCurrentJobExist)
                                    showDialogOnUpdateCurrentJob(job)
                            }
                            setDutyState(OnDemandState.ON_DUTY)
                        }
                        acceptBtn.setBackgroundResource(R.drawable.selector_default_btn_stroke)
                        acceptBtn.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorDefButton
                            )
                        )
                        declineBtn.isVisible = true
                    }

                    OnDemandJobState.CURRENT.name -> {
                        acceptBtn.setText(R.string.on_demand_pause)
                        acceptBtn.setOnClickListener {
                            dashboardViewModel.updateOnDemandJob(
                                job,
                                OnDemandJobState.PAUSED.name,
                                null
                            )
                            setDutyState(OnDemandState.ONLINE)
                        }
                        acceptBtn.setBackgroundResource(R.drawable.selector_orange_btn_stroke)
                        acceptBtn.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorOrangeButton
                            )
                        )
                        declineBtn.isVisible = false
                    }
                }
                findViewById<TextView>(R.id.on_demand_view_accepted_name).text = job.getName
                acceptedParentLayout.addView(this)
            }
        }

        //completed
        val completedList = list.filter { it.state == OnDemandJobState.COMPLETE.name }
        completedList.forEach { job ->
            layoutInflater.inflate(R.layout.on_demand_completed_view, acceptedParentLayout, false)
                .apply {
                    val completedHideBtn = findViewById<View>(R.id.on_demand_completed_view_hide)
                    completedHideBtn?.setOnClickListener {
                        dashboardViewModel.removeOnDemandJob(job)
                    }
                    //completedHideBtn.visible(false)
                    findViewById<TextView>(R.id.on_demand_completed_view_name).text = job.getName

                    val riskScoreText = findViewById<TextView>(R.id.on_demand_completed_risk)
                    val riskScoreLoad = findViewById<View>(R.id.on_demand_completed_risk_load)

                    val countText = findViewById<TextView>(R.id.on_demand_completed_trip_count)
                    val countLoad = findViewById<View>(R.id.on_demand_completed_trip_count_load)

                    val mileageText = findViewById<TextView>(R.id.on_demand_completed_mileage)
                    val mileageLoad = findViewById<View>(R.id.on_demand_completed_mileage_load)

                    val timeText = findViewById<TextView>(R.id.on_demand_completed_time)
                    val timeLoad = findViewById<View>(R.id.on_demand_completed_time_load)

                    dashboardViewModel.getIndividualDataByTag(job)
                        .observe(viewLifecycleOwner) { result ->
                            result.onSuccess { data ->
                                riskScoreLoad.isVisible = false
                                countLoad.isVisible = false
                                mileageLoad.isVisible = false
                                timeLoad.isVisible = false
                                completedHideBtn.isVisible = true

                                riskScoreText.text = data.second.overallScore.toString()
                                riskScoreText.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        data.second.overallScore.getColorByScore()
                                    )
                                )
                                countText.text = data.first.tripsCount.toString()
                                mileageText.text =
                                    dashboardViewModel.measuresFormatter.getDistanceMeasureValue()
                                        .let {
                                            return@let when (it) {
                                                DistanceMeasure.KM -> getString(
                                                    R.string.template_km,
                                                    data.first.mileageKm.toString()
                                                )

                                                DistanceMeasure.MI -> getString(
                                                    R.string.template_mi,
                                                    data.first.mileageKm.toMiles().toString()
                                                )
                                            }
                                        }

                                val time = "${data.first.drivingTime / 60} min"
                                timeText.text = time
                            }
                        }
                    completedParentLayout.addView(this)
                }
        }
    }

    private fun visibleDemandPanels(list: List<DashboardOnDemandJob>?) {

        // for accept panel
        val showAcceptPanel = list?.any {
            it.state == OnDemandJobState.ACCEPTED.name || it.state == OnDemandJobState.CURRENT.name || it.state == OnDemandJobState.PAUSED.name
        } ?: false
        binding.onDemand.onDemandAcceptedJobs.isVisible = showAcceptPanel
        binding.onDemand.onDemandAcceptSplit.isVisible = showAcceptPanel

        // for complete panel
        val showCompletePanel = list?.any { it.state == OnDemandJobState.COMPLETE.name }
            ?: false

        binding.onDemand.onDemandCompleted.isVisible = showCompletePanel
    }

    private fun showDialogOnUpdateCurrentJob(
        jobToCurrent: DashboardOnDemandJob,
        isStart: Boolean = false
    ) {

        activity?.runOnUiThread {
            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.setTitle("Job in progress")
            alertDialog.setMessage("Do you want to Complete or Pause current job?")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel") { dialog, _ ->
                dialog.cancel()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "COMPLETE") { _, _ ->
                dashboardViewModel.setOnDemandCurrentJobToCompleted()
                if (!isStart)
                    dashboardViewModel.updateOnDemandJob(
                        jobToCurrent,
                        OnDemandJobState.CURRENT.name,
                        null
                    )
                else {
                    dashboardViewModel.addAndStartOnDemandJob(jobToCurrent.name, {
                        showAlertForExistOnDemandJobName()
                    }, null)
                    setDutyState(OnDemandState.ONLINE)
                }
            }
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PAUSE") { _, _ ->
                dashboardViewModel.setOnDemandCurrentJobToPaused()
                if (!isStart)
                    dashboardViewModel.updateOnDemandJob(
                        jobToCurrent,
                        OnDemandJobState.CURRENT.name,
                        null
                    )
                else {
                    dashboardViewModel.addAndStartOnDemandJob(jobToCurrent.name, {
                        showAlertForExistOnDemandJobName()
                    }, null)
                    setDutyState(OnDemandState.ONLINE)
                }
            }
            alertDialog.setCancelable(true)

            alertDialog.setOnShowListener {
                //cancel btn
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.DKGRAY)
                //complete btn
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorDefButton))
                //pause btn
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOrangeButton
                        )
                    )
            }

            alertDialog.show()
        }
    }

    private fun showAlertForExistOnDemandJobName() {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setMessage("This job name is already taken")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun showDialogForCompleteCurrentJob(action: () -> Unit) {

        if (!dashboardViewModel.isExistsOnDemandCurrentJobs()) {
            action()
            return
        }

        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("Your current job will be automatically paused.")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES") { _, _ ->
            action()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}