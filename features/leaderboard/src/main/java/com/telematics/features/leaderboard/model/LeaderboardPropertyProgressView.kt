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
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.telematics.leaderboard.R
import com.telematics.leaderboard.databinding.LeaderboardUserValueItemBinding
import kotlin.math.roundToInt

class LeaderboardPropertyProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LeaderboardUserValueItemBinding.inflate(LayoutInflater.from(context), this)

    init {
        with(binding) {
            seekBar.setOnTouchListener { _, _ -> true }
            topView.setOnClickListener { performClick() }

            root.background =
                AppCompatResources.getDrawable(context, R.drawable.ic_wizard_back_bubble_smallest)
        }
    }

    fun setTextRes(@StringRes res: Int) = with(binding) {
        propertyHeader.text = context.getString(res)
    }

    fun setImageRes(@DrawableRes res: Int) = with(binding) {
        propertyIcon.setImageResource(res)
    }

    fun setProgressMax(max: Int = 100) = with(binding) {
        seekBar.max = max * 10
    }

    fun setProgress(progress: Double) = with(binding) {
        if (progress.roundToInt() == seekBar.progress) return
        seekBar.progress = 0
        val animator = ObjectAnimator.ofInt(seekBar, "progress", progress.roundToInt() * 10)
            .setDuration(900)

        animator.start()
    }

    fun highlight() = with(binding) {
        // hided icon
        val h = propertyIcon.layoutParams.height
        val iconParams = LayoutParams(
            0,
            h
        )
        propertyIcon.layoutParams = iconParams

        // changed constraint of seekbar and set start margin for it
        val set = ConstraintSet()
        set.clone(this@LeaderboardPropertyProgressView)
        set.connect(R.id.seekBar, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(R.id.seekBar, ConstraintSet.END, R.id.openArrow, ConstraintSet.START)
        set.setMargin(
            R.id.seekBar,
            ConstraintSet.START,
            resources.getDimensionPixelOffset(R.dimen.leaderboard_user_item_margin_start)
        )
        set.applyTo(this@LeaderboardPropertyProgressView)

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
        val w = layoutParams.width
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
        layoutParams = parentParams
    }

    @SuppressLint("SetTextI18n")
    fun setPlace(place: Int) = with(binding) {
        ValueAnimator.ofInt(0, place).apply {
            duration = 900
            addUpdateListener {
                propertyPlace.text = "#${it.animatedValue}"
            }
        }.start()

    }

    fun setClickListener(listener: OnClickListener?) {
        this.setOnClickListener(listener)
    }


}