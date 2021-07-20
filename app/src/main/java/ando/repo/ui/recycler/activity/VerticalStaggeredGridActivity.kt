package ando.repo.ui.recycler.activity

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ando.repo.ui.recycler.adapter.DataAdapter
import ando.repo.ui.recycler.dpToPx
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider
import ando.repo.R

class VerticalStaggeredGridActivity : BaseActivity() {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
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
        RecyclerDecorationProvider.staggeredGrid()
            .spacingSize(dpToPx(10f))
            .includeEdge()
            .build()
            .addTo(recyclerView)
    }

    override fun initData(adapter: RecyclerView.Adapter<BaseViewHolder>) {
        val quickAdapter = adapter as DataAdapter
        val list= arrayListOf<String>()
        for (i in 0 until 5){
            list.add("vertical")
            list.add("vertical staggered grid")
            list.add("vertical staggered")
            list.add("vertical staggered grid layout manager show. woo~~~")
            list.add("vertical staggered grid layout manager")
            list.add("vertical staggered grid layout manager show.")
        }
        quickAdapter.setList(list)
    }

}
