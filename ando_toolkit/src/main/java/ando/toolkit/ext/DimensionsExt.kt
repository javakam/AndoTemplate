package ando.toolkit.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment

/**
 * Title: 扩展函数 - 尺寸转换
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/9/29  14:53
 */

/* ---------- Context ---------- */

/**
 * 获取屏幕分辨率
 */
val Context.screenDensityDpi: Int
    get() = resources.displayMetrics.densityDpi

/**
 * 屏幕的宽度 screen width in pixels
 */
val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

/**
 * 屏幕的高度 screen height in pixels
 */
val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels

/**
 * returns dp(dp) dimension value in pixels
 * @param value dp
 */
fun Context.dp2px(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dp2px(value: Float): Int = (value * resources.displayMetrics.density).toInt()

/**
 * return sp dimension value in pixels
 * @param value sp
 */
fun Context.sp2px(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.sp2px(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

/**
 * converts [px] value into dp or sp
 * @param px
 */
fun Context.px2dp(px: Int): Float = px.toFloat() / resources.displayMetrics.density

fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

/**
 * return dimen resource value in pixels
 * @param resource dimen resource
 */
fun Context.dimen2px(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

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

object DimensionUtils {

    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    fun getNavBarHeight(): Int {
        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) res.getDimensionPixelSize(resourceId) else 0
    }

    /**
     * 获取状态栏高度高度  the height of status bar
     */
    fun getStatusBarHeight(): Int {
        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) res.getDimensionPixelSize(resourceId) else 0
    }

    /**
     * 获取系统状态栏高度  todo 2020年9月30日 14:56:48 测试
     */
    fun getStatusBarHeightDecor(activity: Activity): Int {
        val localRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(localRect)
        return localRect.top
    }

}