package com.telematics.features.account.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.telematics.domain.model.carservice.Vehicle
import com.telematics.features.account.R

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

        fun bind(vehicle: Vehicle) {
            val carClickArea = itemView.findViewById<View>(R.id.carClickArea)
            carClickArea.setOnClickListener(null)
            carClickArea.setOnClickListener {
                clickListener?.onItemClick(vehicle, absoluteAdapterPosition)
            }
            val carLabel = itemView.findViewById<TextView>(R.id.carLabel)
            val carTitle = vehicle.manufacturer ?: "—"
            carLabel.text = carTitle
            itemView.findViewById<View>(R.id.carConnectedStatus).apply {
                isVisible = vehicle.activated
            }
        }
    }

    interface ClickListeners {
        fun onItemClick(vehicle: Vehicle, listItemPosition: Int)
    }
}