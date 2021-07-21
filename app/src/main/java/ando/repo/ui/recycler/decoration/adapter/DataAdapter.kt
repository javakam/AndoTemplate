package ando.repo.ui.recycler.decoration.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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