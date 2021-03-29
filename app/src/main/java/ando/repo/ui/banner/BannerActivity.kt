package ando.repo.ui.banner

import ando.library.views.recycler.BaseQuickAdapter
import ando.library.views.recycler.BaseViewHolder
import ando.repo.R
import ando.toolkit.ext.noNull
import ando.widget.banner.BannerItem
import ando.widget.banner.ImageBanner
import ando.widget.indicator.MagicIndicator
import ando.widget.indicator.ViewPagerHelper
import ando.widget.indicator.usage.navigator.RoundRectNavigator
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
        mRecycler.itemAnimator = null
        mRecycler.layoutManager = LinearLayoutManager(this)

        val headerView = layoutInflater.inflate(R.layout.layout_widget_banner_header, null, false)
        //Banner
        val banner = headerView.findViewById<ImageBanner>(R.id.bannerImage)
        banner.setSource(mBannerData)
        banner.imageLoader = BannerImageLoader()
        banner.colorDrawable = ColorDrawable(ContextCompat.getColor(this, R.color.white))
        banner.startScroll()

        //Indicator
        val indicator = headerView.findViewById<MagicIndicator>(R.id.indicator)
        val roundNavigator = RoundRectNavigator(this)
        roundNavigator.isFollowTouch = true //是否跟随手指滑动
        roundNavigator.totalCount = mBannerData.size

        roundNavigator.itemColor = Color.LTGRAY
        roundNavigator.indicatorColor = ContextCompat.getColor(this, R.color.color_main_blue)

        roundNavigator.setItemWidth(18.0)
        roundNavigator.setItemSpacing(4.0)
        roundNavigator.setItemHeight(3.0)
        roundNavigator.setItemRadius(3.0)

        roundNavigator.setOnItemClickListener { index -> banner.viewPager?.currentItem = index }
        roundNavigator.notifyDataSetChanged()
        indicator.navigator = roundNavigator

        //Banner 和 Indicator 绑定到一起, 同步滑动
        banner.viewPager?.apply { ViewPagerHelper.bind(indicator, this) }

        //另外一种样式
        initIndicator2(headerView, banner)

        mAdapter.addHeaderView(headerView)
        mAdapter.replaceData(mutableListOf("aaa", "bbb", "ccc", "ddd"))
        mRecycler.adapter = mAdapter

    }

    private fun initIndicator2(v: View, banner: ImageBanner) {
        val tvItemTitle = v.findViewById<TextView>(R.id.tv_item_title)
        val indicator2 = v.findViewById<MagicIndicator>(R.id.indicator2)
        val roundNavigator2 = RoundRectNavigator(this)
        roundNavigator2.isFollowTouch = true //是否跟随手指滑动
        roundNavigator2.totalCount = mBannerData.size

        roundNavigator2.itemColor = Color.WHITE
        roundNavigator2.indicatorColor = ContextCompat.getColor(this, R.color.color_main_red)

        roundNavigator2.setItemWidth(6.0)
        roundNavigator2.setItemSpacing(4.0)
        roundNavigator2.setItemHeight(6.0)
        roundNavigator2.setItemRadius(6.0)

        roundNavigator2.setOnItemClickListener { index -> banner.viewPager?.currentItem = index }
        roundNavigator2.notifyDataSetChanged()
        indicator2.navigator = roundNavigator2

        banner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val item: BannerItem = banner.getItem(position)
                tvItemTitle.text = item.title.noNull()
            }

            override fun onPageSelected(position: Int) {
                val item: BannerItem = banner.getItem(position)
                tvItemTitle.text = item.title.noNull()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        banner.viewPager?.apply { ViewPagerHelper.bind(indicator2, this) }
    }

    private class ListAdapter :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_widget_banner) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.tv_name, item.noNull())
        }
    }

}