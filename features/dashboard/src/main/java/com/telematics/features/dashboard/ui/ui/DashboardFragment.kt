package com.telematics.features.dashboard.ui.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.telematics.domain.model.dashboard.*
import com.telematics.features.dashboard.ui.ui.chart.DashboardTypePagerAdapter
import com.telematics.features.dashboard.ui.ui.ecoscoring.DashboardEcoScoringTabAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_eco_scoring_dashboard.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class DashboardFragment : Fragment() {

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

        binding.drivingScoresPager.offscreenPageLimit = 6
        binding.drivingScoresPager.adapter = scoringAdapter
        binding.progressIndicator.setViewPager(binding.drivingScoresPager)
        scoringAdapter.registerAdapterDataObserver(binding.progressIndicator.adapterDataObserver)

        init()
    }

    private fun init() {

        observeUserIndividualStatistics()
        observeDriveCoins()
    }

    private fun observeDriveCoins() {

        fun showDriveCoins(coins: Int) {
            binding.include.coinsValue.text = coins.toString()
        }

        dashboardViewModel.driveCoinsData.removeObservers(this)
        dashboardViewModel.driveCoinsData.observe(this, {
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

    private fun observeUserIndividualStatistics() {

        fun showUserIndividualStatistics(data: UserStatisticsIndividualData?) {
            if ((data?.mileageKm ?: .0) >= DISTANCE_LIMIT) {
                binding.dashboardEmpty.visibility = View.GONE
                observeDrivingDetails()
                initEcoScoring()
            } else {
                showEmptyDashboard(data)
            }
        }

        dashboardViewModel.userIndividualStatisticsData.removeObservers(this)
        dashboardViewModel.userIndividualStatisticsData.observe(this, {
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

    private fun showEmptyDashboard(inData: UserStatisticsIndividualData?) {

        binding.progressBar2.visibility = View.GONE

        val data = inData ?: UserStatisticsIndividualData()

        //top data
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
            //todo go to Feed
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

        binding.dashboardDistanceValue.text = resources.getString(
            R.string.dashboard_new_template_progress_km,
            data.mileageKm.format("0"),
            DISTANCE_LIMIT.toString()
        )


        val outputData = DashboardEcoScoringTabsData(
            DashboardEcoScoringTabData(-1.0, -1.0, -1.0),
            DashboardEcoScoringTabData(-1.0, -1.0, -1.0),
            DashboardEcoScoringTabData(-1.0, -1.0, -1.0)
        )
        //todo outputData toMiles
        binding.include4.pager.adapter = DashboardEcoScoringTabAdapter(
            this.childFragmentManager,
            outputData,
            this.requireContext()
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

        Log.i("MMMMM Advanced", "initializeChart")
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

        binding.include2.milage.bottomText.text = getString(R.string.dashboard_new_km)
        binding.include2.milage.middleText.text = getString(R.string.dashboard_new_km)
        binding.include2.milage.topText.text = (individualData.mileageKm).format()

        binding.include2.trips.topText.text = (individualData.tripsCount).toString()
        binding.include2.trips.middleText.text = getString(R.string.dashboard_new_total_trips)
        binding.include2.trips.bottomText.text = getString(R.string.dashboard_new_quantity)

        val durationInHours = (individualData.drivingTime) / 60 // minutes
        binding.include2.time.topText.text = durationInHours.toString()
        binding.include2.time.middleText.text = getString(R.string.dashboard_new_time_driven)
        binding.include2.time.bottomText.text = getString(R.string.dashboard_new_hours)
    }

    private fun observeDrivingDetails() {

        fun showContent(dashboardData: DashboardScoringData) {
            fillData(
                dashboardData.drivingDetailsData,
                dashboardData.userStatisticsScoreData
            )
            fillTripData(dashboardData.userStatisticsIndividualData)
            binding.progressBar2.visibility = View.GONE
        }

        dashboardViewModel.scoreLiveData.removeObservers(this)
        dashboardViewModel.scoreLiveData.observe(this, {
            when (it) {
                is Resource.Failure -> {
                    showEmptyDashboard(null)
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val dashboardData = it.data ?: DashboardScoringData()
                    showContent(dashboardData)
                }

            }
        })

        dashboardViewModel.getStatistics()
    }


    private fun initEcoScoring() {

        initMainEcoScoring()
        initEcoScoringTable()
    }

    private fun initMainEcoScoring() {

        fun showMainEcoScoring(data: DashboardEcoScoringMain) {
            val ecoScoring = binding.include4
            ecoScoring.layoutItemEcoScoringFuel.itemEcoScoringProgress.setProgressWithColor(data.fuel)
            ecoScoring.layoutItemEcoScoringTires.itemEcoScoringProgress.setProgressWithColor(data.tires)
            ecoScoring.layoutItemEcoScoringBrakes.itemEcoScoringProgress.setProgressWithColor(data.brakes)
            ecoScoring.layoutItemEcoScoringCost.itemEcoScoringProgress.setProgressWithColor(data.cost)

            val color = data.score.getColorByScore()
            ecoScoringMainScore.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), color))
            ecoScoringMainScore.text = data.score.toString()

            //todo add rotation for array
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
        dashboardViewModel.mainEcoScoringLiveData.observe(this, {
            when (it) {
                is Resource.Failure -> {
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    showMainEcoScoring(it.data ?: DashboardEcoScoringMain())
                }
            }
        })
        dashboardViewModel.getMainEcoScoring()
    }

    private fun initEcoScoringTable() {

        fun showContent(data: DashboardEcoScoringTabsData?) {

            //todo add toMiles
//            if (settingsInteractor.getDistanceMeasure() == DistanceMeasure.MI)
//                data?.toMiles()
            val pagerAdapter = DashboardEcoScoringTabAdapter(
                this.childFragmentManager,
                data,
                this.requireContext()
            )
            pager.adapter = pagerAdapter
            tabLayout.setupWithViewPager(pager)
        }

        dashboardViewModel.tableEcoScoringLiveData.removeObservers(this)
        dashboardViewModel.tableEcoScoringLiveData.observe(this, {
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
}