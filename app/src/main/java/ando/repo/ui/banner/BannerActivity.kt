package ando.repo.ui.banner

import ando.library.views.recycler.BaseQuickAdapter
import ando.library.views.recycler.BaseViewHolder
import ando.repo.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_banner)

        mRecycler.itemAnimator = null
        mRecycler.layoutManager = LinearLayoutManager(this)

        mRecycler.adapter = mAdapter
    }

    private class ListAdapter :
        BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_widget_banner) {

        override fun convert(holder: BaseViewHolder, item: String) {

        }

    }


}