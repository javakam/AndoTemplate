package ando.repo.ui.recycler

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import ando.repo.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ando.repo.ui.recycler.activity.*
import ando.library.widget.recycler.decoration.RecyclerDecorationProvider
import android.view.View
import com.chad.library.adapter.base.listener.OnItemClickListener

/**
 * # RecyclerActivity
 *
 * @author javakam
 * @date 2021/4/12  10:48
 */
class RecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_recycler)

        val mainRv = findViewById<RecyclerView>(R.id.rv_main)

        mainRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = MainAdapter()
        mainRv.adapter = adapter

        RecyclerDecorationProvider.staggeredGrid()
            .includeEdge()
            .includeStartEdge()
            .spacingSize(dpToPx(10f))
            .build()
            .addTo(mainRv)

        adapter.run {
            mainRv.adapter = this

            RecyclerDecorationProvider.staggeredGrid()
                .includeEdge()
                .includeStartEdge()
                .spacingSize(dpToPx(10f))
                .build()
                .addTo(mainRv)

            setList(createData())

            setOnItemClickListener { _, _, position ->
                when (position) {
                    0    -> start(VerticalLinearActivity::class.java)
                    1    -> start(HorizontalLinearActivity::class.java)
                    2    -> start(VerticalGridActivity::class.java)
                    3    -> start(HorizontalGridActivity::class.java)
                    4    -> start(VerticalStaggeredGridActivity::class.java)
                    5    -> start(HorizontalStaggeredGridActivity::class.java)
                    else -> {
                        start(VerticalLinearActivity::class.java)
                    }
                }
            }
        }
    }

    private fun <T> start(actClass: Class<T>) {
        startActivity(Intent(this, actClass))
    }

    private fun createData(): List<String> {
        val list = arrayListOf<String>()
        list.add("Vertical Linear")
        list.add("Horizontal Linear")
        list.add("Vertical Grid")
        list.add("Horizontal Grid")
        list.add("Vertical Staggered Grid")
        list.add("Horizontal Staggered Grid")
        return list
    }

    class MainAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_widget_main, null) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.tv_item_data, "${holder.absoluteAdapterPosition}. $item")
        }
    }
}