package com.telematics.features.account.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.features.account.R
import kotlinx.android.synthetic.main.vehicle_item_view.view.*

class VehicleListAdapter(data: List<Vehicle>) :
    RecyclerView.Adapter<VehicleListAdapter.ViewHolder>() {

    private var dataSet: MutableList<Vehicle> = mutableListOf()

    init {
        dataSet.clear()
        dataSet.addAll(data)
    }

    private var clickListener: ClickListeners? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    fun setOnClickListener(listeners: ClickListeners) {
        this.clickListener = listeners
    }

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(
            vehicle: Vehicle
        ) {
            itemView.carClickArea.setOnClickListener(null)
            itemView.carClickArea.setOnClickListener {
                clickListener?.onItemClick(vehicle, absoluteAdapterPosition)
            }
            val carTitle = vehicle.manufacturer ?: "—"
            itemView.carLabel.text = carTitle
            if (vehicle.activated) {
                itemView.carConnectedStatus.visibility = View.VISIBLE
            } else {
                itemView.carConnectedStatus.visibility = View.GONE
            }
        }
    }

    interface ClickListeners {
        fun onItemClick(vehicle: Vehicle, listItemPosition: Int)
    }
}