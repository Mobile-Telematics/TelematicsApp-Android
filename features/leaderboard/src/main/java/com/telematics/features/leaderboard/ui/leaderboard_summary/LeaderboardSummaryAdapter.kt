package com.telematics.features.leaderboard.ui.leaderboard_summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.telematics.data.mappers.getIconRes
import com.telematics.data.mappers.getStringRes
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.domain.model.leaderboard.LeaderboardUserItems
import com.telematics.features.leaderboard.model.LeaderboardPropertyProgressView
import com.telematics.leaderboard.R
import kotlinx.android.extensions.LayoutContainer

class LeaderboardSummaryAdapter(private val listener: ClickListener?) :
    ListAdapter<LeaderboardUserItems, RecyclerView.ViewHolder>(LeaderboardUserItems.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = when (viewType) {
            ItemType.ITEM.ordinal -> LeaderboardPropertyProgressView(parent.context).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
            }
            else -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.leaderboard_user_placeholder_item, parent, false)
            }
        }
        return LeaderboardItemViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).place == -1) ItemType.PLACEHOLDER.ordinal else ItemType.ITEM.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LeaderboardItemViewHolder).bind(getItem(position), getItemViewType(position))
    }

    inner class LeaderboardItemViewHolder(override val containerView: View) : LayoutContainer,
        RecyclerView.ViewHolder(containerView) {

        fun bind(item: LeaderboardUserItems, type: Int) {
            containerView.setOnClickListener(null)
            when (type) {
                ItemType.ITEM.ordinal -> {
                    (containerView as LeaderboardPropertyProgressView).apply {
                        if (item.type == LeaderboardType.Rate) {
                            highlight()
                        }
                        setProgressMax(item.progressMax)
                        setProgress(item.progress)
                        setPlace(item.place)
                        setImageRes(item.type.getIconRes())
                        setTextRes(item.type.getStringRes())
                        setClickListener {
                            listener?.onClick(item.type)
                        }
                    }
                }
                ItemType.PLACEHOLDER.ordinal -> {

                }
            }
        }

    }

    interface ClickListener {
        fun onClick(type: LeaderboardType)
    }

    internal enum class ItemType {
        ITEM, PLACEHOLDER
    }

}