package ando.repo.ui.recycler.activity

import ando.library.widget.recycler.BaseQuickAdapter
import ando.library.widget.recycler.BaseViewHolder
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ando.repo.ui.recycler.DataServer
import ando.repo.ui.recycler.adapter.DataAdapter
import ando.repo.ui.recycler.dpToPx
import ando.library.widget.recycler.decoration.DecorationProvider
import ando.repo.R

class VerticalGridActivity : BaseActivity() {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this, 4)
    }

    override fun createAdapter(): RecyclerView.Adapter<BaseViewHolder> {
        return DataAdapter()
    }

    override fun addHeaderFooter(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as DataAdapter
        quickAdapter.addHeaderView(getView(R.layout.item_widget_ver_header))
//        quickAdapter.addFooterView(getView(R.layout.item_ver_footer))
    }

    override fun initItemDecoration(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        DecorationProvider.grid()
            .color(Color.BLUE)
            .dividerSize(dpToPx(10f))
//            .includeEdge()
//            .hideLastDivider()
            .hideDividerForItemType(BaseQuickAdapter.HEADER_VIEW, BaseQuickAdapter.FOOTER_VIEW)
            .build()
            .addTo(recyclerView)
    }

    override fun initData(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as DataAdapter
//        quickAdapter.setSpanSizeLookup { gridLayoutManager, position ->
//            2 }
        quickAdapter.setNewData(DataServer.createGridData(21))
    }

}
