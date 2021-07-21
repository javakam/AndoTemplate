package ando.toolkit.ext

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment

/**
 * # 尺寸
 *
 * @author javakam
 * @date 2020/9/29  14:53
 */

/* ---------- Context ---------- */

/**
 * 获取屏幕分辨率
 */
val Context.screenDensityDpi: Int
    get() = DeviceUtils.getScreenDensityDpi()

/**
 * 屏幕的宽度 screen width in pixels
 */
val Context.screenWidth: Int
    get() = DeviceUtils.getScreenWidth()

/**
 * 屏幕的高度 screen height in pixels
 */
val Context.screenHeight: Int
    get() = DeviceUtils.getScreenHeight()

/**
 * returns dp(dp) dimension value in pixels
 * @param value dp
 */
fun Context.dp2px(value: Int): Int = DimensionsUtils.dp2px(this, value)

fun Context.dp2px(value: Float): Int = DimensionsUtils.dp2px(this, value)

/**
 * return sp dimension value in pixels
 * @param value sp
 */
fun Context.sp2px(value: Int): Int = DimensionsUtils.sp2px(this, value)

fun Context.sp2px(value: Float): Int = DimensionsUtils.sp2px(this, value)

/**
 * converts [px] value into dp or sp
 * @param px
 */
fun Context.px2dp(px: Int): Float = DimensionsUtils.px2dp(this, px)

fun Context.px2sp(px: Int): Float = DimensionsUtils.px2sp(this, px)

/**
 * return dimen resource value in pixels
 * @param resource dimen resource
 */
fun Context.dimen2px(@DimenRes resource: Int): Int = DimensionsUtils.dimen2px(this, resource)

/**
 * 获取真实屏幕密度 【注意，Application和Activity的屏幕密度是不一样的】
 */
fun Context.realDpi(): Int {
    val metric = resources.displayMetrics
    val xdpi = metric.xdpi
    val ydpi = metric.ydpi
    return ((xdpi + ydpi) / 2.0f + 0.5f).toInt()
}

/* ---------- Fragment ---------- */

/**
 * 获取屏幕分辨率
 */
val Fragment.screenDensityDpi: Int
    get() = resources.displayMetrics.densityDpi

val Fragment.screenWidth
    get() = activity?.screenWidth

val Fragment.screenHeight
    get() = activity?.screenHeight

fun Fragment.dp2px(value: Int): Int = activity?.dp2px(value) ?: 0
fun Fragment.dp2px(value: Float): Int = activity?.dp2px(value) ?: 0

fun Fragment.sp2px(value: Int): Int = activity?.sp2px(value) ?: 0
fun Fragment.sp2px(value: Float): Int = activity?.sp2px(value) ?: 0

fun Fragment.px2dp(px: Int): Float = activity?.px2dp(px) ?: 0f
fun Fragment.px2sp(px: Int): Float = activity?.px2sp(px) ?: 0f

fun Fragment.dimen2px(@DimenRes resource: Int): Int = activity?.dimen2px(resource) ?: 0

fun Fragment.realDpi(): Int {
    val metric = resources.displayMetrics
    val xdpi = metric.xdpi
    val ydpi = metric.ydpi
    return ((xdpi + ydpi) / 2.0f + 0.5f).toInt()
}

/* ---------- View ---------- */

val View.screenDensityDpi: Int
    get() = resources.displayMetrics.densityDpi

val View.screenWidth
    get() = context.screenWidth

val View.screenHeight
    get() = context.screenHeight

fun View.dp2px(value: Int): Int = context.dp2px(value)
fun View.dp2px(value: Float): Int = context.dp2px(value)

fun View.sp2px(value: Int): Int = context.sp2px(value)
fun View.sp2px(value: Float): Int = context.sp2px(value)

fun View.px2dp(px: Int): Float = context.px2dp(px)
fun View.px2sp(px: Int): Float = context.px2sp(px)

fun View.dimen2px(@DimenRes resource: Int): Int = context.dimen2px(resource)

fun View.realDpi(): Int {
    val metric = resources.displayMetrics
    val xdpi = metric.xdpi
    val ydpi = metric.ydpi
    return ((xdpi + ydpi) / 2.0f + 0.5f).toInt()
}

object DimensionsUtils {
    fun dp2px(context: Context, value: Int): Int = (value * context.resources.displayMetrics.density).toInt()
    fun dp2px(context: Context, value: Float): Int = (value * context.resources.displayMetrics.density).toInt()

    fun sp2px(context: Context, value: Int): Int = (value * context.resources.displayMetrics.scaledDensity).toInt()
    fun sp2px(context: Context, value: Float): Int = (value * context.resources.displayMetrics.scaledDensity).toInt()

    fun px2dp(context: Context, px: Int): Float = px.toFloat() / context.resources.displayMetrics.density
    fun px2sp(context: Context, px: Int): Float = px.toFloat() / context.resources.displayMetrics.scaledDensity

    fun dimen2px(context: Context, @DimenRes resource: Int): Int = context.resources.getDimensionPixelSize(resource)
}