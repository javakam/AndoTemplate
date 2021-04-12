package ando.repo.ui.recycler.activity

import ando.library.widget.recycler.BaseQuickAdapter
import ando.library.widget.recycler.BaseViewHolder
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ando.repo.ui.recycler.DataServer
import ando.repo.ui.recycler.adapter.HorizontalDataAdapter
import ando.repo.ui.recycler.dpToPx
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider
import ando.repo.R

class HorizontalLinearActivity : BaseActivity() {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun createAdapter(): RecyclerView.Adapter<BaseViewHolder> {
        return HorizontalDataAdapter()
    }

    override fun addHeaderFooter(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as HorizontalDataAdapter
        quickAdapter.addHeaderView(getView(R.layout.item_widget_hor_header))
        quickAdapter.addFooterView(getView(R.layout.item_widget_hor_footer))
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
        val quickAdapter = adapter as HorizontalDataAdapter
        quickAdapter.setNewData(DataServer.createLinearData(20))
    }

}
