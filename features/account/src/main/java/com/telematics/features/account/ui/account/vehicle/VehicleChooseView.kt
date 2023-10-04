package com.telematics.features.account.ui.account.vehicle

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentChooseModelVehicleBinding


class VehicleChooseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private val binding =
        FragmentChooseModelVehicleBinding.inflate(LayoutInflater.from(context), this)

    init {
        init(context)
    }

    enum class Type {
        MANUFACTURER, MODEL
    }

    private var currentType: Type? = null
    private var returnAction: OnReturnData? = null

    private lateinit var adapter: Adapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private fun init(context: Context) = with(binding) {

        root.setBackgroundColor(Color.parseColor("#1A000000"))

        val bottomSheet = chooseModelVehicleParent
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    hide()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        adapter = Adapter()
        chooseModelVehicleItemsRV.layoutManager = LinearLayoutManager(context)
        chooseModelVehicleItemsRV.adapter = adapter

        chooseModelVehicleSearchView.queryHint = context.getString(R.string.obd_search)
        chooseModelVehicleSearchView.isIconified = false
        chooseModelVehicleSearchView.clearFocus()
        chooseModelVehicleSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text == null) return false
                adapter.search(text)
                return false
            }
        })

        setOnClickListener {
            isVisible = false
        }
    }

    fun show() = with(binding) {

        chooseModelVehicleItemsRV.smoothScrollToPosition(0)
        chooseModelVehicleSearchView.setQuery("", false)
        chooseModelVehicleSearchView.clearFocus()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        isVisible = true
    }

    fun hide() {

        isVisible = false
    }

    fun setManufacturers(list: List<ManufacturerData>) {

        currentType = Type.MANUFACTURER
        setData(list.map { Data(it.id, it.name) })
    }

    fun setModels(list: List<ModelData>) {
        currentType = Type.MODEL
        setData(list.map { Data(it.id, it.name) })
    }

    fun setOnReturnData(action: OnReturnData) {
        this.returnAction = action
    }

    private fun setData(list: List<Data>) {

        adapter.setList(list)
        adapter.setOnItemClick(object : Adapter.OnItemClick {
            override fun onClick(data: Data) {
                returnAction?.onReturn(data.id, data.name, currentType)
            }
        })
    }


    interface OnReturnData {
        fun onReturn(id: Int, name: String, type: Type?)
    }

    inner class Data(
        val id: Int,
        var name: String
    )

    class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private var dataSet = mutableListOf<Data>()
        private var filteredDataSet = mutableListOf<Data>()
        private var onItemClick: OnItemClick? = null

        fun setList(list: List<Data>) {

            dataSet.clear()
            dataSet.addAll(list)

            filteredDataSet.clear()
            filteredDataSet.addAll(list)

            notifyDataSetChanged()
        }

        fun search(p: String?) {

            val list =
                if (p.isNullOrEmpty()) dataSet else dataSet.filter { it.name.contains(p, true) }

            filteredDataSet.clear()
            filteredDataSet.addAll(list)
            notifyDataSetChanged()
        }

        fun setOnItemClick(action: OnItemClick) {

            this.onItemClick = action
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.vehicles_choose_item_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val view = holder.itemView
            val data = filteredDataSet[position]
            view.findViewById<TextView>(R.id.text).text = data.name
            view.setOnClickListener {
                onItemClick?.onClick(data)
            }
        }

        override fun getItemCount(): Int = filteredDataSet.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
        interface OnItemClick {
            fun onClick(data: Data)
        }
    }
}