package ando.repo.config

import ando.repo.ui.IndicatorActivity
import ando.repo.ui.MainActivity
import ando.repo.ui.TabLayoutActivity
import android.app.Activity
import android.content.Intent

/**
 * # AppRouter
 *
 * @author javakam
 * @date 2021/3/4  13:39
 */
object AppRouter {

    fun toMain(activity: Activity) {
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }

    fun toWidgetTabLayout(activity: Activity) {
        activity.startActivity(Intent(activity, TabLayoutActivity::class.java))
    }

    fun toWidgetIndicator(activity: Activity) {
        activity.startActivity(Intent(activity, IndicatorActivity::class.java))
    }

}