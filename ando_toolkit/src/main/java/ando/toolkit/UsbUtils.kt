package ando.toolkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.text.TextUtils
import java.io.File
import java.util.*

object UsbUtils {

    /**
     * 获取外部存储列表 , Android 2.3之后的系统
     */
    fun getStoragePaths(cxt: Context): Array<String> {
        val pathsList: MutableList<String> = ArrayList()
        val storageManager = cxt.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        try {
            @SuppressLint("DiscouragedPrivateApi")
            val method = StorageManager::class.java.getDeclaredMethod("getVolumePaths")
            method.isAccessible = true
            val result = method.invoke(storageManager)
            if (result is Array<*>) {
                var statFs: StatFs
                for (r in result) {
                    val path = r as String
                    if (!TextUtils.isEmpty(path) && File(path).exists()) {
                        statFs = StatFs(path)
                        if (statFs.blockCount * statFs.blockSize != 0) {
                            pathsList.add(path)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val externalFolder = Environment.getExternalStorageDirectory()
            if (externalFolder != null) {
                pathsList.add(externalFolder.absolutePath)
            }
        }
        return pathsList.toTypedArray()
    }

}