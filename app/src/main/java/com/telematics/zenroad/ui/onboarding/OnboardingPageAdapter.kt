package com.telematics.zenroad.ui.onboarding

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.telematics.zenroad.R

class OnboardingPageAdapter : RecyclerView.Adapter<OnboardingPageAdapter.ViewHolder>() {

    data class Data(
        @StringRes val title: Int,
        @StringRes val text: Int,
        @DrawableRes val image: Int
    )

    private var listener: Listener? = null
    private var data: List<Data> = emptyList()

    fun setOnClick(listener: Listener) {
        this.listener = listener
    }

    fun setData(data: List<Data>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.onboarding_page_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val view = holder.itemView

        view.findViewById<TextView>(R.id.onboardingItemTitle).setText(data[position].title)
        view.findViewById<TextView>(R.id.onboardingItemText).apply {
            setText(data[position].text)
            movementMethod = LinkMovementMethod.getInstance()
        }
        view.findViewById<ImageView>(R.id.onboardingItemImage)
            .setImageResource(data[position].image)

        if (position >= itemCount - 1) {
            listener?.onLastPageShowing()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun onLastPageShowing() = Unit
    }
}