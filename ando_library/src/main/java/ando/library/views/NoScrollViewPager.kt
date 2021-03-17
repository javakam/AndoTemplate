package ando.library.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * # NoScrollViewPager 不能左右划的ViewPager
 *
 * ## 主要原理是利用Android系统的事件分发机制
 *
 * 1. 首先在Action_Down事件ViewGroup不能够拦截掉事件，也就是ViewPager不处理滑动事件
 * 2. 如果子View没有消费本次事件，那么事件通过冒泡方式传递到ViewPager的时候也不消费该事件；
 *
 * @author javakam
 * @date 2018/10/25  11:24
 */
class NoScrollViewPager constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {

    var isEnableScroll = false

    /**
     * 表示事件是否拦截, 返回false表示不拦截, 可以让嵌套在内部的recyclerview、viewpager相应左右滑动的事件不受干扰
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        if (isEnableScroll) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }

    /**
     * 重写onTouchEvent事件,什么都不用做
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (isEnableScroll) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }
}