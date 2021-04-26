package ando.toolkit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import java.util.*

/**
 * # ScreenLockUtils
 *
 * 用于保持屏幕高亮的工具
 */
object ScreenLockUtils {
    private const val TAG = "ScreenLockUtil"
    private val mWakeLockArray = HashMap<Activity, WakeLock>()
    private val mIsUnlockArray = HashMap<Activity, Boolean>()

    /**
     * 保持屏幕常亮
     */
    fun keepScreenOn(activity: Activity) {
        var wakeLock: WakeLock? = mWakeLockArray[activity]
        if (wakeLock == null) {
            val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
            @Suppress("DEPRECATION")
            wakeLock = powerManager.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.FULL_WAKE_LOCK,
                activity.javaClass.name
            )
        }
        wakeLock?.apply {
            if (!this.isHeld) {
                this.acquire(10 * 60 * 1000L /*10 minutes*/)
            }
            mWakeLockArray[activity] = this
        }
        cancelLockScreen(activity)
        Log.i(TAG, "开启屏幕常亮")
    }

    /**
     * 取消屏幕常亮
     */
    fun cancelKeepScreen(activity: Activity) {
        val wakeLock = mWakeLockArray[activity]
        if (wakeLock != null) {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
        Log.i(TAG, "取消屏幕常亮")
    }

    /**
     * 取消锁屏限制
     */
    @SuppressLint("MissingPermission")
    private fun cancelLockScreen(activity: Activity) {
        val isUnlock = mIsUnlockArray[activity]
        if (isUnlock != null && isUnlock) {
            return
        }

        val keyguardManager = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        @Suppress("DEPRECATION")
        val keyguardLock = keyguardManager.newKeyguardLock(activity.javaClass.name)
        keyguardLock.disableKeyguard()
        mIsUnlockArray[activity] = true
    }

}