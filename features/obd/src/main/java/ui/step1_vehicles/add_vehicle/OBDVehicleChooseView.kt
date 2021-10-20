package ui.step1_vehicles.add_vehicle

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.telematics.domain.model.carservice.ManufacturerData
import com.telematics.domain.model.carservice.ModelData
import com.telematics.obd.R
import kotlinx.android.synthetic.main.fragment_obd_choose_model_vehicle.view.*

class OBDVehicleChooseView(context: Context, attributeSet: AttributeSet?) :
    ConstraintLayout(context, attributeSet) {

    private lateinit var view: View

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

    private fun init(context: Context) {

        val inflater =
            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.fragment_obd_choose_model_vehicle, this)

        val bottomSheet = view.chooseModelVehicleParent
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
        view.chooseModelVehicleItemsRV.layoutManager = LinearLayoutManager(context)
        view.chooseModelVehicleItemsRV.adapter = adapter

        view.chooseModelVehicleSearchView.queryHint = context.getString(R.string.obd_search)
        view.chooseModelVehicleSearchView.isIconified = false
        view.chooseModelVehicleSearchView.clearFocus()
        view.chooseModelVehicleSearchView.setOnQueryTextListener(object :
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

        view.setOnClickListener {
            view.isVisible = false
        }
    }

    fun show() {

        view.chooseModelVehicleItemsRV.smoothScrollToPosition(0)
        view.chooseModelVehicleSearchView.setQuery("", false)
        view.chooseModelVehicleSearchView.clearFocus()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        view.isVisible = true
    }

    fun hide() {

        view.isVisible = false
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