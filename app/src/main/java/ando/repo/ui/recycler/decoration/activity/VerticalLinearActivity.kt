package ando.repo.ui.recycler.decoration.activity

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import ando.repo.R
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ando.repo.ui.recycler.decoration.DataServer
import ando.repo.ui.recycler.decoration.adapter.DataAdapter
import ando.repo.ui.recycler.decoration.dpToPx
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider

class VerticalLinearActivity : BaseActivity() {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    override fun createAdapter(): RecyclerView.Adapter<BaseViewHolder> {
        return DataAdapter()
    }

    override fun addHeaderFooter(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as DataAdapter
        quickAdapter.addHeaderView(getView(R.layout.item_widget_ver_header))
        quickAdapter.addFooterView(getView(R.layout.item_widget_ver_footer))
    }

    override fun initItemDecoration(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        RecyclerDecorationProvider.linear()
            .color(Color.RED)
            .dividerSize(dpToPx(1f))
            .marginStart(dpToPx(20f))
            .hideDividerForItemType(BaseQuickAdapter.HEADER_VIEW)
            .hideAroundDividerForItemType(BaseQuickAdapter.FOOTER_VIEW)
            .build()
            .addTo(recyclerView)
    }

    override fun initData(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as DataAdapter
        quickAdapter.setList(DataServer.createLinearData(20).toMutableList())
    }

}
