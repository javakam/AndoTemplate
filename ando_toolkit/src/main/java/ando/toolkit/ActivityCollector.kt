package ando.toolkit

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.text.TextUtils
import ando.toolkit.log.L
import android.annotation.SuppressLint
import java.lang.ref.WeakReference
import java.util.*

/**
 * 应用中所有Activity的管理器，可用于一键关闭所有 Activity
 *
 * https://github.com/guolindev/giffun/blob/master/main/src/main/java/com/quxianggif/util/ActivityCollector.kt
 */
@SuppressLint("StaticFieldLeak")
object ActivityCollector {

    private const val TAG = "123"

    private var mCurActivity: Activity? = null
    private val activityList = Stack<WeakReference<Activity>?>()

    fun size(): Int = activityList.size

    fun add(weakRefActivity: WeakReference<Activity>?) = activityList.add(weakRefActivity)

    fun remove(weakRefActivity: WeakReference<Activity>?) {
        val result = activityList.remove(weakRefActivity)
        L.i(TAG, "remove activity reference $result")
    }

    fun setCurActivity(activity: Activity) {
        mCurActivity = activity
    }

    fun getCurActivity(): Activity? = mCurActivity

    fun getAll(): Stack<WeakReference<Activity>?> = activityList

    fun finishAll() {
        if (activityList.isNotEmpty()) {
            for (activityWeakReference in activityList) {
                val activity = activityWeakReference?.get()
                if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
                    activity.finish()
                }
            }
            activityList.clear()
        }
    }

    fun <T : Activity?> haveActivity(tClass: Class<T>): Boolean {
        for (i in 0 until activityList.size) {
            val activity = activityList[i]?.get() ?: return false
            if (TextUtils.equals(activity.javaClass.name, tClass.name)) {
                return !activity.isDestroyed || !activity.isFinishing
            }
        }
        return false
    }

    fun isBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                L.i(
                    context.packageName, "此 appimportace ="
                            + appProcess.importance
                            + ",context.getClass().getName()="
                            + context.javaClass.name
                )
                return if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    L.i(context.packageName, "处于后台" + appProcess.processName)
                    true
                } else {
                    L.i(context.packageName, "处于前台" + appProcess.processName)
                    false
                }
            }
        }
        return false
    }

}