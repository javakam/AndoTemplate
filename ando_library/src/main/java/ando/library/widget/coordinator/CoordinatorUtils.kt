package ando.library.widget.coordinator

import ando.toolkit.ext.DeviceUtils
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

/**
 * # CoordinatorUtils
 *
 * @author javakam
 * @date 2021/4/14  13:57
 */
object CoordinatorUtils {

    /**
     * 设置 CoordinatorLayout
     */
    fun setCoordinatorLayout(coordinatorLayout: CoordinatorLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout,
                object : OnApplyWindowInsetsListener {
                    override fun onApplyWindowInsets(
                        v: View?,
                        insets: WindowInsetsCompat,
                    ): WindowInsetsCompat {
                        @Suppress("DEPRECATION")
                        return insets.consumeSystemWindowInsets()
                    }
                })
        }
    }

    /**
     * 设置 AppBarLayout
     *
     * @param opaqueColor 不透明时候的颜色
     */
    fun setAppBarLayout(appBar: AppBarLayout, titleView: View, @ColorInt opaqueColor: Int) {
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            val fraction = abs(i * 1.0F) / appBarLayout.totalScrollRange
            val color = changeAlpha(opaqueColor, fraction)
            //Log.w("123", "offset = $fraction")
            titleView.setBackgroundColor(color)
        })
    }

    /**
     * @param titleView 通常为Toolbar
     */
    fun setStatusBarPaddingAndHeight(titleView: View?) {
        if (titleView == null) return

        val statusBarHeight: Int = DeviceUtils.getStatusBarHeight()
        titleView.setPadding(
            titleView.paddingLeft, statusBarHeight, titleView.paddingRight,
            titleView.paddingBottom
        )

        val toolBarHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, titleView.resources?.displayMetrics).toInt()
        titleView.layoutParams.height = statusBarHeight + toolBarHeight
    }

    /**
     * 根据百分比修改颜色透明度 -> AppBarLayout.addOnOffsetChangedListener
     */
    private fun changeAlpha(color: Int, fraction: Float): Int {
        return Color.argb((Color.alpha(color) * fraction).toInt(), Color.red(color), Color.green(color), Color.blue(color))
    }

}