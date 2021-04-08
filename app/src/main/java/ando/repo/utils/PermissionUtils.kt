package ando.repo.utils

import ando.toolkit.log.L
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

/**
 * 权限框架 (Permissions framework)
 *
 * com.permissionx.guolindev.PermissionX
 */
object PermissionUtils {

    const val REQUEST_CODE_CAMERA: Int = 0x6
    const val REQUEST_CODE_STORAGE: Int = 0x7
    const val REQUEST_CODE_OVERLAY: Int = 0x8

    /**
     * 相应的清单文件中配置 (The corresponding listing file configuration):
     *
     * <!-- Apps on devices running Android 4.4 (API level 19) or higher cannot
     *      access external storage outside their own "sandboxed" directory, so
     *      the READ_EXTERNAL_STORAGE (and WRITE_EXTERNAL_STORAGE) permissions
     *      aren't necessary. -->
     *
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     *
     * <uses-permission
     *      android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     *      android:maxSdkVersion="28"
     *      tools:ignore="ScopedStorage" />
     */
    val PERMISSIONS_STORAGE: Array<String> by lazy {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val PERMISSIONS_CAMERA: Array<String> by lazy {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        else arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    //请开启电话和存储权限
    val PERMISSIONS_UMENG: Array<String> by lazy {
        arrayOf(
            //Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun requestPermission(
        context: Any,
        permissions: Array<out String>,
        block: ((allGranted: Boolean, grantedList: List<String>, deniedList: List<String>) -> Unit)? = null,
    ) {
        when (context) {
            is FragmentActivity -> requestPermission(context, permissions, block)
            is Fragment -> requestPermission(context.requireActivity(), permissions, block)
            else -> return
        }
    }

    fun requestPermission(
        activity: FragmentActivity,
        permissions: Array<out String>,
        block: ((allGranted: Boolean, grantedList: List<String>, deniedList: List<String>) -> Unit)? = null,
    ) {
        PermissionX.init(activity)
            .permissions(*permissions)
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "请在设置中手动开启以下权限", "允许", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    L.i("已授予所有权限")
                } else {
                    deniedList?.forEach {
                        L.w("以下权限被拒绝：[ $it ]")
                    }
                }
                block?.invoke(allGranted, grantedList, deniedList)
            }
    }

    /**
     * 悬浮窗权限  AndroidManifest.xml
     *
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
     */
    fun overlay(activity: Activity, onGranted: () -> Unit = {}) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否已经授予权限
            if (!Settings.canDrawOverlays(activity)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY)
            } else onGranted.invoke()
        } else onGranted.invoke()
    }

    fun verifyStoragePermissions(activity: Activity) {
        verifyPermissions(activity, PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE)
    }

    fun verifyCameraPermissions(activity: Activity) {
        verifyPermissions(activity, PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA)
    }

    fun verifyPermissions(activity: Activity, permissions: Array<out String>, requestCode: Int) {
        for (permission in permissions) {
            val granted = ContextCompat.checkSelfPermission(activity, permission)
            if (granted != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
                break
            }
        }
    }

    fun havePermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions.isNotEmpty()) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission.toString())
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }
}