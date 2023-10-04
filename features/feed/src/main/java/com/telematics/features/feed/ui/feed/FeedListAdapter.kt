package com.telematics.features.feed.ui.feed

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.telematics.data.extentions.color
import com.telematics.data.extentions.drawable
import com.telematics.data.extentions.format
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.tracking.TripData
import com.telematics.feed.R
import com.telematics.feed.databinding.LayoutTripItemBinding
import kotlin.math.roundToInt


class FeedListAdapter(private val formatter: MeasuresFormatter) :
    RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {

    private var dataSet: MutableList<TripData> = mutableListOf()
    private var lastPosition = -1
    private val animationList = mutableListOf<ValueAnimator>()
    private var clickListener: ClickListeners? = null


    fun setOnClickListener(listeners: ClickListeners) {
        this.clickListener = listeners
    }

    fun addData(data: List<TripData>) {

        animationList.forEach {
            it.duration = 1
        }

        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    fun removeItem(itemPosition: Int) {

        dataSet.removeAt(itemPosition)
        notifyItemRemoved(itemPosition)
    }

    fun clearAllData() {

        lastPosition = -1
        dataSet.clear()
        notifyDataSetChanged()
    }

    fun updateItemByPos(newType: TripData.TripType, position: Int) {

        dataSet[position].type = newType
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutTripItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
        holder.binding.eventTripMainBubble.layoutParams.width = screenWidth
        holder.binding.eventTripMainBubble.invalidate()
        holder.binding.eventTripHorizontalScroll.scrollX = 0

        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int = dataSet.size

    private fun setAnimation(view: View, position: Int) {

        val firstFixCount = 6

        if (position > lastPosition && position > firstFixCount) {
            val animation = ValueAnimator.ofFloat(500f, 0f)
            animation.duration = 300
            animation.addUpdateListener {
                val v = it.animatedValue as Float
                view.translationX = v
            }
            animation.start()

            animationList.add(animation)
            lastPosition = position
        }
    }

    inner class ViewHolder(val binding: LayoutTripItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tripItem: TripData) = with(binding) {

            val context = itemView.context

            formatter.getDistanceByKm(tripItem.dist.toDouble()).apply {
                eventTripMileage.text = this.format()
            }

            formatter.getDistanceMeasureValue().apply {
                val distValue = when (formatter.getDistanceMeasureValue()) {
                    DistanceMeasure.KM -> R.string.dashboard_new_km
                    DistanceMeasure.MI -> R.string.dashboard_new_mi
                }
                measureDistText.text = context.getString(distValue)
            }
            val startDate = formatter.parseFullNewDate(tripItem.timeStart!!)
            val endDate = formatter.parseFullNewDate(tripItem.timeEnd!!)
            eventTripDateStart.text = formatter.getDateWithTime(startDate)
            eventTripDateFinish.text = formatter.getDateWithTime(endDate)
            eventTripStartCity.text =
                "|  ".plus(tripItem.cityStart + ", " + tripItem.districtStart) // "125, 5th Really long name Avenue, Pittsburgh, PA"
            eventTripEndCity.text =
                "|  ".plus(tripItem.cityEnd + ", " + tripItem.districtEnd) // "47 Cherry Hill Highway, New York, NY"
            eventTripOverallScore.text = tripItem.rating.roundToInt().toString()

            eventTripOverallScore.setTextColor(
                when (tripItem.rating.roundToInt()) {
                    in 0..40 -> context.resources.color(R.color.colorRedText)
                    in 41..60 -> context.resources.color(R.color.colorOrangeText)
                    in 61..80 -> context.resources.color(R.color.colorYellowText)
                    in 80..100 -> context.resources.color(R.color.colorGreenText)
                    else -> context.resources.color(R.color.colorGreenText)
                }
            )

            eventTripDetailsClickArea.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemClick(
                    tripItem,
                    absoluteAdapterPosition
                )
            }

            itemEventTypeLayout.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemChangeTypeClick(
                    tripItem,
                    absoluteAdapterPosition
                )
            }

            eventTripDelete.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemDelete(
                    tripItem,
                    absoluteAdapterPosition
                )
            }
            eventTripHide.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemHide(
                    tripItem,
                    absoluteAdapterPosition
                )
            }

            // check radio btn
            val updateViews = {
                when (tripItem.type) {
                    TripData.TripType.DRIVER -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_driver)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_driver)
                    }

                    TripData.TripType.PASSENGER -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_passenger)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_passenger)
                    }

                    TripData.TripType.BUS -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_bus)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_bus)
                    }

                    TripData.TripType.MOTORCYCLE -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_motorcycle)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_motorcycle)
                    }

                    TripData.TripType.TRAIN -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_train)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_train)
                    }

                    TripData.TripType.TAXI -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_taxi)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_taxi)
                    }

                    TripData.TripType.BICYCLE -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_bicycle)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_bicycle)
                    }

                    TripData.TripType.OTHER -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_other)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_other)
                    }

                    else -> {
                        itemEventTypeName.text =
                            context.getString(R.string.progress_trip_type_other)
                        itemEventTypeImg.setImageResource(R.drawable.ic_event_trip_bubble_other)
                    }
                }
            }

            updateViews()

            eventTripLabel.text =
                context.getString(R.string.progress_event_trip)
            eventTripLabel.background = itemView.resources.drawable(
                R.drawable.ic_event_trip_label_bg_green,
                context
            )
        }
    }

    interface ClickListeners {
        fun onItemClick(tripData: TripData, listItemPosition: Int)
        fun onItemChangeTypeClick(tripData: TripData, listItemPosition: Int)
        fun onItemDelete(tripData: TripData, listItemPosition: Int)
        fun onItemHide(tripData: TripData, listItemPosition: Int)
    }
}