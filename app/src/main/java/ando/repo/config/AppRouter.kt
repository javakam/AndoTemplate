package ando.repo.config

import ando.indicator.example.ExampleMainActivity
import ando.repo.ui.*
import ando.repo.ui.coordinator.TabLayoutActivity
import ando.repo.ui.indicator.IndicatorActivity
import ando.repo.ui.indicator.IndicatorViewPagerActivity
import ando.repo.ui.indicator.IndicatorViewPagerNoActivity
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

    //顶部导航 + Fragment + 不带ViewPager
    fun toWidgetIndicatorViewPagerNo(activity: Activity) {
        activity.startActivity(Intent(activity, IndicatorViewPagerNoActivity::class.java))
    }

    //顶部导航 + Fragment + ViewPager
    fun toWidgetIndicatorViewPager(activity: Activity) {
        activity.startActivity(Intent(activity, IndicatorViewPagerActivity::class.java))
    }

    fun toWidgetIndicatorOrigin(activity: Activity) {
        activity.startActivity(Intent(activity, ExampleMainActivity::class.java))
    }



}