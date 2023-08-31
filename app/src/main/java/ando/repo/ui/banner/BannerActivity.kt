package ando.repo.ui.banner

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import ando.repo.R
import ando.toolkit.thread.ThreadUtils
import ando.toolkit.ext.noNull
import ando.toolkit.ext.toastLong
import ando.toolkit.ext.toastShort
import ando.toolkit.thread.ThreadTask
import ando.widget.banner.BannerItem
import ando.widget.banner.ImageBanner
import ando.widget.indicator.MagicIndicator
import ando.widget.indicator.ViewPagerHelper
import ando.widget.indicator.usage.navigator.RoundRectNavigator
import ando.widget.indicator.usage.navigator.ScaleCircleNavigator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

/**
 * # BannerActivity
 *
 * @author javakam
 * @date 2021/3/24  11:47
 */
class BannerActivity : AppCompatActivity() {

    private val mRecycler: RecyclerView by lazy { findViewById(R.id.recyclerView) }
    private val mAdapter: ListAdapter by lazy { ListAdapter() }
    private val mBannerData: List<BannerItem> = mutableListOf(
        BannerItem(
            "推荐",
            "http://pic.ntimg.cn/20130129/11507979_020415120167_2.jpg"
        ),
        BannerItem(
            "热点",
            "http://pic.ntimg.cn/file/20210320/29633157_104445000089_2.jpg"
        ),
        BannerItem(
            "动态",
            "http://pic.ntimg.cn/20130224/11507979_230737207196_2.jpg"
        ),
        BannerItem(
            "影视",
            "http://pic.ntimg.cn/file/20210320/32310093_092207251771_2.jpg"
        ),
        BannerItem(
            "直播",
            "http://pic.ntimg.cn/file/20210318/31747648_210726576081_2.jpg"
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_banner)
        toastLong("模拟延时加载(3秒)")

        mRecycler.itemAnimator = null
        mRecycler.layoutManager = LinearLayoutManager(this)

        val headerView = layoutInflater.inflate(R.layout.layout_widget_banner_header, null, false)
        //Banner
        val banner = headerView.findViewById<ImageBanner>(R.id.bannerImage)
        //banner.setSource(mBannerData)
        banner.imageLoader = BannerImageLoader()
        banner.colorDrawable = ColorDrawable(ContextCompat.getColor(this, R.color.white))
        banner.setOnItemClickListener {
            toastShort("Index :$it")
        }

        //Indicator
        if (banner.size() > 0) {
            initIndicator1(headerView, banner)
            //另外样式
            initIndicator2(headerView, banner)
            initIndicator3(headerView, banner)
        }

        mAdapter.addHeaderView(headerView)
        mRecycler.adapter = mAdapter

        //异步更新数据
        ThreadUtils.executeByCached(ThreadTask({
            //子线程
            Thread.sleep(3000)
            mutableListOf(
                BannerItem(
                    "电影",
                    "http://pic.ntimg.cn/file/20210320/32310093_092207251771_2.jpg"
                ),
                BannerItem(
                    "电视剧",
                    "http://pic.ntimg.cn/file/20210318/31747648_210726576081_2.jpg"
                ),
            )
        }, {
            //主线程
            if (!isDestroyed && !isFinishing) {
                mAdapter.setList(mutableListOf("aaa", "bbb", "ccc", "ddd"))

                banner.setSource(it)
                banner.startScroll()

                if (banner.size() > 0) {
                    initIndicator1(headerView, banner)
                    initIndicator2(headerView, banner)
                    initIndicator3(headerView, banner)
                }
            }
        }))

    }

    private fun initIndicator1(v: View, banner: ImageBanner) {
        val indicator = v.findViewById<MagicIndicator>(R.id.indicator)
        val roundNavigator = RoundRectNavigator(this)
        roundNavigator.isFollowTouch = true //是否跟随手指滑动
        roundNavigator.totalCount = banner.size()

        roundNavigator.itemColor = Color.LTGRAY
        roundNavigator.indicatorColor = ContextCompat.getColor(this, R.color.color_main_blue)

        roundNavigator.setItemWidth(18.0)
        roundNavigator.setItemSpacing(4.0)
        roundNavigator.setItemHeight(4.0)
        roundNavigator.setItemRadius(4.0)

        roundNavigator.setOnItemClickListener { index -> banner.viewPager?.currentItem = index }
        //roundNavigator.notifyDataSetChanged()
        indicator.navigator = roundNavigator

        //Banner 和 Indicator 绑定到一起, 同步滑动
        banner.viewPager?.apply { ViewPagerHelper.bind(indicator, this) }
    }

    private fun initIndicator2(v: View, banner: ImageBanner) {
        val tvItemTitle = v.findViewById<TextView>(R.id.tv_item_title)
        val indicator2 = v.findViewById<MagicIndicator>(R.id.indicator2)
        val roundNavigator2 = RoundRectNavigator(this)
        roundNavigator2.isFollowTouch = true //是否跟随手指滑动
        roundNavigator2.totalCount = banner.size()

        roundNavigator2.itemColor = Color.WHITE
        roundNavigator2.indicatorColor = ContextCompat.getColor(this, R.color.color_main_red)

        roundNavigator2.setItemWidth(6.0)
        roundNavigator2.setItemSpacing(5.0)
        roundNavigator2.setItemHeight(6.0)
        roundNavigator2.setItemRadius(6.0)

        roundNavigator2.setOnItemClickListener { index -> banner.viewPager?.currentItem = index }
        indicator2.navigator = roundNavigator2

        val item: BannerItem = banner.getItem(banner.currentPosition)
        tvItemTitle.text = item.title.noNull()

        banner.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                tvItemTitle.text = banner.getItem(position).title.noNull()
            }
        })
        banner.viewPager?.apply { ViewPagerHelper.bind(indicator2, this) }
    }

    private fun initIndicator3(v: View, banner: ImageBanner) {
        val indicator3 = v.findViewById<MagicIndicator>(R.id.indicator3)
        val scaleCircleNavigator = ScaleCircleNavigator(this)
        scaleCircleNavigator.setSkimOver(true)
        scaleCircleNavigator.setFollowTouch(true) //是否跟随手指滑动
        scaleCircleNavigator.setCircleCount(banner.size())
        scaleCircleNavigator.setNormalCircleColor(Color.WHITE)
        scaleCircleNavigator.setSelectedCircleColor(Color.BLUE)
        scaleCircleNavigator.setMaxRadius(10)
        scaleCircleNavigator.setMinRadius(7)
        scaleCircleNavigator.setCircleSpacing(13)
        indicator3.navigator = scaleCircleNavigator

//        val bezierNavigator = BezierNavigator(this)
//        bezierNavigator.isFollowTouch = true
//        bezierNavigator.circleCount = banner.size()
//        bezierNavigator.circleSpacing = 15
//        bezierNavigator.setColors(
//            Color.parseColor("#ff4a42"),
//            Color.parseColor("#fcde64"),
//            Color.parseColor("#73e8f4"),
//            Color.parseColor("#d2e824"),
//            Color.parseColor("#c683fe")
//        )
//        bezierNavigator.setCircleClickListener { index -> banner.viewPager?.currentItem = index }
//        indicator3.navigator = bezierNavigator

        banner.viewPager?.apply { ViewPagerHelper.bind(indicator3, this) }
    }

    private class ListAdapter :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_widget_banner) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.tv_name, item.noNull())
        }
    }

}