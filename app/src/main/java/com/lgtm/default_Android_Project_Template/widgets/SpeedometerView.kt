package com.lgtm.default_Android_Project_Template.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.lgtm.default_Android_Project_Template.R
import com.lgtm.default_Android_Project_Template.utils.toPx
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0
) : View(context, attrs, defStyleInt) {

    private val circlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.bluegray)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val scalePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val scaleTextPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textSize = 16.toPx(context).toFloat()
    }

    private val speedHandPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.red)
        strokeWidth = 11f
    }

    private val currentSpeedPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        textAlign = Paint.Align.CENTER
    }

    private val startDegree: Float = 220f

    private val endDegree: Float = 140f

    private val maxSpeed: Int = 220

    private var lastSpeed: Float = 0f

    var currentSpeed: Float = 0f
        set(value) {
            if (value > 0f) {
                lastSpeed = field
                field = value
                invalidate()
            }
        }

    init {
        setBackgroundColor(resources.getColor(R.color.darknavy))
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
        drawScaleSpeed(canvas)
        drawSpeedHand(canvas)

        drawCurrentSpeedText(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        val radius = (measuredWidth.coerceAtMost(measuredHeight)) / 2f - 16.toPx(context)

        canvas.drawCircle(centerX, centerY, radius, circlePaint)
    }

    private fun drawScale(canvas: Canvas) {
        val totalAngle = (endDegree - startDegree + 360) % 360
        var scaleLength: Float

        canvas.save()
        canvas.rotate(startDegree, measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat())
        for (speed in 0..maxSpeed step 10) {
            if (speed % 20 == 0) {
                scalePaint.strokeWidth = 10f
                scaleLength = 60f
            } else {
                scalePaint.strokeWidth = 4f
                scaleLength = 35f
            }
            canvas.drawLine(measuredWidth / 2.toFloat(), 50f, measuredWidth / 2.toFloat(), (50 + scaleLength), scalePaint)
            canvas.rotate((totalAngle / (maxSpeed / 10)), measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat())
        }
        canvas.restore()
    }

    private fun drawScaleSpeed(canvas: Canvas) {
        val textR = (measuredWidth / 2 - 160).toFloat()

        val totalDegree = (endDegree - startDegree + 360) % 360
        val stepDegree = totalDegree / (maxSpeed / 20)

        var currentDegree = startDegree
        for (speed in 0..maxSpeed step 20) {
            val speedText = speed.toString()

            val rect = Rect()
            scaleTextPaint.getTextBounds(speedText, 0, speedText.length, rect)

            val startX = (measuredWidth / 2 + textR * sin(Math.PI / 180 * currentDegree) - rect.width() / 2).toFloat()
            val startY = (measuredHeight / 2 - textR * cos(Math.PI / 180 * currentDegree) + rect.height() / 2).toFloat()
            canvas.drawText(speedText, startX, startY, scaleTextPaint)

            currentDegree = (currentDegree + stepDegree) % 360
        }
    }

    private fun drawSpeedHand(canvas: Canvas) {
        val circleRadius = (measuredWidth.coerceAtMost(measuredHeight)) / 2f - 16.toPx(context)
        val startRadius = (measuredWidth.coerceAtMost(measuredHeight)) / 2f - 140.toPx(context)

        val totalDegree = (endDegree - startDegree + 360) % 360
        val targetDegree = (totalDegree * currentSpeed / maxSpeed + startDegree) % 360

        val startX = (measuredWidth / 2 + startRadius * sin(Math.PI / 180 * targetDegree)).toFloat()
        val startY = (measuredHeight / 2 - startRadius * cos(Math.PI / 180 * targetDegree)).toFloat()

        val endX = (measuredWidth / 2 + circleRadius * sin(Math.PI / 180 * targetDegree)).toFloat()
        val endY = (measuredHeight / 2 - circleRadius * cos(Math.PI / 180 * targetDegree)).toFloat()
        speedHandPaint.shader = LinearGradient(startX, startY, endX, endY, ContextCompat.getColor(context, R.color.transparent), ContextCompat.getColor(context, R.color.red), Shader.TileMode.MIRROR)

        canvas.drawLine(startX, startY, endX, endY, speedHandPaint)
    }

    private fun drawCurrentSpeedText(canvas: Canvas) {
        currentSpeedPaint.textSize = 72.toPx(context).toFloat()
        canvas.drawText(currentSpeed.roundToInt().toString(), measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), currentSpeedPaint)

        currentSpeedPaint.textSize = 26.toPx(context).toFloat()
        val dy = currentSpeedPaint.textSize * 1.5f
        canvas.drawText("km/h", measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat() + dy, currentSpeedPaint)
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