package ando.toolkit.ext

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import ando.toolkit.log.L
import android.app.Activity
import android.content.res.Resources
import android.graphics.Rect

/**
 * Title: 扩展函数 - 设备信息
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/9/29  16:56
 */

/* ---------- Context ---------- */

/**
 * 获取版本号  eg: 123
 */
val Context.versionCode: Long
    get() = try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        if (packageInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } else 1L
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        1L
    }

/**
 * 获取版本名 eg: 1.0.6
 */
val Context.versionName: String
    get() = try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }

/**
 * 获取app名称 eg: "学习强国"
 */
val Context.appName: String
    get() = try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        resources.getString(packageInfo.applicationInfo.labelRes)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }

/**
 * 获取应用的图标
 */
val Context.appIcon: Drawable?
    get() = try {
        packageManager.getApplicationInfo(packageName, 0).loadIcon(packageManager)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }

/**
 * 获取手机相对分辨率
 *
 * https://www.jianshu.com/p/1a931d943fb4
 */
val Context.screenRelatedInformation: String
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels
        val heightPixels = outMetrics.heightPixels
        val densityDpi = outMetrics.densityDpi
        val density = outMetrics.density
        val scaledDensity = outMetrics.scaledDensity
        //可用显示大小的绝对宽度（以像素为单位）。
        //屏幕密度表示为每英寸点数。
        //显示器的逻辑密度。
        //显示屏上显示的字体缩放系数。
        L.d(
            """
                widthPixels = $widthPixels,heightPixels = $heightPixels
                ,densityDpi = $densityDpi
                ,density = $density,scaledDensity = $scaledDensity
            """.trimIndent()
        )
        return widthPixels.toString() + "x" + heightPixels
    }

/**
 * 获取手机绝对分辨率  https://www.jianshu.com/p/1a931d943fb4
 */
val Context.realScreenRelatedInformation: String
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels
        val heightPixels = outMetrics.heightPixels
        val densityDpi = outMetrics.densityDpi
        val density = outMetrics.density
        val scaledDensity = outMetrics.scaledDensity
        //可用显示大小的绝对宽度（以像素为单位）。
        //可用显示大小的绝对高度（以像素为单位）。
        //屏幕密度表示为每英寸点数。
        //显示器的逻辑密度。
        //显示屏上显示的字体缩放系数。
        L.d(
            """
                widthPixels = $widthPixels,heightPixels = $heightPixels
                ,densityDpi = $densityDpi
                ,density = $density,scaledDensity = $scaledDensity
            """.trimIndent()
        )
        return widthPixels.toString() + "x" + heightPixels
    }

/* ---------- Fragment ---------- */

val Fragment.versionCode: Long get() = activity?.versionCode ?: 0
val Fragment.versionName: String get() = activity?.versionName ?: ""
val Fragment.appName: String get() = activity?.appName ?: ""
val Fragment.appIcon: Drawable? get() = activity?.appIcon
val Fragment.screenRelatedInformation: String get() = activity?.screenRelatedInformation ?: ""
val Fragment.realScreenRelatedInformation: String
    get() = activity?.realScreenRelatedInformation ?: ""

/* ---------- View ---------- */

val View.versionCode: Long get() = context.versionCode
val View.versionName: String get() = context.versionName
val View.appName: String get() = context.appName
val View.appIcon: Drawable? get() = context.appIcon
val View.screenRelatedInformation: String get() = context.screenRelatedInformation
val View.realScreenRelatedInformation: String get() = context.realScreenRelatedInformation

/* ---------- DeviceUtils ---------- */

object DeviceUtils {
    /**
     * 获取当前手机系统版本号
     */
    fun getSystemVersion(): String? = Build.VERSION.RELEASE

    /**
     * 获取手机型号
     */
    fun getSystemModel(): String? = Build.MODEL

    /**
     * 获取手机厂商
     */
    fun getDeviceBrand(): String? = Build.BRAND

    /**
     * 获取手机设备名
     */
    fun getSystemDevice(): String? = Build.DEVICE

    /**
     * 获取 CPU ABI
     */
    fun getCpuABIS(): Array<String>? = Build.SUPPORTED_ABIS

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