package ui.step2_wizard

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.telematics.obd.R

class ObdWizardPagerAdapter : PagerAdapter() {

    private var onClick: OnClick? = null

    override fun getCount(): Int {
        return ConnectVehicleEnum.values().size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val customPagerEnum = ConnectVehicleEnum.values()[position]
        val inflater = LayoutInflater.from(container.context)
        val layout = inflater.inflate(customPagerEnum.layout, container, false) as ViewGroup
        container.addView(layout)
        if (position == ConnectVehicleEnum.PHOTO_PHOTO_OF_ODOMETR.ordinal) {
            layout.findViewById<View>(R.id.openCameraButton).setOnClickListener {
                onClick?.onClickNext()
            }
        }
        if (position == ConnectVehicleEnum.INSTALL_YOUR_DEVICE.ordinal) {
            layout.findViewById<TextView>(R.id.can_not_find_can_text).setOnClickListener {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(container.context.getString(R.string.obd_help_link))
                )
                container.context.startActivity(browserIntent)
            }
        }
        return layout
    }

    fun setOnClick(action: OnClick) {
        this.onClick = action
    }

    interface OnClick {
        fun onClickNext() = run { }
    }
}