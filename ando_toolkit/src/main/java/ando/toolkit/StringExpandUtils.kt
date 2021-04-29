package ando.toolkit

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt

/**
 * # StringExpandUtils
 *
 * Description:
 *      StaticLayout 源码分析 https://jaeger.itscoder.com/android/2016/08/05/staticlayout-source-analyse.html
 *      Android TextView实现查看全部和收起功能 https://www.jianshu.com/p/838d407e0df0
 *
 * @author Changbao
 * @date 2020/5/10  11:06
 */
object StringExpandUtils {

    /**
     * Array<SpannableString> 第一个元素 notExpandString展开的内容  ; 第二个元素 expandString收起的内容
     *
     * endText "...[ 详情 ]"   "...查看全部"
     *
     * expandHeight 计算得出 ContentView 最后展开的高度
     */
    fun obtain(
        tv: TextView, maxLine: Int, content: String,
        upText: String?, endText: String, @ColorInt endTextColor: Int, endTextBoundWithColor: Int,
        callBack: ((foldString: SpannableString, expandString: SpannableString, foldHeight: Int) -> Unit)? = null
    ) {
        if (content.isBlank() || endText.isBlank()) {
            return
        }
        //获取TextView的画笔对象
        val paint = tv.paint
        //每行文本的布局宽度
//      val width = context.resources.displayMetrics.widthPixels - (40F * tv.context.resources.displayMetrics.density + 0.5f).toInt()
        val width: Int = tv.context.resources.displayMetrics.run {
            this.widthPixels - (Layout.getDesiredWidth(endText, paint) * this.density + 0.5f).toInt()
        }
        //实例化StaticLayout 传入相应参数
        val staticLayout: StaticLayout = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(content, 0, content.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0F, 1F)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                content,
                paint,
                width,
                Layout.Alignment.ALIGN_NORMAL,
                1F,
                0F,
                false
            )
        }

        //判断content是行数是否超过最大限制行数3行
        if (staticLayout.lineCount > maxLine) {
            //定义展开后的文本内容
            val expandString = if (upText.isNullOrBlank()) {
                SpannableString("")
            } else {
                val upString = "$content  $upText"
                //"收起" 设成蓝色
                SpannableString(upString).apply {
                    setSpan(
                        ForegroundColorSpan(endTextColor),
                        content.length,
                        upString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            //获取到 maxLine 最后一个文字的下标
            val index = staticLayout.getLineStart(maxLine) - 1
            //"查看全部"
            //String substring = content.substring(0, index - 5) + "..." + "查看全部";
            val substring = content.substring(0, index - endText.length) + endText
            val foldString = SpannableString(substring)

            //https://www.jianshu.com/p/71ae03df679d
            var clickHandled = false
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    clickHandled = true // 标记为已处理

                    //计算得出 ContentView 最后展开的高度
                    val foldHeight = staticLayout.height + tv.paddingTop + tv.paddingBottom
                    callBack?.invoke(foldString, expandString, foldHeight)
                }
            }
            foldString.setSpan(
                clickableSpan, substring.length - endTextBoundWithColor,
                substring.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            //给"查看全部"设成蓝色
            foldString.setSpan(
                ForegroundColorSpan(endTextColor), substring.length - endTextBoundWithColor,
                substring.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //设置收起后的文本内容
            tv.text = foldString
            tv.setOnClickListener(object : SimpleNoShakeListener() {
                override fun onSingleClick(v: View) {
                    if (clickHandled) { // 若已处理则直接返回
                        clickHandled = false
                        return
                    }
                    //计算得出 ContentView 最后展开的高度
                    val foldHeight = staticLayout.height + tv.paddingTop + tv.paddingBottom
                    callBack?.invoke(foldString, expandString, foldHeight)
                }
            })

            //将 TextView 设成选中状态
            //false 用来表示文本未展示完全的状态, true 表示完全展示状态，用于点击时的判断
            tv.isSelected = true
        } else {
            //没有超过 直接设置文本
            tv.text = content
            tv.setOnClickListener(null)
        }
    }

}