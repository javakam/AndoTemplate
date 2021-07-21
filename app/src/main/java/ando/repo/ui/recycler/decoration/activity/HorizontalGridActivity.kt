package ando.repo.ui.recycler.decoration.activity

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import ando.repo.ui.recycler.decoration.DataServer
import ando.repo.ui.recycler.decoration.adapter.HorizontalDataAdapter
import ando.repo.ui.recycler.decoration.dpToPx
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider
import ando.repo.R

class HorizontalGridActivity : BaseActivity() {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this, 3,GridLayoutManager.HORIZONTAL,false)
    }

    override fun createAdapter(): RecyclerView.Adapter<BaseViewHolder> {
        return HorizontalDataAdapter()
    }

    override fun addHeaderFooter(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as HorizontalDataAdapter
        quickAdapter.addHeaderView(getView(R.layout.item_widget_hor_header),-1,LinearLayout.HORIZONTAL)
//        quickAdapter.addFooterView(getView(R.layout.item_hor_footer))
    }

    override fun initItemDecoration(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        RecyclerDecorationProvider.grid()
            .color(Color.BLUE)
//            .includeEdge()
            .dividerSize(dpToPx(10f))
//            .hideLastDivider()
            .hideDividerForItemType(BaseQuickAdapter.HEADER_VIEW, BaseQuickAdapter.FOOTER_VIEW)
            .build()
            .addTo(recyclerView)
    }

    override fun initData(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as HorizontalDataAdapter
        quickAdapter.setList(DataServer.createGridData(20))
    }

}
