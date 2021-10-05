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
import kotlinx.android.synthetic.main.layout_trip_item.view.*
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_trip_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])

        val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
        holder.itemView.eventTripMainBubble.layoutParams.width = screenWidth
        holder.itemView.eventTripMainBubble.invalidate()
        holder.itemView.eventTripHorizontalScroll.scrollX = 0

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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(tripItem: TripData) {

            val context = itemView.context

            formatter.getDistanceByKm(tripItem.dist.toDouble()).apply {
                itemView.eventTripMileage.text = this.format()
            }

            formatter.getDistanceMeasureValue().apply {
                val distValue = when (formatter.getDistanceMeasureValue()) {
                    DistanceMeasure.KM -> R.string.dashboard_new_km
                    DistanceMeasure.MI -> R.string.dashboard_new_mi
                }
                itemView.measure_dist_text.text = context.getString(distValue)
            }
            val startDate = formatter.parseFullNewDate(tripItem.timeStart!!)
            val endDate = formatter.parseFullNewDate(tripItem.timeEnd!!)
            itemView.eventTripDateStart.text = formatter.getDateWithTime(startDate)
            itemView.eventTripDateFinish.text = formatter.getDateWithTime(endDate)
            itemView.eventTripStartCity.text =
                "|  ".plus(tripItem.cityStart + ", " + tripItem.districtStart) // "125, 5th Really long name Avenue, Pittsburgh, PA"
            itemView.eventTripEndCity.text =
                "|  ".plus(tripItem.cityEnd + ", " + tripItem.districtEnd) // "47 Cherry Hill Highway, New York, NY"
            itemView.eventTripOverallScore.text = tripItem.rating.roundToInt().toString()

            itemView.eventTripOverallScore.setTextColor(
                when (tripItem.rating.roundToInt()) {
                    in 0..40 -> context.resources.color(R.color.colorRedText)
                    in 41..60 -> context.resources.color(R.color.colorOrangeText)
                    in 61..80 -> context.resources.color(R.color.colorYellowText)
                    in 80..100 -> context.resources.color(R.color.colorGreenText)
                    else -> context.resources.color(R.color.colorGreenText)
                }
            )

            itemView.eventTripDetailsClickArea.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemClick(
                    tripItem,
                    this.absoluteAdapterPosition
                )
            }

            itemView.item_event_type_layout.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemChangeTypeClick(
                    tripItem,
                    this.absoluteAdapterPosition
                )
            }

            itemView.eventTripDelete.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemDelete(
                    tripItem,
                    this.absoluteAdapterPosition
                )
            }
            itemView.eventTripHide.setOnClickListener {
                this@FeedListAdapter.clickListener?.onItemHide(
                    tripItem,
                    this.absoluteAdapterPosition
                )
            }

            // check radio btn
            val updateViews = {
                when (tripItem.type) {
                    TripData.TripType.DRIVER -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_driver)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_driver)
                    }
                    TripData.TripType.PASSENGER -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_passenger)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_passenger)
                    }
                    TripData.TripType.BUS -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_bus)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_bus)
                    }
                    TripData.TripType.MOTORCYCLE -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_motorcycle)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_motorcycle)
                    }
                    TripData.TripType.TRAIN -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_train)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_train)
                    }
                    TripData.TripType.TAXI -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_taxi)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_taxi)
                    }
                    TripData.TripType.BICYCLE -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_bicycle)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_bicycle)
                    }
                    TripData.TripType.OTHER -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_other)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_other)
                    }
                    else -> {
                        itemView.item_event_type_name.text =
                            context.getString(R.string.progress_trip_type_other)
                        itemView.item_event_type_img.setImageResource(R.drawable.ic_event_trip_bubble_other)
                    }
                }
            }

            updateViews()

            itemView.eventTripLabel.text =
                itemView.resources.getString(R.string.progress_event_trip)
            itemView.eventTripLabel.background = itemView.resources.drawable(
                R.drawable.ic_event_trip_label_bg_green,
                itemView.context
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