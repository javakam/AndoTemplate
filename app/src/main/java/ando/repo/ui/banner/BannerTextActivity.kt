package ando.repo.ui.banner

import ando.widget.banner.TextBanner
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * # 文字轮播(仅支持左右方向)
 *
 * @author javakam
 * @date 2021/3/24  11:47
 */
class BannerTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val textBanner = TextBanner(this@BannerTextActivity).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 120)
            setItemBgColor(Color.GRAY)

            setSource(mutableListOf("千山鸟飞绝，", "万径人踪灭。", "孤舟蓑笠翁，", "独钓寒江雪。"))
            startScroll()
        }

        container.addView(textBanner)

        setContentView(container)
    }

}