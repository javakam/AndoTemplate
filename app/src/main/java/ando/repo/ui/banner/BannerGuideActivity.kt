package ando.repo.ui.banner

import ando.repo.R
import ando.toolkit.ext.toastShort
import ando.widget.banner.GuideBanner
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * # 引导页
 *
 * @author javakam
 * @date 2021/3/24  11:47
 */
class BannerGuideActivity : AppCompatActivity() {

    private fun initWindowStyle() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        val params = window.attributes
        @Suppress("DEPRECATION")
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = params
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initWindowStyle()
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val guideBanner = GuideBanner(this@BannerGuideActivity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            //动画
            //viewPager.setPageTransformer(true, FadeSlideTransformer())
            imageLoader = BannerImageLoader()
            //图片
            setSource(
                mutableListOf<Any>(
                    //Remote
                    //"http://pic.ntimg.cn/20130129/11507979_020415120167_2.jpg",
                    //"http://pic.ntimg.cn/file/20210320/29633157_104445000089_2.jpg",
                    //"http://pic.ntimg.cn/20130224/11507979_230737207196_2.jpg",

                    //Local
                    R.mipmap.pic1,
                    R.mipmap.pic2,
                    R.mipmap.pic3,
                )
            )
            startScroll()
            setOnJumpClickListener {
                toastShort("jump")
            }
        }

        container.addView(guideBanner)
        setContentView(container)
    }

    class FadeSlideTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = 0f
            if (position <= -1.0f || position >= 1.0f) {
                page.alpha = 0.0f
            } else if (position == 0.0f) {
                page.alpha = 1.0f
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                page.alpha = 1.0f - abs(position)
            }
        }
    }

}