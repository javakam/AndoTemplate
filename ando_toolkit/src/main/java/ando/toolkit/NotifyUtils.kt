package ando.toolkit

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

/**
 * 通知栏权限工具
 *
 * @author javakam
 * @date 2019-09-04 14:00
 */
object NotifyUtils {
    /**
     * 通知栏权限是否获取
     */
    fun isNotificationEnabled(context: Context): Boolean {
        val manager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.areNotificationsEnabled() && (manager.importance != NotificationManager.IMPORTANCE_NONE)
        } else manager.areNotificationsEnabled()
    }

    /**
     * 打开通知栏权限设置页面
     */
    @SuppressLint("ObsoleteSdkInt")
    fun openNotifyPermissionSetting(context: Context) {
        try {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
                context.startActivity(intent)
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
                context.startActivity(intent)
                return
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
                return
            }

            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
                return
            }
            intent.action = Intent.ACTION_VIEW
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails")
            intent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}