package ando.library.base

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Process.killProcess
import android.os.Process.myPid
import androidx.multidex.MultiDexApplication
import ando.toolkit.ActivityCollector.finishAll

/**
 * # BaseApplication
 *
 * @author javakam
 * @date 2019/8/6  9:13
 */
open class BaseApplication : MultiDexApplication() {

    /**
     * 重写 getResource 方法，防止系统字体影响
     *
     * https://www.jianshu.com/p/5effff3db399
     */
    override fun getResources(): Resources? { //禁止app字体大小跟随系统字体大小调节
        val resources = super.getResources()
        if (resources != null && resources.configuration.fontScale != 1.0f) {
            val configuration = resources.configuration
            configuration.fontScale = 1.0f
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            //createConfigurationContext(configuration)
        }
        return resources
    }

    companion object {
        /**
         * 是否灰度显示
         */
        var isGray = false

        fun exit() {
            finishAll()
            killProcess(myPid())
            //System.exit(0)
        }

        /**
         * 触发 Home 事件
         *
         * 模拟用户退出到桌面, 并没有真正退出应用
         */
        fun exitHome(activity: Activity) {
            val backHome = Intent(Intent.ACTION_MAIN)
            backHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            backHome.addCategory(Intent.CATEGORY_HOME)
            activity.startActivity(backHome)
        }
    }

}