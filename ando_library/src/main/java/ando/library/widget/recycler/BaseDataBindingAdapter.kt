package ando.library.widget.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseDataBindingAdapter<T, V : ViewDataBinding> :
    RecyclerView.Adapter<BaseDataBindingViewHolder<V>>() {

    private val mData = ArrayList<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseDataBindingViewHolder<V> {
        return BaseDataBindingViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(
        holder: BaseDataBindingViewHolder<V>,
        position: Int
    ) {
        initView(holder.binding, mData[position])
        holder.binding.executePendingBindings()
    }

    abstract val layoutId: Int
    abstract fun initView(binding: V, entity: T)

    fun getData(): List<T> = mData

    fun addData(list: List<T>?) {
        list?.let {
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun setData(list: List<T>?) {
        list?.let {
            mData.clear()
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun setData(list: List<T>?, isFirstPage: Boolean) {
        list?.let {
            if (isFirstPage) mData.clear()
            mData.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun updateItem(position: Int, entity: T) {
        if (position <= 0 && position < mData.size) {
            mData[position] = entity
        }
        notifyItemChanged(position, 1)
    }
}