package ando.library.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import java.util.*

/**
 * <ando.xxx.ObservableScrollView
 *      android:id="@+id/observableScrollView"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"
 *      android:background="@color/white"
 *      android:descendantFocusability="blocksDescendants"
 *      android:fillViewport="true"
 *      android:overScrollMode="never"
 *      android:paddingBottom="@dimen/dp_40"
 *      android:scrollbars="vertical">
 * </ando.library.views.ObservableScrollView>
 */
class ObservableScrollView : ScrollView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    )

    private var mScrollOffset = 0
    private var mOnScrollChangedListeners: MutableList<OnScrollChangedListener>? = null

    /**
     * 添加滚动监听
     */
    fun addOnScrollChangedListener(onScrollChangedListener: OnScrollChangedListener) {
        if (mOnScrollChangedListeners == null) {
            mOnScrollChangedListeners = ArrayList()
        }
        if (mOnScrollChangedListeners?.contains(onScrollChangedListener) == false) {
            return
        }
        mOnScrollChangedListeners?.add(onScrollChangedListener)
    }

    fun removeOnScrollChangedListener(onScrollChangedListener: OnScrollChangedListener) {
        mOnScrollChangedListeners?.remove(onScrollChangedListener)
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        mScrollOffset = y
        mOnScrollChangedListeners?.forEach {
            it.onScrollChanged(this@ObservableScrollView, x, y, oldx, oldy)
        }
    }

    fun getScrollOffset(): Int {
        return mScrollOffset
    }

    interface OnScrollChangedListener {
        /**
         * 滚动发生变化
         *
         * @param scrollView
         * @param x 变化后的X轴位置
         * @param y 变化后的Y轴的位置
         * @param oldx 原先的X轴的位置
         * @param oldy 原先的Y轴的位置
         */
        fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int)
    }

}