package ando.toolkit

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Adapted from com.blankj.utilcode.util.ProcessUtils#getCurrentProcessName
 */
object ProcessUtils {

    fun getCurrentProcessName(context: Context): String? {
        var name: String? = currentProcessNameByFile
        if (!TextUtils.isEmpty(name)) {
            return name
        }
        name = getCurrentProcessNameByAms(context)
        if (!TextUtils.isEmpty(name)) {
            return name
        }
        name = getCurrentProcessNameByReflect(context)
        return name
    }

    private val currentProcessNameByFile: String
        get() {
            var reader: BufferedReader? = null
            return try {
                val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
                reader = BufferedReader(FileReader(file))
                val processName = reader.readLine()
                if (!TextUtils.isEmpty(processName)) {
                    processName.trim { it <= ' ' }
                } else ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            } finally {
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    private fun getCurrentProcessNameByAms(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            ?: return ""
        val info = am.runningAppProcesses
        if (info == null || info.size == 0) {
            return ""
        }
        val pid = Process.myPid()
        for (aInfo in info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName
                }
            }
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(context: Context): String? {
        var processName = ""
        try {
            val app = context.applicationContext as Application
            val loadedApkField = app.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[app]
            var activityThreadField: Field? = null
            if (loadedApk != null) {
                activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
                activityThreadField.isAccessible = true
                val activityThread = activityThreadField[loadedApk]
                var getProcessName: Method? = null
                if (activityThread != null) {
                    getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
                    processName = getProcessName.invoke(activityThread) as String
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return processName
    }
}