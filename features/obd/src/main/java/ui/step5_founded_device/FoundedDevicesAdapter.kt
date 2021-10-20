package ui.step5_founded_device

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.telematics.data.extentions.drawable
import com.telematics.domain.model.tracking.ElmDevice
import com.telematics.obd.R

class FoundedDevicesAdapter : RecyclerView.Adapter<FoundedDevicesAdapter.ViewHolder>() {

    private val data = mutableListOf<ElmDevice>()
    private var selected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.elm_device_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    fun setData(devices: List<ElmDevice>) {
        data.clear()
        data.addAll(devices)
        notifyDataSetChanged()
    }

    fun getSelected(): ElmDevice = data[selected]

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ElmDevice, position: Int) {

            val containerView = itemView
            containerView.setOnClickListener(null)
            containerView.setOnClickListener {
                if (selected != position) {
                    selected = position
                    this@FoundedDevicesAdapter.notifyDataSetChanged()
                }
            }
            val elmDeviceText = itemView.findViewById<TextView>(R.id.elm_device_text)
            elmDeviceText.text = item.deviceName ?: "ELM Device"
            if (position == selected) {
                containerView.background = containerView.resources.drawable(
                    R.drawable.elm_selected_background,
                    containerView.context
                )
            } else containerView.background = null
        }
    }
}