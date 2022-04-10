package com.lgtm.default_Android_Project_Template.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.lgtm.default_Android_Project_Template.R
import com.lgtm.default_Android_Project_Template.utils.toPx

class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0
) : View(context, attrs, defStyleInt) {

    private val circlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val scalePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    val startAngle: Float = 240f

    val endAngle: Float = 120f

    val maximumSpeed: Int = 220


    init {
        setBackgroundColor(resources.getColor(R.color.purple_200))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val measuredSide = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(measuredSide, measuredSide)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawCircle(canvas)
        drawScale(canvas)

    }

    private fun drawCircle(canvas: Canvas) {
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        val radius = (measuredWidth.coerceAtMost(measuredHeight)) / 2f - 16.toPx(context)

        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    private fun drawScale(canvas: Canvas) {
        val totalAngle = (endAngle - startAngle + 360) % 360

        var scaleLength: Float?
        canvas.save()
        //0.. 59 for [0,59]

        canvas.rotate(startAngle, measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat())
        for (speed in 0..maximumSpeed step 10) {
            if (speed % 20 == 0) {
                scalePaint.strokeWidth = 5f
                scaleLength = 20f
            } else {
                scalePaint.strokeWidth = 3f
                scaleLength = 10f
            }
            canvas.drawLine(measuredWidth / 2.toFloat(), 5f, measuredWidth / 2.toFloat(), (5 + scaleLength), scalePaint)
            canvas.rotate((totalAngle / (maximumSpeed / 10)), measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat())
        }
        //Restore the original state
        canvas.restore()
    }


}
//
//open class PointerSpeedometer @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0,
//) : Speedometer(context, attrs, defStyleAttr) {
//
//    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//
//    private var speedometerColor = 0xFFEEEEEE.toInt()
//
//    /**
//     * change the color of the center circle.
//     */
//    var centerCircleColor: Int
//        get() = circlePaint.color
//        set(centerCircleColor) {
//            circlePaint.color = centerCircleColor
//            if (isAttachedToWindow)
//                invalidate()
//        }
//
//    /**
//     * change the width of the center circle.
//     */
//    var centerCircleRadius = dpTOpx(12f)
//        set(centerCircleRadius) {
//            field = centerCircleRadius
//            if (isAttachedToWindow)
//                invalidate()
//        }
//
//    init {
//        init()
//        initAttributeSet(context, attrs)
//    }
//
//    private fun init() {
//        speedometerPaint.style = Paint.Style.STROKE
//        speedometerPaint.strokeCap = Paint.Cap.ROUND
//        circlePaint.color = 0xFFFFFFFF.toInt()
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
//        super.onSizeChanged(w, h, oldW, oldH)
//
//        val risk = speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat()
//        speedometerRect.set(risk, risk, size - risk, size - risk)
//
//        updateRadial()
//        updateBackgroundBitmap()
//    }
//
//    private fun initDraw() {
//        speedometerPaint.strokeWidth = speedometerWidth
//        speedometerPaint.shader = updateSweep()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        initDraw()
//
//        val roundAngle = getRoundAngle(speedometerWidth, speedometerRect.width())
//        canvas.drawArc(speedometerRect, getStartDegree() + roundAngle
//            , (getEndDegree() - getStartDegree()) - roundAngle * 2f, false, speedometerPaint)
//
//        if (withPointer) {
//            canvas.save()
//            canvas.rotate(90 + degree, size * .5f, size * .5f)
//            canvas.drawCircle(size * .5f, speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat(), speedometerWidth * .5f + dpTOpx(8f), pointerBackPaint)
//            canvas.drawCircle(size * .5f, speedometerWidth * .5f + dpTOpx(8f) + padding.toFloat(), speedometerWidth * .5f + dpTOpx(1f), pointerPaint)
//            canvas.restore()
//        }
//
//        drawSpeedUnitText(canvas)
//        drawIndicator(canvas)
//
//        val c = centerCircleColor
//        circlePaint.color = Color.argb((Color.alpha(c) * .5f).toInt(), Color.red(c), Color.green(c), Color.blue(c))
//        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius + dpTOpx(6f), circlePaint)
//        circlePaint.color = c
//        canvas.drawCircle(size * .5f, size * .5f, centerCircleRadius, circlePaint)
//
//        drawNotes(canvas)
//    }
//
//    private fun updateSweep(): SweepGradient {
//        val startColor = Color.argb(150, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
//        val color2 = Color.argb(220, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
//        val color3 = Color.argb(70, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
//        val endColor = Color.argb(15, Color.red(speedometerColor), Color.green(speedometerColor), Color.blue(speedometerColor))
//        val position = getOffsetSpeed() * (getEndDegree() - getStartDegree()) / 360f
//        val sweepGradient = SweepGradient(size * .5f, size * .5f, intArrayOf(startColor, color2, speedometerColor, color3, endColor, startColor), floatArrayOf(0f, position * .5f, position, position, .99f, 1f))
//        val matrix = Matrix()
//        matrix.postRotate(getStartDegree().toFloat(), size * .5f, size * .5f)
//        sweepGradient.setLocalMatrix(matrix)
//        return sweepGradient
//    }
//
//}