package ando.library.widget.recycler

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 *
 * @author javakam
 * @date 2021/3/24  12:21
 */

interface BaseAnimation {
    fun animators(view: View): Array<Animator>
}

class AlphaInAnimation @JvmOverloads constructor(private val mFrom: Float = 0F) : BaseAnimation {
    override fun animators(view: View): Array<Animator> {
        val animator = ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f)
        animator.duration = 300L
        animator.interpolator = LinearInterpolator()
        return arrayOf(animator)
    }
}

interface GridSpanSizeLookup {
    fun getSpanSize(
        @NonNull gridLayoutManager: GridLayoutManager?,
        viewType: Int,
        position: Int
    ): Int
}

interface OnItemChildClickListener {
    /**
     * callback method to be invoked when an item child in this view has been click
     *
     * @param adapter  BaseQuickAdapter
     * @param view     The view whihin the ItemView that was clicked
     * @param position The position of the view int the adapter
     */
    fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}

interface OnItemChildLongClickListener {
    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param adapter  this BaseQuickAdapter adapter
     * @param view     The childView whihin the itemView that was clicked and held.
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean
}

interface OnItemClickListener {
    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adapter
     * @param view     The itemView within the RecyclerView that was clicked (this
     * will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)
}

interface OnItemLongClickListener {
    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param adapter  the adapter
     * @param view     The view whihin the RecyclerView that was clicked and held.
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    fun onItemLongClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int): Boolean
}