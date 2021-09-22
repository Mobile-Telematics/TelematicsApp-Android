package com.telematics.features.reward.streaks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.telematics.domain.model.reward.Streak
import com.telematics.domain.model.reward.StreakCarType
import com.telematics.reward.R

class StreaksRecyclerAdapter(private val streaksList: List<Streak> = listOf()) :
    RecyclerView.Adapter<StreaksRecyclerAdapter.StreaksCardViewHolder>() {

    class StreaksCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var badgeImage: ImageView = itemView.findViewById(R.id.card_category_icon)
        private var title: TextView = itemView.findViewById(R.id.card_category_title)
        private var currentValues: TextView =
            itemView.findViewById(R.id.card_current_distance_value)
        private var bestValues: TextView = itemView.findViewById(R.id.card_best_distance_value)
        private var currentTripsCount: TextView =
            itemView.findViewById(R.id.card_current_trips_value)
        private var bestTripsCount: TextView = itemView.findViewById(R.id.card_best_trips_value)
        private var currentDate: TextView =
            itemView.findViewById(R.id.card_current_date_value)
        private var bestDate: TextView = itemView.findViewById(R.id.card_best_date_value)

        fun bind(streak: Streak) {
            when (streak.cardType) {
                StreakCarType.Acceleration -> {
                    badgeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_acceleration_arrow
                        )
                    )
                    title.text = itemView.context.getString(R.string.acceleration_card_title)
                }
                StreakCarType.Braking -> {
                    badgeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_leaderboard_deceleration
                        )
                    )
                    title.text = itemView.context.getString(R.string.braking_card_title)
                }
                StreakCarType.Cornering -> {
                    badgeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_leaderboard_cornering
                        )
                    )
                    title.text = itemView.context.getString(R.string.cornering_card_title)
                }
                StreakCarType.Distraction -> {
                    badgeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_decelerations
                        )
                    )
                    title.text = itemView.context.getString(R.string.distraction_card_title)
                }
                StreakCarType.Speeding -> {
                    badgeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_leaderboard_acceleration
                        )
                    )
                    title.text = itemView.context.getString(R.string.speeding_card_title)
                }
                StreakCarType.PhoneUsage -> {
                    badgeImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_leaderboard_phone
                        )
                    )
                    title.text = itemView.context.getString(R.string.phone_card_title)
                }
            }
            currentValues.text = streak.currentDistance
            bestValues.text = streak.bestDistance
            currentTripsCount.text = streak.currentTrips
            bestTripsCount.text = streak.bestTrips
            currentDate.text = streak.currentDate
                ?: itemView.context.getString(R.string.current_duration_placeholder)
            bestDate.text = streak.bestDate
                ?: itemView.context.getString(R.string.best_duration_placeholder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreaksCardViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.streaks_list_item, parent, false)
        return StreaksCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: StreaksCardViewHolder, position: Int) {
        holder.bind(streaksList[position])
    }

    override fun getItemCount(): Int {
        return streaksList.count()
    }

}