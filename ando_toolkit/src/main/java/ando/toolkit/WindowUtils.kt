package ando.toolkit

import android.view.View
import android.view.Window

/**
 * # WindowUtils
 *
 * Window窗体工具类
 *
 * @author javakam
 * @date 2018/9/23 18:54
 */
object WindowUtils {

    /**
     * 隐藏底部导航栏
     */
    fun hideStatusBarBottom(window: Window) {
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = option
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = option
            }
        }
    }

    /**
     * 全屏显示
     */
    fun hideStatusBar(window: Window?) {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}