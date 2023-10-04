package com.telematics.features.leaderboard.ui.leaderboard_details.page

import android.annotation.SuppressLint
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
import com.telematics.leaderboard.databinding.LayoutItemLeaderboardBinding

class LeaderboardDetailsPageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<LeaderboardMemberData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.ITEM.ordinal -> {
                LeaderboardMemberViewHolder(
                    LayoutItemLeaderboardBinding
                        .inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                )
            }

            else -> {
                LeaderboardPlaceholderViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(
                            R.layout.layout_item_leaderboard_placeholder,
                            parent,
                            false
                        )
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


    inner class LeaderboardMemberViewHolder(val binding: LayoutItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var item: LeaderboardMemberData

        @SuppressLint("SetTextI18n")
        fun bind(lm: LeaderboardMemberData) = with(binding) {
            item = lm
            Glide.with(root.context)
                .load(lm.photoUrl)
                .placeholder(R.drawable.ic_no_avatar_white)
                .into(leaderboardUserIcon)
            leaderboardPosition.text = lm.rank.toString()

            if (lm.firstName.isNullOrEmpty() && lm.familyName.isNullOrEmpty())
                leaderboardNickName.text = lm.nickname
            else
                leaderboardNickName.text = "${lm.firstName} ${lm.familyName}"

            if (lm.type == LeaderboardType.Trips)
                leaderboardScore.text = lm.value.format("0.#")
            else
                leaderboardScore.text = lm.value.format()

            if (lm.isCurrentUser) {
                leaderbordItem.background = root.resources.drawable(
                    R.drawable.gradient_leadboard_current_user,
                    root.context
                )
            } else {
                leaderbordItem.background = null
            }
        }
    }

    inner class LeaderboardPlaceholderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    fun setData(list: List<LeaderboardMemberData>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    private enum class ItemType {
        ITEM, PLACEHOLDER
    }

}