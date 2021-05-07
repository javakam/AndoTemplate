package ando.repo.ui.string

import ando.repo.R
import ando.toolkit.ExpandableTextViewUtils
import ando.toolkit.StringExpandUtils
import ando.toolkit.ext.toastShort
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.scwang.smart.refresh.layout.util.SmartUtil.dp2px


class StringExpandActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_string_expand)

        sample1()  //字符串后面直接拼接
        sample2()  //展开/收起 & 无动画
        sample3()  //展开/收起 & 动画
        sample4()  //ExpandableTextView
    }

    /**
     * 字符串后面直接拼接
     */
    private fun sample1() {
        val tvNoExpand: TextView = findViewById(R.id.tv_string_expand_no_expand)

        val upTextColor = Color.RED
        val endTextColor = Color.BLUE
        StringExpandUtils
            .obtain(
                tvNoExpand,
                4,
                strContent,
                "",
                upTextColor,
                "...查看全部",
                endTextColor,
                4,
                false,
                object : StringExpandUtils.OnClickListener {
                    override fun onSpanClick(
                        foldString: SpannableString,
                        expandString: SpannableString,
                        foldHeight: Int
                    ) {
                    }

                    override fun onViewClick(v: View) {
                    }
                })
    }

    /**
     * 展开/收起 & 无动画
     */
    private fun sample2() {
        val tvNoAnimation: TextView = findViewById(R.id.tv_string_expand_no_animation)

        val upTextColor = Color.RED
        val endTextColor = Color.BLUE
        StringExpandUtils
            .obtain(
                tvNoAnimation,
                3,
                strContent,
                "...收起",
                upTextColor,
                " ...查看全部",
                endTextColor,
                0,
                true,
                object : StringExpandUtils.OnClickListener {
                    override fun onSpanClick(
                        foldString: SpannableString,
                        expandString: SpannableString,
                        foldHeight: Int
                    ) {
                        if (tvNoAnimation.isSelected) {
                            //如果是收起的状态
                            tvNoAnimation.text = expandString
                            tvNoAnimation.isSelected = false
                        } else {
                            //如果是展开的状态
                            tvNoAnimation.text = foldString
                            tvNoAnimation.isSelected = true
                        }
                    }

                    override fun onViewClick(v: View) {
                        toastShort("onViewClick")
                    }
                }
            )
    }

    //防抖
    private var lastClickTime = 0L
    private val MIN_CLICK_DELAY_TIME = 500L

    /**
     * 展开/收起 & 动画
     */
    private fun sample3() {
        val tv: TextView = findViewById(R.id.tv_string_expand)
        var expandHeight = 0

        val upTextColor = Color.RED
        val endTextColor = Color.BLUE
        StringExpandUtils
            .obtain(
                tv,
                4,
                strContent,
                "...收起",
                upTextColor,
                "...查看全部",
                endTextColor,
                3,
                true,
                object : StringExpandUtils.OnClickListener {
                    override fun onSpanClick(
                        foldString: SpannableString,
                        expandString: SpannableString,
                        foldHeight: Int
                    ) {
                        val currentTime: Long = System.currentTimeMillis()
                        val isFastClick = (currentTime - lastClickTime < MIN_CLICK_DELAY_TIME)
                        Log.w("123", "两次事件间隔时间: ${currentTime - lastClickTime}  isFastClick = $isFastClick")
                        lastClickTime = currentTime
                        if (isFastClick) return

                        Log.e("123", "${tv.isSelected}  $expandHeight  ${tv.height}")

                        val animation: ExpandCollapseAnimation?
                        if (tv.isSelected) {
                            //收起 -> 展开
                            //因为现在是收起的状态，所以可以得到 contentView 开始执行动画的高度
                            expandHeight = tv.height

                            animation = ExpandCollapseAnimation(tv, expandHeight, foldHeight)
                            animation.fillAfter = true
                            animation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {
                                    //将 tv 高度设置为 textview 的高度，以此让textview是一行一行的展示
                                    tv.layoutParams.height = expandHeight
                                    tv.requestLayout()
                                    tv.text = expandString
                                    tv.isSelected = false
                                }

                                override fun onAnimationEnd(animation: Animation?) {}
                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                        } else {
                            //展开 -> 收起
                            animation = ExpandCollapseAnimation(tv, foldHeight, expandHeight)
                            animation.fillAfter = true
                            animation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) {
                                    tv.layoutParams.height = foldHeight
                                    tv.requestLayout()
                                    // 动画结束后 TextView 设置展开的状态
                                    tv.text = foldString
                                    tv.isSelected = true
                                }

                                override fun onAnimationRepeat(animation: Animation?) {}
                            })
                        }

                        tv.clearAnimation()
                        tv.startAnimation(animation)
                    }

                    override fun onViewClick(v: View) {
                    }
                }
            )
    }

    private fun sample4() {
        val expandableTextView = findViewById<TextView>(R.id.expanded_text)
        val expandUtil = ExpandableTextViewUtils.obtain(expandableTextView)

        @Suppress("DEPRECATION")
        val viewWidth = windowManager.defaultDisplay.width - dp2px(20F)
        expandUtil.initWidth(viewWidth)
            .setMaxLines(2)
            .setOpenOrCloseByUserHandle(true)//自定义控制事件
            .setHasAnimation(true)
            .setCloseInNewLine(true) //true 新插入一行显示"收起"按钮
            .setOpenSuffixColor(Color.RED)
            .setCloseSuffixColor(Color.BLUE)
            .setOpenSuffix(" 查看全部")
            .setCloseSuffix(" 收起")
            .setOriginalText(strContent)
            .setOnClickListener(object : ExpandableTextViewUtils.OnClickListener {
                override fun onOpenClick() {
                    expandUtil.switchOpenClose()
                }

                override fun onCloseClick() {
                    expandUtil.switchOpenClose()
                }

                override fun onViewClick(v: View) {
                }
            })

    }

    companion object {
        private const val strContent =
            "Android 控件中，看起来最简单、最基础的 TextView 实际上是很复杂的，很多常见的控件都是其子类，例如 Botton、EditText、CheckBox 等，由于作为一个基础控件类，TextView 需要考虑到子类的各种使用场景，满足子类的需求。源码中，TextView 单个类源码就多达 1万行，而且其工作时还依赖很多辅助类。其文本的排版、折行处理，以及最终的显示，均是交给辅助类 Layout 类来处理的。" +
                    "由于 Canvas 本身提供的 drawText 绘制文本是不支持换行的，所以在文本需要换行显示时，就需要用到 Layout 类。我们可以看到官方对 Layout 类的描述" +
                    "由于 Canvas 本身提供的 drawText 绘制文本是不支持换行的，所以在文本需要换行显示时，就需要用到 Layout 类。我们可以看到官方对 Layout 类的描述 END"

    }

}