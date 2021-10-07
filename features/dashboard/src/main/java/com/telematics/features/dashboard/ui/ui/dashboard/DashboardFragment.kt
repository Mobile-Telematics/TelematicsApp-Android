package com.telematics.features.dashboard.ui.ui.dashboard

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
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
import com.telematics.data.utils.Resource
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.statistics.*
import com.telematics.features.dashboard.ui.ui.chart.DashboardTypePagerAdapter
import com.telematics.features.dashboard.ui.ui.ecoscoring.DashboardEcoScoringTabAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_new_dashboard_rank.*
import kotlinx.android.synthetic.main.layout_eco_scoring_dashboard.*
import kotlinx.android.synthetic.main.layout_last_trip_dashboard.view.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    companion object {
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

    private val TAG = "DashboardFragment"

    private val DISTANCE_LIMIT = BuildConfig.DASHBOARD_DISTANCE_LIMIT

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

        binding.dashboardEmptyLastTrip.dashboardEmptyLastTripPermissions.setOnClickListener {
            openLinkTelematicsLink()
        }
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

        binding.dashboardEmptyLastTrip.dashboardEmptyLastTripLayout.setOnClickListener {
            navToFeed()
        }

        binding.include3.lastTripParent.setOnClickListener {
            navToFeed()
        }

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
    }

    private fun observeDriveCoins() {

        fun showDriveCoins(coins: Int) {
            binding.include.coinsValue.text = coins.toString()
        }

        dashboardViewModel.driveCoinsData.removeObservers(this)
        dashboardViewModel.driveCoinsData.observe(viewLifecycleOwner, {
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
        })
        dashboardViewModel.getDriveCoins()
    }

    private fun observeRank() {

        Log.d(TAG, "observeRank: start")

        fun setRank(rank: String) {
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
            rankValue.text = spannable
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
        dashboardViewModel.userIndividualStatisticsData.observe(viewLifecycleOwner, {
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
        })
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
        binding.dashboardEmptyLastTrip.root.visibility = View.VISIBLE
        binding.dashboardEmptyLastTrip.dashboardEmptyLastTripPermissions.setOnClickListener {
            //todo open permissions
        }
        binding.dashboardEmptyLastTrip.root.setOnClickListener {
            navToFeed()
        }

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
        dashboardViewModel.scoreLiveData.observe(viewLifecycleOwner, {
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
        })

        dashboardViewModel.getStatistics()
    }

    private fun observeLastTrip() {

        dashboardViewModel.getLastTrip().observe(viewLifecycleOwner) { result ->
            result.onSuccess { tripData ->

                if (tripData == null) return@onSuccess
                binding.dashboardEmptyLastTrip.dashboardEmptyLastTripParent.isVisible = false
                binding.include3.lastTripParent.isVisible = true


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
                binding.include3.eventTripOverallScore.eventTripOverallScore.setTextColor(
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
                binding.include3.lastTripImage.setImageBitmap(it)
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
            ecoScoringMainScore.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), color))
            ecoScoringMainScore.text = data.score.toString()

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
        dashboardViewModel.mainEcoScoringLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Failure -> {
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    showMainEcoScoring(it.data ?: StatisticEcoScoringMain())
                }
            }
        })
        dashboardViewModel.getMainEcoScoring()
    }

    private fun initEcoScoringTable() {

        fun showContent(data: StatisticEcoScoringTabsData?) {

            val pagerAdapter = DashboardEcoScoringTabAdapter(
                this.childFragmentManager,
                data,
                this.requireContext(),
                dashboardViewModel.measuresFormatter
            )
            pager.adapter = pagerAdapter
            tabLayout.setupWithViewPager(pager)
        }

        dashboardViewModel.tableEcoScoringLiveData.removeObservers(this)
        dashboardViewModel.tableEcoScoringLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Failure -> {

                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    showContent(it.data)
                }
            }
        })

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
}