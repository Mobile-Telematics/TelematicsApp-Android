package com.telematics.features.leaderboard.ui.leaderboard_details.page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telematics.data.extentions.drawable
import com.telematics.data.extentions.format
import com.telematics.domain.model.leaderboard.LeaderboardMemberData
import com.telematics.domain.model.leaderboard.LeaderboardType
import com.telematics.leaderboard.R
import kotlinx.android.synthetic.main.layout_item_leaderboard.view.*

class LeaderboardDetailsPageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<LeaderboardMemberData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.ITEM.ordinal -> {
                LeaderboardMemberViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_leaderboard, parent, false)
                )
            }
            else -> {
                LeaderboardMemberViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_leaderboard_placeholder, parent, false)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ItemType.ITEM.ordinal) (holder as LeaderboardMemberViewHolder).bind(
            items[position]
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].rank == -1) ItemType.PLACEHOLDER.ordinal else ItemType.ITEM.ordinal
    }


    inner class LeaderboardMemberViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var item: LeaderboardMemberData

        fun bind(lm: LeaderboardMemberData) {
            this.item = lm
            Glide.with(view.context)
                .load(lm.photoUrl)
                .placeholder(R.drawable.ic_no_avatar_white)
                .into(view.leaderboardUserIcon)
            view.leaderboardPosition.text = lm.rank.toString()

            if (lm.firstName.isNullOrEmpty() && lm.familyName.isNullOrEmpty())
                view.leaderboardNickName.text = lm.nickname
            else
                view.leaderboardNickName.text = lm.firstName + " " + lm.familyName

            if (lm.type == LeaderboardType.Trips)
                view.leaderboardScore.text = lm.value.format("0.#")
            else
                view.leaderboardScore.text = lm.value.format()

            if (lm.isCurrentUser) {
                view.leaderbordItem.background = view.resources.drawable(
                    R.drawable.gradient_leadboard_current_user,
                    view.context
                )
            } else {
                view.leaderbordItem.background = null
            }
        }
    }

    fun setData(list: List<LeaderboardMemberData>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    private enum class ItemType {
        ITEM, PLACEHOLDER
    }

}