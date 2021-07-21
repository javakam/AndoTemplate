package ando.repo.ui.recycler.decoration.activity

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ando.repo.ui.recycler.decoration.DataServer
import ando.repo.ui.recycler.decoration.adapter.DataAdapter
import ando.repo.ui.recycler.decoration.dpToPx
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider
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
        RecyclerDecorationProvider.grid()
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
        quickAdapter.setList(DataServer.createGridData(21))
    }

}
