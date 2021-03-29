package ando.repo.ui.banner

import ando.library.views.recycler.BaseQuickAdapter
import ando.library.views.recycler.BaseViewHolder
import ando.repo.R
import ando.toolkit.ext.noNull
import ando.widget.banner.banner.BannerItem
import ando.widget.banner.banner.CustomImageBanner
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        BannerItem("推荐", "http://pic.ntimg.cn/20130129/11507979_020415120167_2.jpg"),
        BannerItem("热点", "http://pic.ntimg.cn/file/20210320/29633157_104445000089_2.jpg"),
        BannerItem("动态", "http://pic.ntimg.cn/20130224/11507979_230737207196_2.jpg"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_banner)

        mRecycler.itemAnimator = null
        mRecycler.layoutManager = LinearLayoutManager(this)

        //Banner
        val headerView = layoutInflater.inflate(R.layout.layout_widget_banner_header, null, false)
        val bannerImage = headerView.findViewById<CustomImageBanner>(R.id.bannerImage)
        bannerImage.setSource(mBannerData)
        bannerImage.imageLoader = BannerImageLoader()
        bannerImage.colorDrawable = ColorDrawable(ContextCompat.getColor(this, R.color.white))
        bannerImage.startScroll()
        //Indicator
        mAdapter.addHeaderView(headerView)
        //
        mAdapter.replaceData(mutableListOf("aaa", "bbb", "ccc"))
        mRecycler.adapter = mAdapter
    }

    private class ListAdapter :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_widget_banner) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.tv_name, item.noNull())
        }
    }

}