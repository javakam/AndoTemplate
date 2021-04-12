package ando.repo.ui.recycler.adapter

import ando.library.widget.recycler.BaseQuickAdapter
import ando.library.widget.recycler.BaseViewHolder
import ando.repo.R

/**
 * @author   zyyoona7
 * @version  v1.0
 * @since    2018/12/13.
 */
class DataAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_widget_data, null) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv_item_data, "${holder.adapterPosition}. $item")
    }
}