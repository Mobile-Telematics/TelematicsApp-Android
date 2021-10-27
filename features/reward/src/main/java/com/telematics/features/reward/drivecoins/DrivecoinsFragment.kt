package com.telematics.features.reward.drivecoins

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.tabs.TabLayout
import com.telematics.content.utils.BaseFragment
import com.telematics.domain.model.reward.DriveCoinsDetailedData
import com.telematics.domain.model.reward.DriveCoinsDuration
import com.telematics.reward.R
import com.telematics.reward.databinding.FragmentDrivecoinsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DrivecoinsFragment : BaseFragment() {

    @Inject
    lateinit var driveCoinsViewModel: DriveCoinsViewModel

    private var lastSelectedDriveCoinsTab = 0
    private lateinit var statisticPager: ViewPager2
    private lateinit var statisticAdapter: DriveCoinsViewPagerAdapter

    private lateinit var binding: FragmentDrivecoinsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrivecoinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        init()
    }

    private fun setListeners() {

        val swipeRefreshLayout = binding.driveCoinSwipeRefresh
        swipeRefreshLayout.setOnRefreshListener {

            Handler().postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 1000)

            init()
        }

        statisticPager = binding.statisticPager


        binding.bottomTabsTravelingButton.setOnClickListener {
            statisticPager.setCurrentItem(0, false)
        }

        binding.bottomTabsSafeButton.setOnClickListener {
            statisticPager.setCurrentItem(1, false)
        }

        binding.bottomTabsEcoButton.setOnClickListener {
            statisticPager.setCurrentItem(2, false)
        }

        statisticAdapter =
            DriveCoinsViewPagerAdapter(this, null, driveCoinsViewModel.measuresFormatter)
        statisticPager.adapter = statisticAdapter
        statisticPager.offscreenPageLimit = 3

        val arrowParentView = binding.driveCoinsSelectArrowParent

        statisticPager.isUserInputEnabled = false
        statisticPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                lastSelectedDriveCoinsTab = position
                when (position) {
                    1 -> arrowParentView.gravity = Gravity.CENTER
                    2 -> arrowParentView.gravity = Gravity.END
                    else -> arrowParentView.gravity = Gravity.START
                }
            }
        })

        val durationTabs = binding.durationTabs
        durationTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                statisticPager.setCurrentItem(lastSelectedDriveCoinsTab, false)
                when (tab?.position) {
                    1 -> observeTotalCoins(DriveCoinsDuration.DAY)
                    2 -> observeTotalCoins(DriveCoinsDuration.THIS_MONTH)
                    3 -> observeTotalCoins(DriveCoinsDuration.LAST_MONTH)
                    else -> observeTotalCoins(DriveCoinsDuration.ALL_TIME)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        showDailyLimit(0)
        binding.coinsGraph.setNoDataText("")
    }

    private fun init() {

        binding.root.apply {
            alpha = 0f
            animate().setDuration(400).alpha(1f).start()
        }

        observeDailyLimit()
        observeTotalCoins(DriveCoinsDuration.ALL_TIME)
    }

    /***/
    private fun observeDailyLimit() {

        driveCoinsViewModel.getDailyLimit().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showDailyLimit(it.dailyLimitData)
                observeDriveCoinsDaily(it.dailyLimitData)
            }
            result.onFailure {
                showDailyLimit(0)
            }
        }
    }

    private fun observeDriveCoinsDaily(dailyLimit: Int) {

        driveCoinsViewModel.getDaily().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                if (it.isNullOrEmpty())
                    showEmptyChart()
                else
                    showChart(it, dailyLimit)
            }
            result.onFailure {
                showEmptyChart()
            }
        }
    }

    private fun observeTotalCoins(duration: DriveCoinsDuration) {

        driveCoinsViewModel.getTotalCoinsByDuration(duration)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    showTotalCoin(it.totalEarnedCoins)
                    if (duration == DriveCoinsDuration.ALL_TIME)
                        showAcquiredCoins(it.acquiredCoins)
                }
                result.onFailure {

                }
            }
        observeDetailedByDuration(duration)
    }

    private fun observeDetailedByDuration(duration: DriveCoinsDuration) {

        driveCoinsViewModel.getDetailedByDuration(duration)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    showDetailedCoins(it)
                }
                result.onFailure {
                    showDetailedCoins(DriveCoinsDetailedData())
                }
            }
    }


    /***/
    private val xAxisValues = mutableListOf<String>()
    private fun initializeChart(
        values: List<Pair<Int, Int>>,
        dailyLimitData: Int,
        grayChart: Boolean = false
    ) {

        val coinsGraph = binding.coinsGraph

        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        val max = (values.maxByOrNull { it.second }?.second)?.toFloat() ?: 100f

        coinsGraph.clear()
        val entries = mutableListOf<Entry>()
        val entriesDaily = mutableListOf<Entry>()
        xAxisValues.clear()

        values.withIndex().forEach {
            entries.add(Entry((it.index).toFloat(), it.value.second.toFloat()))
            entriesDaily.add(Entry((it.index).toFloat(), dailyLimitData.toFloat()))
            xAxisValues.add(it.value.first.toString())
        }

        // main chart
        val d1 = LineDataSet(entries, "DataSet 1").apply {
            color = if (grayChart) Color.DKGRAY else Color.rgb(84, 199, 81)
            setDrawCircles(false)
            fillColor = if (grayChart) Color.LTGRAY else Color.rgb(209, 231, 202)
            setDrawFilled(true)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineWidth = 3f
        }

        // daily limit line
        val d2 = LineDataSet(entriesDaily, "DataSet 2").apply {
            color = Color.rgb(90, 90, 90)
            setDrawCircles(false)
            fillAlpha = 0
            setDrawFilled(true)
            mode = LineDataSet.Mode.LINEAR
            lineWidth = 1f
            enableDashedLine(40f, 20f, 0f)
        }
        dataSets.add(d1)
        dataSets.add(d2)

        val lineData = LineData(dataSets)
        lineData.setDrawValues(false)
        lineData.isHighlightEnabled = false

        coinsGraph.description.isEnabled = false
        coinsGraph.legend.isEnabled = false
        coinsGraph.data = lineData

        coinsGraph.axisLeft.setDrawGridLines(false)
        coinsGraph.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        coinsGraph.axisLeft.setLabelCount(1, true)
        coinsGraph.axisLeft.granularity = 25f
        coinsGraph.axisLeft.axisMinimum = 0f
        coinsGraph.axisLeft.axisMaximum = max + 2
        coinsGraph.axisLeft.yOffset = -5f

        coinsGraph.setVisibleYRangeMinimum(110f, YAxis.AxisDependency.LEFT)
        coinsGraph.axisRight.setDrawGridLines(false)
        coinsGraph.axisRight.isEnabled = false
        coinsGraph.xAxis.setDrawGridLines(false)
        coinsGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        coinsGraph.xAxis.labelCount = xAxisValues.size
        coinsGraph.xAxis.granularity = 1f
        coinsGraph.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

        coinsGraph.invalidate()
        coinsGraph.animateXY(0, 200)
    }

    fun showChart(driveCoinsDetailedData: List<Pair<Int, Int>>, dailyLimitData: Int) {

        initializeChart(driveCoinsDetailedData, dailyLimitData)
    }

    fun showEmptyChart() {

        val values = listOf(
            Pair(1, 1),
            Pair(2, 10),
            Pair(3, 50),
            Pair(4, 10),
            Pair(5, 60),
            Pair(6, 50),
            Pair(6, 40)
        )

        val dailyLimitData = 20
        initializeChart(values, dailyLimitData, grayChart = true)
    }

    fun showDailyLimit(dailyLimit: Int) {

        binding.limitTitle.text = getString(R.string.daily_limit_placeholder, dailyLimit.toString())
    }

    private fun showAcquiredCoins(acquiredCoins: Int?) {

        binding.coinsCount.text = (acquiredCoins ?: 0).toString()
    }

    private fun showTotalCoin(totalEarnedCoins: Int?) {

        binding.dailyCoins.text = (totalEarnedCoins ?: 0).toString()
    }

    fun showDetailedCoins(data: DriveCoinsDetailedData) {

        val travelingTextColor = when {
            data.travellingSum == 0 -> ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
            data.travellingSum > 0 -> ContextCompat.getColor(
                requireContext(),
                R.color.colorGreenText
            )
            else -> ContextCompat.getColor(requireContext(), R.color.colorRedText)
        }
        val safeDrivingTextColor = when {
            data.safeDrivingSum == 0 -> ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
            data.safeDrivingSum > 0 -> ContextCompat.getColor(
                requireContext(),
                R.color.colorGreenText
            )
            else -> ContextCompat.getColor(requireContext(), R.color.colorRedText)
        }
        val ecoDrivingTextColor = when {
            data.ecoDrivingSum == 0 -> ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryText
            )
            data.ecoDrivingSum > 0 -> ContextCompat.getColor(
                requireContext(),
                R.color.colorGreenText
            )
            else -> ContextCompat.getColor(requireContext(), R.color.colorRedText)
        }

        binding.travelingSum
        binding.travelingSum.text = data.travellingSum.toString()
        binding.travelingSum.setTextColor(travelingTextColor)


        binding.safeSum.text = data.safeDrivingSum.toString()
        binding.safeSum.setTextColor(safeDrivingTextColor)


        binding.ecoDrivingSum.text = data.ecoDrivingSum.toString()
        binding.ecoDrivingSum.setTextColor(ecoDrivingTextColor)

        statisticPager.adapter = null
        if (!this::statisticAdapter.isInitialized) {
            statisticAdapter =
                DriveCoinsViewPagerAdapter(this, data, driveCoinsViewModel.measuresFormatter)
            statisticPager.adapter = statisticAdapter
        }
        if (statisticPager.adapter == null) {
            statisticPager.adapter = statisticAdapter
        }
        statisticAdapter.updateData(data)

        statisticPager.setCurrentItem(lastSelectedDriveCoinsTab, false)
    }
}