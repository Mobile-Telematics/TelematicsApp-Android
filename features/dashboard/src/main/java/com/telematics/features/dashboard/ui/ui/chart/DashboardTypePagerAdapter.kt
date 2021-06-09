package com.telematics.features.dashboard.ui.ui.chart

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.telematics.dashboard.R
import com.telematics.domain.model.statistics.ScoreType
import com.telematics.domain.model.statistics.ScoreTypeModel
import kotlinx.android.extensions.LayoutContainer


class DashboardTypePagerAdapter(private val data: MutableList<ScoreTypeModel>) :
    RecyclerView.Adapter<DashboardTypePagerAdapter.ViewHolder>() {

    private var addData = mutableListOf<ScoreTypeModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val page = DashboardProgressView(parent.context)
        page.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        return ViewHolder(page)
    }

    fun updateData(data: List<ScoreTypeModel>, scoreData: List<ScoreTypeModel>) {
        this.data.clear()
        this.data.addAll(data)
        addData.clear()
        addData.addAll(scoreData)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data[position]

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(override val containerView: DashboardProgressView) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bind(item: ScoreTypeModel, position: Int) {
            containerView.setProgress(addData[position].score)
            val strType =
                when (item.type) {
                    ScoreType.OVERALL -> containerView.context.getString(R.string.dashboard_new_overall_score)
                    ScoreType.ACCELERATION -> containerView.context.getString(R.string.dashboard_new_acceleration_score)
                    ScoreType.BREAKING -> containerView.context.getString(R.string.dashboard_new_braking_score)
                    ScoreType.PHONE_USAGE -> containerView.context.getString(R.string.dashboard_new_phone_dist_score)
                    ScoreType.SPEEDING -> containerView.context.getString(R.string.dashboard_new_speeding_score)
                    ScoreType.CORNERING -> containerView.context.getString(R.string.dashboard_new_cornering_score)
                }
            containerView.setType(strType)
        }
    }
}