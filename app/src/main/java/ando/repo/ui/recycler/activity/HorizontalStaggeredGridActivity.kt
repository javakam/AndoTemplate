package ando.repo.ui.recycler.activity

import ando.library.widget.recycler.BaseViewHolder
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ando.repo.ui.recycler.adapter.HorizontalDataAdapter
import ando.repo.ui.recycler.dpToPx
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider
import ando.repo.R

class HorizontalStaggeredGridActivity : BaseActivity() {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
    }

    override fun createAdapter(): RecyclerView.Adapter<BaseViewHolder> {
        return HorizontalDataAdapter()
    }

    override fun addHeaderFooter(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as HorizontalDataAdapter
        quickAdapter.addHeaderView(getView(R.layout.item_widget_ver_header))
        quickAdapter.addFooterView(getView(R.layout.item_widget_ver_footer))
    }

    override fun initItemDecoration(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<BaseViewHolder>) {
        RecyclerDecorationProvider.staggeredGrid()
            .spacingSize(dpToPx(10f))
            .includeEdge()
            .build()
            .addTo(recyclerView)
    }

    override fun initData(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as HorizontalDataAdapter
        val list = arrayListOf<String>()
        for (i in 0 until 5) {
            list.add("vertical")
            list.add("vertical staggered grid")
            list.add("vertical staggered")
            list.add("vertical staggered grid layout manager show. woo~~~")
            list.add("vertical staggered grid layout manager")
            list.add("vertical staggered grid layout manager show.")
        }
        quickAdapter.setNewData(list)
    }

}
