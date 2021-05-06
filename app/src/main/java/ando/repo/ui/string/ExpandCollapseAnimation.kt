package ando.repo.ui.string

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ExpandCollapseAnimation(
    private val mTargetView: View, //动画执行view
    private val mStartHeight: Int, //动画执行的开始高度
    private val mEndHeight: Int,   //动画结束后的高度
) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        //TextView 收起动画异常 https://juejin.cn/post/6844903952757047309
        mTargetView.scrollY = 0
        //applyTransformation()方法就是动画具体的实现，每隔一段时间会调用此方法
        //计算出每次应该显示的高度, 通过剬改变执行view的高度，实现动画
        mTargetView.layoutParams.height = ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight).toInt()
        mTargetView.requestLayout()
    }

    init {
        duration = 400
    }
}