package ando.library.widget.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * # RecyclerItemDecoration
 *
 * ### 使用方法:
 * #### 添加默认分割线：高度为2px，颜色为灰色
 * ```
 * mRecyclerView.addItemDecoration(new RecyclerViewDividerItemDecoration(mContext, LinearLayoutManager.HORIZONTAL));
 * ```
 *
 * #### 添加自定义分割线：可自定义分割线drawable
 * ```
 * mRecyclerView.addItemDecoration(new RecyclerViewDividerItemDecoration(
 * mContext, LinearLayoutManager.HORIZONTAL, R.drawable.divider_mileage));
 * ```
 *
 * #### 添加自定义分割线：可自定义分割线高度和颜色
 * ```
 * mRecyclerView.addItemDecoration(new RecyclerViewDividerItemDecoration(
 * mContext, LinearLayoutManager.HORIZONTAL, 10, getResources().getColor(R.color.divide_gray_color)));
 * ```
 *
 * @author javakam
 * @date 2019/11/22  9:35
 */
open class DrawItemDecoration(context: Context, orientation: Int) : ItemDecoration() {

    private var mPaint: Paint? = null
    private var mDivider: Drawable? = null

    /**
     * 分割线高度，默认为1px
     */
    private var mDividerHeight = 1

    /**
     * 列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
     */
    private val mOrientation: Int

    /**
     * 自定义分割线
     *
     * @param orientation 列表方向
     * @param drawableId  分割线图片
     */
    constructor(context: Context, orientation: Int, drawableId: Int) : this(context, orientation) {
        mDivider = ContextCompat.getDrawable(context, drawableId)
        mDividerHeight = mDivider?.intrinsicHeight ?: 0
    }

    /**
     * 自定义分割线
     *
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    constructor(context: Context, orientation: Int, dividerHeight: Int, dividerColor: Int) : this(
        context,
        orientation
    ) {
        mDividerHeight = dividerHeight
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.color = dividerColor
        mPaint?.style = Paint.Style.FILL
    }

    /**
     * 获取分割线尺寸
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //设置偏移的高度是mDivider.getIntrinsicHeight，该高度正是分割线的高度
        outRect[0, 0, 0] = mDividerHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    /**
     * 横向分割线
     */
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft       //获取分割线的左边距，即RecyclerView的padding值
        val right = parent.measuredWidth - parent.paddingRight //分割线右边距

        val childSize = parent.childCount
        //遍历所有item view，为它们的下方绘制分割线
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + mDividerHeight
            if (mDivider != null) {
                mDivider?.setBounds(left, top, right, bottom)
                mDivider?.draw(canvas)
            }
            mPaint?.let {
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    it
                )
            }
        }
    }

    /**
     * 纵向分割线
     */
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + mDividerHeight
            if (mDivider != null) {
                mDivider?.setBounds(left, top, right, bottom)
                mDivider?.draw(canvas)
            }
            mPaint?.let {
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    it
                )
            }
        }
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     */
    init {
        require(!(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL))
        { "Please enter the correct parameters！" }

        mOrientation = orientation
        val attrs = intArrayOf(android.R.attr.listDivider)  //使用系统自带的listDivider
        val a = context.obtainStyledAttributes(attrs)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

}