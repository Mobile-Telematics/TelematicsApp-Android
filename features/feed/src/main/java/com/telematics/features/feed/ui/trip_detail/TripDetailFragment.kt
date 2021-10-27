package com.telematics.features.feed.ui.trip_detail

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.here.android.mpa.common.*
import com.here.android.mpa.mapping.*
import com.here.android.mpa.mapping.Map
import com.telematics.content.utils.BaseFragment
import com.telematics.data.extentions.color
import com.telematics.data.extentions.convertDpToPx
import com.telematics.data.extentions.drawable
import com.telematics.data.extentions.format
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.tracking.TripData
import com.telematics.domain.model.tracking.TripDetailsData
import com.telematics.domain.model.tracking.TripPointData
import com.telematics.features.feed.model.AlertType
import com.telematics.features.feed.model.ChangeDriverTypeDialog
import com.telematics.features.feed.model.SpeedType
import com.telematics.feed.R
import com.telematics.feed.databinding.FragmentTripDatailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TripDetailFragment : BaseFragment() {

    companion object {
        const val TRIP_DETAILS_TRIP_DATA_KEY = "TRIP_DETAILS_TRIP_DATA_KEY"
        const val TRIP_DETAILS_TRIP_POS_KEY = "TRIP_DETAILS_TRIP_POS_KEY"
    }

    private val TAG = "TripDetailFragment"

    @Inject
    lateinit var tripDetailViewModel: TripDetailViewModel

    private var currentTripPosition = -1
    private val eventsMarkersList = mutableListOf<MapObject>()
    private val eventsList = mutableListOf<TripPointData>()
    private var listMapObjects = ArrayList<MapObject>()
    private var selectedMarker: MapObject? = null
    private var overlay: MapOverlay? = null
    private var isNeedUpdateFeedList = false

    //UI
    lateinit var binding: FragmentTripDatailBinding
    private var mapFragment: AndroidXMapFragment? = null
    private var map: Map? = null
    private lateinit var claimDialog: AlertDialog
    private lateinit var changeDriverTypeDialog: ChangeDriverTypeDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTripDatailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()

        val tripPos = arguments?.getInt(TRIP_DETAILS_TRIP_POS_KEY)!!
        currentTripPosition = tripPos

        initViews()
        setListeners()
    }

    /** init UIs */
    private fun initViews() {

        initMap()
        initBottomSheet()
        initClaimDialog()
        initChangeDriverTypeDialog()

        //set first state
        getTripDetailsByPos(currentTripPosition)
    }

    private fun initMap() {

        MapSettings.setDiskCacheRootPath(
            requireContext().getExternalFilesDir(null).toString() + File.separator + ".here-maps"
        )
        mapFragment?.retainInstance = true
        mapFragment =
            childFragmentManager.findFragmentById(R.id.tripDetailsMap) as AndroidXMapFragment
        mapFragment?.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapFragment?.map
                map?.mapScheme =
                    Map.Scheme.NORMAL_NIGHT_GREY
                map?.setUseSystemLanguage()
                getTripDetailsByPos(currentTripPosition)
                mapFragment?.mapGesture?.addOnGestureListener(object :
                    MapGesture.OnGestureListener {

                    override fun onMapObjectsSelected(p0: MutableList<ViewObject>): Boolean {
                        val markers = p0.filter {
                            it.baseType == ViewObject.Type.USER_OBJECT && (it as MapObject).type == MapObject.Type.MARKER && eventsMarkersList.contains(
                                it
                            )
                        } as List<MapMarker>
                        if (markers.size > 1) {
                            map?.zoomTo(
                                GeoBoundingBox.getBoundingBoxContainingGeoCoordinates(
                                    markers.map { it.coordinate })!!, Map.Animation.LINEAR, 30f
                            )
                            return false
                        }
                        if (markers.isEmpty()) return false
                        val marker = markers[0]
                        if (marker != selectedMarker) {
                            overlay?.let {
                                map?.removeMapOverlay(overlay!!)
                                overlay = null
                            }
                            selectedMarker = marker
                            initAndFillOverlay(marker)
                        } else {
                            if (overlay != null) {
                                map?.removeMapOverlay(overlay!!)
                                overlay = null
                            } else {
                                initAndFillOverlay(marker)
                            }
                        }
                        return false
                    }

                    override fun onTapEvent(p0: PointF): Boolean {
                        overlay?.let {
                            map?.removeMapOverlay(overlay!!)
                            overlay = null
                        }
                        return false
                    }

                    override fun onDoubleTapEvent(p0: PointF): Boolean {
                        return false
                    }

                    override fun onPanStart() {}
                    override fun onPanEnd() {}
                    override fun onMultiFingerManipulationStart() {}
                    override fun onMultiFingerManipulationEnd() {}
                    override fun onPinchLocked() {}
                    override fun onPinchZoomEvent(p0: Float, p1: PointF): Boolean = false
                    override fun onRotateLocked() {}
                    override fun onRotateEvent(p0: Float): Boolean = false
                    override fun onTiltEvent(p0: Float): Boolean = false
                    override fun onLongPressEvent(p0: PointF): Boolean = false
                    override fun onLongPressRelease() {}
                    override fun onTwoFingerTapEvent(p0: PointF): Boolean = false
                }, 0, true)
            } else {
                Log.d(TAG, "initMap: $error")
                showProgress(false)
            }
        }
    }

    private fun initAndFillOverlay(marker: MapMarker) {
        val index = eventsMarkersList.indexOf(marker)
        val event = eventsList[index]
        val v = LayoutInflater.from(context)
            .inflate(R.layout.info_bubble_layout, binding.tripDetailsContainer as ViewGroup, false)
        overlay =
            MapOverlay(v, GeoCoordinate(marker.coordinate.latitude, marker.coordinate.longitude))
        // для custom_info_bubble.xml размером 200dpх205dp. Если изменить размер, то придется подбирать снова
        overlay?.anchorPoint =
            PointF(convertDpToPx(requireContext(), 100f), convertDpToPx(requireContext(), 215f))
        val alertType = AlertType.from(event.alertType)
        val typeText = resources.getString(alertType.getStringRes())
        v.findViewById<View>(R.id.info_not_event_button).setOnClickListener {
            showEventDialog(event, alertType)
        }
        if (event.edited) v.findViewById<View>(R.id.info_not_event_button).visibility = View.GONE
        v.findViewById<ImageView>(R.id.info_image).setImageResource(alertType.drawableResId)
        v.findViewById<TextView>(R.id.info_type_text).text = typeText
        v.findViewById<TextView>(R.id.info_date_text).text = event.date
        v.findViewById<TextView>(R.id.info_max_force).text =
            getString(R.string.trip_details_max_force, event.alertValue.format())
        v.findViewById<TextView>(R.id.info_speed_text).text =
            getString(R.string.trip_details_km_h, event.speed.format())

        map?.addMapOverlay(overlay!!)
    }

    private fun initBottomSheet() {

        val tripBottomSheet = binding.tripDetailsBottomSheet.tripBottomSheet
        val tripBottomSheetSwipe = binding.tripDetailsBottomSheet.tripBottomSheetSwipe
        val tripBottomSheetButtons = binding.tripDetailsBottomSheet.tripBottomSheetButtons
        val nextArrow = binding.nextArrow
        val prevArrow = binding.prevArrow
        val tripBottomSheetTypeLayout = binding.tripDetailsBottomSheet.tripBottomSheetTypeLayout

        val bottomSheetBehavior = BottomSheetBehavior.from(tripBottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                var slideOffsetForAnimation = slideOffset
                if (slideOffsetForAnimation < 0) slideOffsetForAnimation = 0f
                tripBottomSheetSwipe.animate()
                    .alpha(1f - slideOffsetForAnimation)
                    .setDuration(0)
                    .withEndAction { }
                    .start()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        tripBottomSheetTypeLayout.isVisible = false
                        nextArrow.isVisible = true
                        prevArrow.isVisible = true
                        tripBottomSheetButtons.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction {
                                tripBottomSheetSwipe.isVisible = true
                                tripBottomSheetButtons.isVisible = true
                            }
                            .start()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        tripBottomSheetTypeLayout.isVisible = true
                        nextArrow.isVisible = false
                        prevArrow.isVisible = false
                        tripBottomSheetSwipe.isVisible = false
                        tripBottomSheetButtons.isVisible = true
                        tripBottomSheetButtons.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }
                }
            }
        })

        tripBottomSheetSwipe.setOnClickListener {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun initClaimDialog() {
        claimDialog = AlertDialog.Builder(context)
            .setView(R.layout.claim_dialog_view)
            .setCancelable(true)
            .create()
        claimDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initChangeDriverTypeDialog() {

        changeDriverTypeDialog = ChangeDriverTypeDialog(requireContext())
    }

    private fun setListeners() {

        binding.tripDetailsToolbar.setOnClickListener {
            finish()
        }

        binding.nextArrow.setOnClickListener {
            next()
        }
        binding.prevArrow.setOnClickListener {
            prev()
        }
    }

    /** go to next trip details */
    private fun next() {

        currentTripPosition++
        getTripDetailsByPos(currentTripPosition)
    }

    /** go to previous trip details */
    private fun prev() {

        currentTripPosition--
        getTripDetailsByPos(currentTripPosition)
    }

    /** observe trip details */
    private fun getTripDetailsByPos(position: Int) {

        showMapFragment(false)
        showProgress(true)
        setTranslucentForBottomSheet(true)
        showPreviousArrow(position > 0)

        tripDetailViewModel.getTripDetailsByPos(position)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess { tripDetails ->
                    tripDetails?.let {
                        bindTrip(tripDetails)
                    } ?: run {
                        currentTripPosition = 0
                        getTripDetailsByPos(currentTripPosition)
                        showError("getTripDetailsByPos tripId == null")
                    }
                }
                result.onFailure {
                    showError(it.message ?: "getTripDetailsByPos onFailure")
                }
            }
    }

    private fun bindTrip(tripDetailsData: TripDetailsData) {

        renderTrip(tripDetailsData)
        bindTripType(tripDetailsData)

        binding.tripDetailsBottomSheet.tripBottomSheetOverallScore.text =
            tripDetailsData.rating.toString()
        binding.tripDetailsBottomSheet.tripBottomSheetTime.text = formatTime(tripDetailsData.time)

        tripDetailViewModel.formatter.getDistanceByKm(tripDetailsData.distance).apply {
            binding.tripDetailsBottomSheet.tripBottomSheetMileage.text =
                this.format()
        }

        tripDetailViewModel.formatter.getDistanceMeasureValue().apply {
            val distValue = when (this) {
                DistanceMeasure.KM -> R.string.dashboard_new_km
                DistanceMeasure.MI -> R.string.dashboard_new_mi
            }
            binding.tripDetailsBottomSheet.distanceMeasureText.text = getString(distValue)
        }

        binding.tripDetailsBottomSheet.tripBottomSheetOverallScore.setTextColor(
            when (tripDetailsData.rating) {
                in 0..40 -> requireContext().resources.color(R.color.colorRedText)
                in 41..60 -> requireContext().resources.color(R.color.colorOrangeText)
                in 61..80 -> requireContext().resources.color(R.color.colorYellowText)
                in 80..100 -> requireContext().resources.color(R.color.colorGreenText)
                else -> requireContext().resources.color(R.color.colorGreenText)
            }
        )

        binding.tripDetailsBottomSheet.tripBottomSheetCityStart.text =
            tripDetailsData.addressStartParts?.city
        binding.tripDetailsBottomSheet.tripBottomSheetCityFinish.text =
            tripDetailsData.addressFinishParts?.city
        binding.tripDetailsBottomSheet.tripBottomSheetAddressStart.text =
            if (tripDetailsData.addressStartParts?.distinct.isNullOrEmpty()) tripDetailsData.addressStartParts?.city else tripDetailsData.addressStartParts?.distinct
        binding.tripDetailsBottomSheet.tripBottomSheetAddressFinish.text =
            if (tripDetailsData.addressFinishParts?.distinct.isNullOrEmpty()) tripDetailsData.addressFinishParts?.city else tripDetailsData.addressFinishParts?.distinct  // street.plus(" ").plus(tripDetailsData.addressFinishParts?.house)
        binding.tripDetailsBottomSheet.tripBottomSheetTimeStart.text = tripDetailsData.startDate
        binding.tripDetailsBottomSheet.tripBottomSheetTimeFinish.text = tripDetailsData.endDate

        setScoringValueWithColors(
            binding.tripDetailsBottomSheet.tripBottomSheetAccValue,
            binding.tripDetailsBottomSheet.tripBottomSheetAccDot,
            tripDetailsData.ratingAcceleration
        )
        setScoringValueWithColors(
            binding.tripDetailsBottomSheet.tripBottomSheetBrakingValue,
            binding.tripDetailsBottomSheet.tripBottomSheetBrakingDot,
            tripDetailsData.ratingBraking
        )
        setScoringValueWithColors(
            binding.tripDetailsBottomSheet.tripBottomSheetPhoneValue,
            binding.tripDetailsBottomSheet.tripBottomSheetPhoneDot,
            tripDetailsData.ratingPhoneUsage
        )
        setScoringValueWithColors(
            binding.tripDetailsBottomSheet.tripBottomSheetSpeedingValue,
            binding.tripDetailsBottomSheet.tripBottomSheetSpeedingDot,
            tripDetailsData.ratingSpeeding
        )
        setScoringValueWithColors(
            binding.tripDetailsBottomSheet.tripBottomSheetCorneringValue,
            binding.tripDetailsBottomSheet.tripBottomSheetCorneringDot,
            tripDetailsData.ratingCornering
        )

        showMapFragment(true)
        showProgress(false)
        setTranslucentForBottomSheet(false)
    }

    private fun bindTripType(tripDetailsData: TripDetailsData) {

        val tripType = tripDetailsData.type

        binding.tripDetailsBottomSheet.tripBottomSheetTypeLayout.isEnabled = true
        binding.tripDetailsBottomSheet.tripBottomSheetTypeLayout.setOnClickListener(null)

        val tripTypeTextRes: Int
        val tripTypeImgRes: Int

        when (tripType) {
            TripData.TripType.DRIVER -> {
                tripTypeTextRes = R.string.progress_trip_type_driver
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_driver
            }
            TripData.TripType.PASSENGER -> {
                tripTypeTextRes = R.string.progress_trip_type_passenger
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_passenger
            }
            TripData.TripType.BUS -> {
                tripTypeTextRes = R.string.progress_trip_type_bus
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_bus
            }
            TripData.TripType.MOTORCYCLE -> {
                tripTypeTextRes = R.string.progress_trip_type_motorcycle
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_motorcycle
            }
            TripData.TripType.TRAIN -> {
                tripTypeTextRes = R.string.progress_trip_type_train
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_train
            }
            TripData.TripType.TAXI -> {
                tripTypeTextRes = R.string.progress_trip_type_taxi
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_taxi
            }
            TripData.TripType.BICYCLE -> {
                tripTypeTextRes = R.string.progress_trip_type_bicycle
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_bicycle
            }
            TripData.TripType.OTHER -> {
                tripTypeTextRes = R.string.progress_trip_type_other
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_other
            }
            else -> {
                tripTypeTextRes = R.string.progress_trip_type_other
                tripTypeImgRes = R.drawable.ic_event_trip_bubble_other
            }
        }

        binding.tripDetailsBottomSheet.tripBottomSheetTypeName.setText(tripTypeTextRes)
        binding.tripDetailsBottomSheet.tripBottomSheetTypeImg.setImageResource(tripTypeImgRes)

        binding.tripDetailsBottomSheet.tripBottomSheetTypeLayout.setOnClickListener {
            changeDriverTypeDialog.showDialog(tripType) { type ->
                changeTripTypeTo(tripDetailsData, type)
            }
        }

        binding.tripDetailsBottomSheet.tripBottomSheetDelete.setOnClickListener {
            showAnswerDialog(
                R.string.dialog_events_delete_trip,
                onPositive = {
                    showProgress(true)
                    tripDetailViewModel.setDeleteStatus(tripDetailsData.id!!)
                        .observe(viewLifecycleOwner) { result ->
                            result.onSuccess {
                                finish()
                            }
                            result.onFailure {
                                showMessage(R.string.server_error_error)
                            }
                            showProgress(false)
                        }
                }
            )
        }

        binding.tripDetailsBottomSheet.tripBottomSheetHide.setOnClickListener {
            showAnswerDialog(
                R.string.dialog_events_hide_trip,
                onPositive = {
                    showProgress(true)
                    tripDetailViewModel.hideTrip(tripDetailsData.id!!)
                        .observe(viewLifecycleOwner) { result ->
                            result.onSuccess {
                                finish()
                            }
                            result.onFailure {
                                showMessage(R.string.server_error_error)
                            }
                            showProgress(false)
                        }
                }
            )
        }
    }

    private fun setScoringValueWithColors(textView: TextView, imageView: ImageView, value: Int) {
        textView.text = value.toString()
        val color = when (value) {
            in 0..40 -> requireContext().resources.color(R.color.colorRedText)
            in 41..60 -> requireContext().resources.color(R.color.colorOrangeText)
            in 61..80 -> requireContext().resources.color(R.color.colorYellowText)
            in 80..100 -> requireContext().resources.color(R.color.colorGreenText)
            else -> requireContext().resources.color(R.color.colorGreenText)
        }
        val drawable = when (value) {
            in 0..40 -> requireContext().resources.drawable(R.drawable.ic_dot_red, requireContext())
            in 41..60 -> requireContext().resources.drawable(
                R.drawable.ic_dot_orange,
                requireContext()
            )
            in 61..80 -> requireContext().resources.drawable(
                R.drawable.ic_dot_yellow,
                requireContext()
            )
            in 80..100 -> requireContext().resources.drawable(
                R.drawable.ic_dot_green,
                requireContext()
            )
            else -> requireContext().resources.drawable(R.drawable.ic_dot_green, requireContext())
        }
        textView.setTextColor(color)
        imageView.setImageDrawable(drawable)
    }

    /** render trip on map */
    private fun renderTrip(tripDetailsData: TripDetailsData) {

        if (map != null) {
            var listCoordinates: MutableList<GeoCoordinate> = ArrayList()
            val listLines = ArrayList<MapPolyline>()
            val listMarkers = ArrayList<MapMarker>()
            map?.removeMapObjects(listMapObjects)
            listMapObjects.clear()
            eventsMarkersList.clear()
            eventsList.clear()

            renderPhoneUsage(tripDetailsData)

            val imageStart = Image()
            val imageStop = Image()
            imageStart.setImageResource(R.drawable.ic_dot_a_trip)
            imageStop.setImageResource(R.drawable.ic_dot_b_trip)

            val tripPoints = tripDetailsData.points!!
            tripPoints.indices.forEach { i ->
                val point = tripPoints[i]

                if (i > 0) {
                    listCoordinates.add(GeoCoordinate(point.latitude, point.longitude, 0.0))
                    val line = MapPolyline(GeoPolyline(listCoordinates))
                    val color = SpeedType.getColor(point.speedType)
                    line.lineColor = ContextCompat.getColor(requireContext(), color)
                    line.lineWidth = 8
                    listLines.add(line)
                    listCoordinates = ArrayList()
                    listCoordinates.add(GeoCoordinate(point.latitude, point.longitude, 0.0))

                } else if (i == 0) {
                    val marker = MapMarker()
                    marker.coordinate = GeoCoordinate(point.latitude, point.longitude, 0.0)
                    marker.icon = imageStart
                    listMarkers.add(marker)
                    listCoordinates.add(GeoCoordinate(point.latitude, point.longitude, 0.0))
                }

                if (i == tripPoints.size - 1) {
                    val marker = MapMarker()
                    marker.coordinate = GeoCoordinate(point.latitude, point.longitude, 0.0)
                    marker.icon = imageStop
                    listMarkers.add(marker)
                }

                if (AlertType.from(point.alertType) != AlertType.UNKNOWN) {
                    val marker = MapMarker()
                    marker.coordinate = GeoCoordinate(point.latitude, point.longitude, 0.0)

                    val image = Image()
                    try {
                        image.setBitmap(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                AlertType.getDrawableRes(point.alertType)
                            )!!.toBitmap()
                        )
                        marker.icon = image
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    listMarkers.add(marker)
                    if (AlertType.from(point.alertType) != AlertType.UNKNOWN) {
                        if (eventsList.find { it.latitude == point.latitude && it.longitude == point.longitude } != null) {
                            listMarkers.remove(marker)
                        } else {
                            if (AlertType.from(point.alertType) == AlertType.TURN) marker.zIndex = 3
                            eventsMarkersList.add(marker)
                            eventsList.add(point)
                        }
                    }
                }
            }

            if (listCoordinates.size > 1) {
                val line = MapPolyline(GeoPolyline(listCoordinates))
                val point = tripPoints[tripPoints.size - 1]
                val color = SpeedType.getColor(point.speedType)
                line.lineColor = ContextCompat.getColor(requireContext(), color)
                line.lineWidth = 8
                listLines.add(line)
            }
            for (line in listLines) {
                listMapObjects.add(line)
            }
            if (listMarkers.size > 0) {
                for (marker in listMarkers) {
                    listMapObjects.add(marker)
                }
            }
            if (map != null) {
                renderEventsAndRoute()
                if (tripPoints.isNotEmpty()) {
                    centerMapByRoute(tripPoints)
                }
            }
        }
    }

    private fun renderPhoneUsage(tripDetailsData: TripDetailsData) {

        val tripPoints = tripDetailsData.points!!
        var listCoordinates: MutableList<GeoCoordinate> = ArrayList()
        val listLines = ArrayList<MapPolyline>()

        tripPoints.indices.forEach { i ->
            val point = tripPoints[i]

            if (i > 0) {
                listCoordinates.add(GeoCoordinate(point.latitude, point.longitude, 0.0))
                if (point.usePhone) {
                    val phoneLine = MapPolyline(GeoPolyline(listCoordinates))
                    val phoneColor =
                        ContextCompat.getColor(requireContext(), R.color.colorPhoneUsage)
                    phoneLine.lineColor = phoneColor
                    phoneLine.lineWidth = 20
                    phoneLine.capStyle = MapPolyline.CapStyle.ROUND
                    listLines.add(phoneLine)
                }
                listCoordinates = ArrayList()
                listCoordinates.add(GeoCoordinate(point.latitude, point.longitude, 0.0))
            } else if (i == 0) {
                listCoordinates.add(GeoCoordinate(point.latitude, point.longitude, 0.0))
            }
        }

        if (listCoordinates.size > 1) {
            val line = MapPolyline(GeoPolyline(listCoordinates))
            val point = tripPoints[tripPoints.size - 1]
            val color = SpeedType.getColor(point.speedType)
            line.lineColor = ContextCompat.getColor(requireContext(), color)
            line.lineWidth = 8
            listLines.add(line)
        }
        for (line in listLines) {
            listMapObjects.add(line)
        }
        if (map != null) {
            renderEventsAndRoute()
            if (tripPoints.isNotEmpty()) {
                centerMapByRoute(tripPoints)
            }
        }
    }

    private fun renderEventsAndRoute() {
        map?.removeMapObjects(listMapObjects)
        map?.addMapObjects(listMapObjects)
    }

    private fun centerMapByRoute(tripPoints: List<TripPointData>) {
        val geoCoords: MutableList<GeoCoordinate> = mutableListOf()
        tripPoints.forEach { point ->
            geoCoords.add(GeoCoordinate(point.latitude, point.longitude))
        }
        val geoPolyline = GeoPolyline(geoCoords)
        val boundingBox = geoPolyline.boundingBox
        boundingBox!!.expand(850F, 850F)
        map?.zoomTo(boundingBox, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION)
    }

    /** additional UIs*/
    private fun showConfirmDialog(callback: () -> Unit) {
        showAnswerDialog(
            R.string.are_you_sure,
            onPositive = callback
        )
    }

    private fun showEventDialog(event: TripPointData, alertType: AlertType) {
        claimDialog.show()
        claimDialog.findViewById<ImageView>(R.id.noEventImage)
            .setImageResource(alertType.wrongDialogResId)
        claimDialog.findViewById<TextView>(R.id.noEventText).text =
            resources.getString(alertType.getStringRes())

        val remainingItems =
            AlertType.values().filter { it != AlertType.UNKNOWN && it != alertType }

        claimDialog.findViewById<ImageView>(R.id.firstImage)
            .setImageResource(remainingItems[0].editedDialogResId)
        claimDialog.findViewById<TextView>(R.id.firstText).text =
            resources.getString(remainingItems[0].getStringRes())

        claimDialog.findViewById<ImageView>(R.id.secondImage)
            .setImageResource(remainingItems[1].editedDialogResId)
        claimDialog.findViewById<TextView>(R.id.secondText).text =
            resources.getString(remainingItems[1].getStringRes())

        claimDialog.findViewById<ImageView>(R.id.thirdImage)
            .setImageResource(remainingItems[2].editedDialogResId)
        claimDialog.findViewById<TextView>(R.id.thirdText).text =
            resources.getString(remainingItems[2].getStringRes())

        claimDialog.findViewById<View>(R.id.noEventLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.noEventLayout).setOnClickListener {
            showConfirmDialog {
                claimDialog.dismiss()
                //presenter.sendNotEvent(event, alertType)
            }
        }

        claimDialog.findViewById<View>(R.id.firstLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.firstLayout).setOnClickListener {
            showConfirmDialog {
                changeTripEvent(event, remainingItems[0])
            }

        }

        claimDialog.findViewById<View>(R.id.secondLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.secondLayout).setOnClickListener {
            showConfirmDialog {
                changeTripEvent(event, remainingItems[1])
            }
        }

        claimDialog.findViewById<View>(R.id.thirdLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.thirdLayout).setOnClickListener {
            showConfirmDialog {
                changeTripEvent(event, remainingItems[2])
            }
        }

        claimDialog.findViewById<View>(R.id.cancelButton).setOnClickListener {
            claimDialog.dismiss()
        }
    }

    private fun showError(msg: String) {

        Log.d(TAG, "showError: $msg")
        showMessage(msg)
    }

    private fun showProgress(show: Boolean) {

        binding.tripDetailsLoadingView.setOnClickListener { }
        binding.tripDetailsLoadingView.isVisible = show
    }

    private fun showPreviousArrow(show: Boolean) {

        binding.prevArrow.isVisible = show
    }

    private fun showMapFragment(show: Boolean) {

        if (show) {
            binding.tripDetails.tripDetailsMap.isVisible = true
            binding.tripDetails.tripDetailsMap.alpha = 0f
            binding.tripDetails.tripDetailsMap.animate()
                .setDuration(300)
                .setStartDelay(100)
                .alpha(1f)
                .start()
        } else {
            binding.tripDetails.tripDetailsMap.isVisible = false
        }
    }

    private fun setTranslucentForBottomSheet(show: Boolean) {
        binding.tripDetailsBottomSheet.tripBottomSheet.alpha = if (show) 0.5f else 1f
    }

    private fun showSuccessToast(success: Boolean) {

        val s =
            if (success) resources.getString(R.string.trip_details_thanks) else resources.getString(
                R.string.trip_details_error
            )
        val t = Toast.makeText(context, s, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER, 0, 0)
        val view = LayoutInflater.from(context).inflate(R.layout.wrong_event_toast, null)
        val toastText = view.findViewById<TextView>(R.id.toast_text)
        val toastImage = view.findViewById<ImageView>(R.id.toast_image)
        toastText.text = s
        if (success) toastImage.setImageResource(R.drawable.ic_toast_success)
        else toastImage.setImageResource(R.drawable.ic_toast_error)
        t.view = view
        t.show()
    }

    /** handle trip */
    private fun changeTripTypeTo(tripDetailsData: TripDetailsData, toType: TripData.TripType) {

        val tripId = tripDetailsData.id!!

        showProgress(true)
        tripDetailViewModel.changeTripTypeTo(tripId, toType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    if (it) {
                        isNeedUpdateFeedList = true
                        tripDetailsData.type = toType
                    }
                    bindTripType(tripDetailsData)
                }
                result.onFailure {
                    bindTripType(tripDetailsData)
                }
                showProgress(false)
            }
    }

    private fun changeTripEvent(event: TripPointData, alertType: AlertType) {

        showProgress(true)
        claimDialog.dismiss()
        tripDetailViewModel.changeTripEvent(event, alertType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    event.edited = true
                    val index = eventsList.indexOf(event)
                    eventsList[index].alertType = alertType.type
                    if (index >= 0) {
                        val image = Image()
                        image.setBitmap(
                            resources.drawable(
                                alertType.editedDrawableResId,
                                requireContext()
                            ).toBitmap()
                        )
                        (listMapObjects[listMapObjects.indexOf(eventsMarkersList[index])] as MapMarker).icon =
                            image
                        renderEventsAndRoute()
                        removeOverlay()
                    }
                    showSuccessToast(true)
                }
                result.onFailure {
                    showSuccessToast(false)
                }
                showProgress(false)
            }
    }

    private fun removeOverlay() {
        overlay?.let {
            map?.removeMapOverlay(overlay!!)
            overlay = null
        }
    }

    private fun formatTime(value: Int): String {
        var min = (value / 60).toLong()
        val h = min / 60
        min %= 60
        return requireContext().getString(R.string.common_time_in_hm_format, h, min)
    }

    private fun finish() {
        onBackPressed()
    }
}