package com.telematics.domain.model.leaderboard

import androidx.recyclerview.widget.DiffUtil

data class LeaderboardUserItems(
    val type: LeaderboardType = LeaderboardType.Rate,
    val progress: Double = 0.0,
    val place: Int = -1,
    val progressMax: Int = 100
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LeaderboardUserItems>() {
            override fun areItemsTheSame(
                oldItem: LeaderboardUserItems,
                newItem: LeaderboardUserItems
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: LeaderboardUserItems,
                newItem: LeaderboardUserItems
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}