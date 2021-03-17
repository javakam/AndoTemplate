package ando.library.views.recycler

import android.view.View

/**
 * # 通用 RecyclerView 适配器
 *
 * @author javakam
 * @date 2017/9/10 18:30
 */
abstract class BaseRecyclerAdapter<T>(layoutResId: Int) : BaseAdapter<T, BaseViewHolder>(layoutResId, null) {
    override fun getViewHolder(view: View): BaseViewHolder = BaseViewHolder(view)
}