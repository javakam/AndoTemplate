package ando.toolkit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.net.Uri
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * # Activity 工具类
 *
 * @author javakam
 * @date 2019/11/15 14:44
 */
object ActivityUtils {

    fun isActivityLive(activity: Activity?): Boolean =
        activity != null && !activity.isFinishing && !activity.isDestroyed

    /**
     * 切换全屏状态
     *
     * @param activity Activity
     * @param isFull   设置为true则全屏，否则非全屏
     */
    fun toggleFullScreen(activity: Activity, isFull: Boolean) {
        hideTitleBar(activity)
        val window = activity.window
        val params = window.attributes
        if (isFull) {
            @Suppress("DEPRECATION")
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = params
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            @Suppress("DEPRECATION")
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = params
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * 设置为全屏
     */
    fun setFullScreen(activity: Activity) = toggleFullScreen(activity, true)

    /**
     * 隐藏Activity的系统默认标题栏
     */
    fun hideTitleBar(activity: Activity) {
        if (activity is AppCompatActivity) activity.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        else activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    /**
     * 强制设置Activity的显示方向为垂直方向。
     */
    fun setScreenVertical(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 强制设置Activity的显示方向为横向
     */
    fun setScreenHorizontal(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * 快捷方式是否存在
     */
    fun ifAddShortCut(context: Context, appName: String): Boolean {
        var isInstallShortCut = false
        val cr = context.contentResolver
        val authority = "com.android.launcher2.settings"
        var cursor: Cursor? = null
        try {
            val uri = Uri.parse("content://$authority/favorites?notify=true")
            cursor =
                cr.query(uri, arrayOf("title", "iconResource"), "title=?", arrayOf(appName), null)
            if (null != cursor && cursor.count > 0) isInstallShortCut = true
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }
        return isInstallShortCut
    }

    fun startActivity(context: Any, intent: Intent) {
        if (context is Activity) {
            if (isActivityLive(context)) {
                context.startActivity(intent)
            }
        } else if (context is Fragment) {
            val activity = context.activity
            if (isActivityLive(activity)) {
                context.startActivity(intent)
            }
        }
    }

    fun startActivityForResult(context: Any, intent: Intent, requestCode: Int) {
        if (context is Activity) {
            if (isActivityLive(context)) {
                context.startActivityForResult(intent, requestCode)
            }
        } else if (context is Fragment) {
            val activity = context.activity
            if (isActivityLive(activity)) {
                context.startActivityForResult(intent, requestCode)
            }
        }
    }
}