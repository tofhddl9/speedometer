package com.lgtm.simple_speedometer.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

fun Context.toPx(px: Number): Int {
    return (px.toFloat() / (resources.displayMetrics.densityDpi.toFloat().div(DisplayMetrics.DENSITY_DEFAULT))).toInt()
}

fun Number.toPx(context: Context) = context.toDp(this)


fun Context.toDp(dp: Number): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
}

fun Number.toDp(context: Context) = context.toPx(this)

fun Context.screenWidth(): Int = windowManager.defaultDisplay.width

fun Context.screenHeight(): Int = windowManager.defaultDisplay.height
