package com.telematics.features.leaderboard.model

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.telematics.leaderboard.R
import kotlinx.android.synthetic.main.leaderboard_user_value_item.view.*
import kotlin.math.roundToInt

class LeaderboardPropertyProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        val mInflater =
            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mInflater.inflate(R.layout.leaderboard_user_value_item, this, true)
        seekBar.setOnTouchListener { _, _ -> true }
        topView.setOnClickListener { this.performClick() }
    }

    fun setTextRes(@StringRes res: Int) {
        propertyHeader.text = context.getString(res)
    }

    fun setImageRes(@DrawableRes res: Int) {
        propertyIcon.setImageResource(res)
    }

    fun setProgressMax(max: Int = 100) {
        seekBar.max = max * 10
    }

    fun setProgress(progress: Double) {
        if (progress.roundToInt() == seekBar.progress) return
        seekBar.progress = 0
        val animator = ObjectAnimator.ofInt(seekBar, "progress", progress.roundToInt() * 10)
            .setDuration(900)

        animator.start()
    }

    fun highlight() {
        // hided icon
        val h = propertyIcon.layoutParams.height
        val iconParams = LayoutParams(
            0,
            h
        )
        propertyIcon.layoutParams = iconParams

        // changed constraint of seekbar and set start margin for it
        val set = ConstraintSet()
        set.clone(container)
        set.connect(R.id.seekBar, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(R.id.seekBar, ConstraintSet.END, R.id.openArrow, ConstraintSet.START)
        set.setMargin(
            R.id.seekBar,
            ConstraintSet.START,
            resources.getDimensionPixelOffset(R.dimen.leaderboard_user_item_margin_start)
        )
        set.applyTo(container)

        // changed typeface and text size of type text
        propertyHeader.setTypeface(propertyPlace.typeface, Typeface.BOLD)
        propertyHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.leaderboard_user_item_type_text_big).toFloat()
        )

        // changed text size of place
        propertyPlace.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.leaderboard_user_item_place_text_big).toFloat()
        )

        // made parent hwight bigger and set margins for it
        val w = container.layoutParams.width
        val parentParams = LayoutParams(
            w,
            resources.getDimensionPixelOffset(R.dimen.leaderboard_user_item_big_size)
        )
        parentParams.setMargins(
            resources.getDimensionPixelOffset(R.dimen.leaderboard_user_item_margin_start),
            0,
            resources.getDimensionPixelOffset(R.dimen.leaderboard_user_item_margin_start),
            0
        )
        container.layoutParams = parentParams
    }

    @SuppressLint("SetTextI18n")
    fun setPlace(place: Int) {
        ValueAnimator.ofInt(0, place).apply {
            duration = 900
            addUpdateListener {
                propertyPlace?.text = "#${it.animatedValue}"
            }
        }.start()

    }

    fun setClickListener(listener: OnClickListener?) {
        this.setOnClickListener(listener)
    }


}