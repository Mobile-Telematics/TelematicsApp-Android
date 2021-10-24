package ui.step4_connect_device

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import com.telematics.obd.R

class DeviceConnectedWizardAdapter : PagerAdapter() {

    private var listener: OnSearchClickListener? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return DeviceConnectEnum.values().size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val customPagerEnum = DeviceConnectEnum.values()[position]
        val inflater = LayoutInflater.from(container.context)
        val layout = inflater.inflate(customPagerEnum.pageResId, container, false) as ViewGroup
        container.addView(layout)
        if (position == DeviceConnectEnum.TURN_ON_BLUETOOTH.ordinal) {
            layout.findViewById<Button>(R.id.bluetooth_search_button).setOnClickListener {
                listener?.search()
            }
        }
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun setOnClick(action: OnSearchClickListener) {
        this.listener = action
    }

    interface OnSearchClickListener {
        fun search()
    }
}