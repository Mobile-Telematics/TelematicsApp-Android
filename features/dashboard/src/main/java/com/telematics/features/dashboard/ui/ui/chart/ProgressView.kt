package com.telematics.features.dashboard.ui.ui.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paintArc: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var oval: RectF
    private lateinit var bitmap: Bitmap
    private lateinit var mCanvas: Canvas
    private val mBitmapPaint =
        Paint(Paint.DITHER_FLAG)
    var progress: Int = 0
        @Synchronized
        set(value) {
            if (value < 0) throw Exception("Progress should be greater than zero")
            if (value == field) return
            field = min(value, 100)
            currentProgress = 0
            if (this::mCanvas.isInitialized) {
                mCanvas.drawColor(Color.TRANSPARENT)
            }
            invalidate()
        }
    private var currentProgress: Int = 0

    private val lineColors = mapOf(
        ColorName.RED to Color.rgb(232, 39, 39),
        ColorName.ORANGE to Color.rgb(234, 103, 21),
        ColorName.YELLOW to Color.rgb(255, 188, 41),
        ColorName.GREEN to Color.rgb(84, 199, 81)
    )

    private val backColors = mapOf(
        ColorName.RED to Color.rgb(249, 200, 200),
        ColorName.ORANGE to Color.rgb(252, 215, 195),
        ColorName.YELLOW to Color.rgb(255, 243, 191),
        ColorName.GREEN to Color.rgb(209, 231, 202)
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oval = RectF(30f, 30f, width - 30f, height.toFloat())
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas) {
        if (currentProgress == 0)
            drawBackGround(canvas)
        if (progress > 0)
            drawLine(canvas)
    }

    fun setStrokeWidth(width: Float) {
        paintArc.strokeWidth = width
        invalidate()
    }


    //region Progress drawing region

    private fun drawLine(canvas: Canvas) {
        paintArc.color = getLineColor()
        if (currentProgress > progress) currentProgress = progress
        when (currentProgress) {
            in 0..62 -> {
                drawFirstLinePart(currentProgress, canvas)
            }
            in 63..71 -> {
                val forDraw = 9 - (71 - currentProgress)
                drawSecondLinePart(forDraw, canvas)
            }
            in 72..80 -> {
                val forDraw = 9 - (80 - currentProgress)
                drawThirdLinePart(forDraw, canvas)
            }
            in 81..89 -> {
                val forDraw = 9 - (89 - currentProgress)
                drawForthLinePart(forDraw, canvas)
            }
            else -> {
                val forDraw = 9 - (98 - currentProgress)
                drawFifthLinePart(forDraw, canvas)
            }
        }
        currentProgress++
        if (currentProgress <= progress)
            postInvalidateOnAnimation()

    }

    private fun drawFifthLinePart(forDraw: Int, canvas: Canvas) {
        val angle = 215f * (forDraw / 100f)
        paintArc.strokeCap = Paint.Cap.BUTT

        if (forDraw in 11..11) {
            mCanvas.drawArc(oval, 359F, 17F, false, paintArc)
            paintArc.strokeCap = Paint.Cap.ROUND
            mCanvas.drawArc(oval, 375F, 9F, false, paintArc)
        } else {

            mCanvas.drawArc(oval, 359F, angle, false, paintArc)
            paintArc.strokeCap = Paint.Cap.ROUND
            mCanvas.drawArc(oval, 359.8F + angle, 0.1f, false, paintArc)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
        canvas.save()
        canvas.restore()
    }

    private fun drawForthLinePart(forDraw: Int, canvas: Canvas) {
        val angle = 215f * (forDraw / 100f)
        paintArc.strokeCap = Paint.Cap.BUTT

        if (forDraw in 8..9) {
            mCanvas.drawArc(oval, 338F, 20F, false, paintArc)
        } else {

            mCanvas.drawArc(oval, 338F, angle, false, paintArc)
            paintArc.strokeCap = Paint.Cap.ROUND
            mCanvas.drawArc(oval, 338.65F + angle, 0.1f, false, paintArc)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
        canvas.save()
        canvas.restore()
    }

    private fun drawThirdLinePart(forDraw: Int, canvas: Canvas) {
        val angle = 215f * (forDraw / 100f)
        paintArc.strokeCap = Paint.Cap.BUTT

        if (forDraw in 8..9) {
            mCanvas.drawArc(oval, 317F, 20F, false, paintArc)
        } else {

            mCanvas.drawArc(oval, 317F, angle, false, paintArc)
            paintArc.strokeCap = Paint.Cap.ROUND
            mCanvas.drawArc(oval, 317.65F + angle, 0.1f, false, paintArc)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
        canvas.save()
        canvas.restore()
    }

    private fun drawSecondLinePart(forDraw: Int, canvas: Canvas) {
        val angle = 215f * (forDraw / 100f)
        paintArc.strokeCap = Paint.Cap.BUTT

        if (forDraw in 8..9) {
            mCanvas.drawArc(oval, 296F, 20F, false, paintArc)
        } else {

            mCanvas.drawArc(oval, 296F, angle, false, paintArc)

            paintArc.strokeCap = Paint.Cap.ROUND
            mCanvas.drawArc(oval, 296.65F + angle, 0.1f, false, paintArc)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
        canvas.save()
        canvas.restore()
    }

    private fun drawFirstLinePart(forDraw: Int, canvas: Canvas) {
        val angle = 215f * (forDraw / 100f)
        paintArc.strokeCap = Paint.Cap.ROUND
        if (forDraw == 62) {
            mCanvas.drawArc(oval, 155F, 137f, false, paintArc)
            paintArc.strokeCap = Paint.Cap.BUTT
            mCanvas.drawArc(oval, 292F, 3f, false, paintArc)
        } else {
            mCanvas.drawArc(oval, 155F, angle + 5, false, paintArc)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
        canvas.save()
        canvas.restore()
    }

    private fun getLineColor(): Int = when (progress) {
        in 0..62 -> lineColors[ ColorName.RED]!!
        in 63..71 -> lineColors[ ColorName.ORANGE]!!
        in 72..80 -> lineColors[ ColorName.ORANGE]!!
        in 81..89 -> lineColors[ ColorName.YELLOW]!!
        else -> lineColors[ ColorName.GREEN]!!
    }
    //endregion

    //region Background drawing methods

    private fun drawBackGround(canvas: Canvas) {
        drawFirstPart(canvas)
        drawSecondPart(canvas)
        drawThirdPart(canvas)
        drawFourthPart(canvas)
        drawFifthPart(canvas)
    }

    private fun drawFirstPart(canvas: Canvas) {
        paintArc.strokeCap = Paint.Cap.ROUND
        paintArc.color = backColors[ ColorName.RED]!!
        mCanvas.drawArc(oval, 155F, 137F, false, paintArc)

        paintArc.strokeCap = Paint.Cap.BUTT
        mCanvas.drawArc(oval, 292F, 3F, false, paintArc)
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
    }

    private fun drawSecondPart(canvas: Canvas) {
        paintArc.strokeCap = Paint.Cap.BUTT
        paintArc.color = backColors[ ColorName.RED]!!
        mCanvas.drawArc(oval, 296F, 20F, false, paintArc)
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
    }

    private fun drawThirdPart(canvas: Canvas) {
        paintArc.strokeCap = Paint.Cap.BUTT
        paintArc.color = backColors[ ColorName.ORANGE]!!
        mCanvas.drawArc(oval, 317F, 20F, false, paintArc)
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
    }

    private fun drawFourthPart(canvas: Canvas) {
        paintArc.strokeCap = Paint.Cap.BUTT
        paintArc.color = backColors[ ColorName.YELLOW]!!
        mCanvas.drawArc(oval, 338F, 20F, false, paintArc)
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
    }

    private fun drawFifthPart(canvas: Canvas) {
        paintArc.strokeCap = Paint.Cap.BUTT
        paintArc.color = backColors[ ColorName.GREEN]!!
        mCanvas.drawArc(oval, 359F, 17F, false, paintArc)

        paintArc.strokeCap = Paint.Cap.ROUND
        mCanvas.drawArc(oval, 375F, 9F, false, paintArc)
        canvas.drawBitmap(bitmap, 0f, 0f, mBitmapPaint)
    }
    //endregion


    init {
        paintArc.strokeWidth = 20f
        paintArc.style = Paint.Style.STROKE
    }

    enum class ColorName {
        RED,
        ORANGE,
        YELLOW,
        GREEN
    }
}