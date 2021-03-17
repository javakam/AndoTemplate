package ando.library.views.recycler

import ando.library.BuildConfig
import ando.toolkit.log.L
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * # RecyclerView 适配器
 *
 * https://github.com/JoanZapata/base-adapter-helper
 *
 * usage:
 *  ```kotlin
 *
 *  class CustomAdapter :BaseRecyclerAdapter<String>(R.layout.xxx, null) {
 *       override fun bindData(holder: BaseViewHolder, position: Int, item: String) {}
 *  }
 *
 *
 * class CustomAdapter2 : BaseAdapter<String, CustomHolder>(null) {
 *      override fun getViewHolder(view: View): CustomHolder = CustomHolder(view)
 *      override fun bindData(holder: CustomHolder, position: Int, item: String) {}
 * }
 *
 * class CustomHolder(v: View) : BaseViewHolder(v) {}
 *  ```
 *
 * @author javakam
 * @date 2019-08-11 16:12
 */
abstract class BaseAdapter<T, VH : BaseViewHolder> : RecyclerView.Adapter<VH> {

    interface OnItemLongClickListener {
        fun onItemLongClick(viewHolder: BaseViewHolder, position: Int): Boolean
    }

    interface OnItemClickListener {
        fun onItemClick(viewHolder: BaseViewHolder, position: Int)
    }

    private var mLayoutResId: Int = 0
    private var mData: MutableList<T> = mutableListOf()
    private var mClickListener: OnItemClickListener? = null
    private var mLongClickListener: OnItemLongClickListener? = null

    constructor(data: List<T>?) : this(0, data)

    constructor(@LayoutRes layoutResId: Int) : this(layoutResId, null)

    constructor(@LayoutRes layoutResId: Int, @Nullable data: List<T>?) {
        mData = data?.toMutableList() ?: mutableListOf()
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId
        }
    }

    /**
     * 自定义的ViewHolder
     */
    protected abstract fun getViewHolder(view: View): VH

    protected abstract fun bindData(holder: VH, position: Int, item: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = getViewHolder(
            LayoutInflater.from(parent.context).inflate(mLayoutResId, parent, false)
        )
        if (mClickListener != null) {
            holder.itemView.setOnClickListener {
                if (BuildConfig.DEBUG) {
                    L.i(
                        "${javaClass.simpleName} ItemClick: " +
                                "adapterPosition:${holder.adapterPosition} layoutPosition:${holder.layoutPosition} " +
                                "oldPosition:${holder.oldPosition} absolutePosition:${holder.absolutePosition} " +
                                "relativePosition:${holder.relativePosition} position:${holder.position}"
                    )
                }
                val position: Int = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                mClickListener?.onItemClick(holder, position)
            }
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener {
                val position: Int = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    false
                } else {
                    mLongClickListener?.onItemLongClick(holder, position)
                    true
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        bindData(holder, position, mData[position])
    }

    override fun getItemCount(): Int = (mData.size)

    fun getData(): List<T> = mData

    fun getItem(position: Int): T? =
        if (position >= 0 && position < mData.size) mData[position] else null

    fun isEmpty(): Boolean = (itemCount == 0)

    /**
     * 给指定位置添加一项
     */
    open fun addData(@IntRange(from = 0L) position: Int, @NonNull data: T?) {
        data?.apply {
            mData.add(position, this)
            notifyItemInserted(position)
        }
    }

    /**
     * 在列表末端增加一项
     */
    open fun addData(data: T?) {
        data?.apply {
            mData.add(this)
            notifyItemInserted(mData.size - 1)
        }
    }

    /**
     * 删除列表中指定索引的数据
     */
    open fun remove(@IntRange(from = 0L) position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mData.size - position)
    }

    /**
     * 刷新列表中指定位置的数据
     */
    open fun setData(@IntRange(from = 0) index: Int, @NonNull data: T?) {
        data?.apply {
            mData[index] = this
            notifyItemChanged(index)
        }
    }

    /**
     * 将新数据添加到特定位置
     */
    open fun addData(
        @IntRange(from = 0) position: Int,
        @NonNull newData: Collection<T>?
    ) {
        if (!newData.isNullOrEmpty()) {
            mData.addAll(position, newData)
            notifyItemRangeInserted(position, newData.size)
        }
    }

    /**
     * 将新数据添加到 mData 的末尾 eg: 加载更多
     */
    open fun addData(newData: Collection<T>?) {
        if (!newData.isNullOrEmpty()) {
            mData.addAll(newData)
            notifyDataSetChanged()
            //notifyItemRangeInserted(mData.size - newData.size, newData.size)
        }
    }

    /**
     * 使用数据替换 mData 中的所有项目
     */
    open fun replaceData(data: Collection<T>?) {
        // 不是同一个引用才清空列表
        if (data !== mData && !data.isNullOrEmpty()) {
            mData.clear()
            mData.addAll(data)
            notifyDataSetChanged()
        }
    }

    /**
     * 为数据设置一个新实例
     */
    open fun setNewData(@Nullable data: List<T>?) {
        mData = data?.toMutableList() ?: mutableListOf()
        notifyDataSetChanged()
    }

    /**
     * 设置列表项点击监听
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mClickListener = listener
    }

    /**
     * 设置列表项长按监听
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        mLongClickListener = listener
    }

    /**
     * 清除数据
     */
    fun clear() {
        if (!isEmpty()) {
            mData.clear()
            notifyDataSetChanged()
        }
    }

}