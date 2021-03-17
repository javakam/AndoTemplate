package ando.toolkit

import kotlin.math.abs

/**
 * # 事件防抖
 *
 * 注: 不仅适用于 View , 其他控件如: MenuItem 同样适用
 *
 * 1. 既适用于单个`View`事件防抖, 也适用于`Adapter`中`ItemView`事件防抖
 * 2. 如果事件为跳转到新的`Activity`, 该`Activity`启动模型应为`android:launchMode="singleTop"`
 *
 * eg:
 * ```
 * //快速点击事件 (Quick click event)
 * val listener = object : NoShakeClickListener2() {
 *    override fun onFastClick(item: Any?) {
 *      super.onFastClick(item)
 *      Log.i("123", "onFastClick Click")
 *      (item as? MenuItem?)?.apply {
 *          val fg = fragmentArray.get(itemId)
 *          if (fg.isAdded) fg.refreshData()
 *      }
 *    }
 * }
 *
 * BottomNavigationView.setOnNavigationItemSelectedListener {
 *      switchPage(it.itemId)
 *      listener.proceedClick(it)
 *      true
 * }
 * ```
 */
open class NoShakeClickListener2 @JvmOverloads constructor(interval: Long = 500L) {

    private var mTimeInterval = 500L
    private var mLastClickTime: Long = 0   //最近一次点击的时间
    private var mLastClick: Any? = null    //最近一次点击的控件 View or MenuItem ...

    init {
        mTimeInterval = interval
    }

    fun proceedClick() {
        if (isFastClick(null, mTimeInterval)) onFastClick(null) else onSingleClick(null)
    }

    fun <T> proceedClick(item: T?) {
        if (isFastClick(item, mTimeInterval)) onFastClick(item) else onSingleClick(item)
    }

    /**
     * 是否是快速点击
     *
     * @param item      点击的控件 View or MenuItem ...
     * @param interval 时间间期（毫秒）
     * @return true:是，false:不是
     */
    private fun <T> isFastClick(item: T?, interval: Long): Boolean {
        val nowTime = System.currentTimeMillis()
        val timeInterval = abs(nowTime - mLastClickTime)
        return if (timeInterval < interval && item == mLastClick) {
            // 快速点击事件
            true
        } else {
            // 单次点击事件
            mLastClickTime = nowTime
            mLastClick = item
            false
        }
    }

    protected open fun onFastClick(item: Any?) {}
    protected open fun onSingleClick(item: Any?) {}
}