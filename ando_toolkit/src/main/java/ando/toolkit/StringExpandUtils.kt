package ando.toolkit

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt

/**
 * # StringExpandUtils
 *
 * - StaticLayout 源码分析  https://jaeger.itscoder.com/android/2016/08/05/staticlayout-source-analyse.html
 *
 * - 收起动画异常  https://juejin.cn/post/6844903952757047309
 *
 * @author javakam
 * @date 2020/5/10  11:06
 */
object StringExpandUtils {

    interface OnClickListener {
        fun onSpanClick(foldString: SpannableString, expandString: SpannableString, foldHeight: Int)
        fun onViewClick(v: View)
    }

    /**
     * Array<SpannableString> 第一个元素 notExpandString展开的内容  ; 第二个元素 expandString收起的内容
     *
     * endText "...[ 详情 ]"   "...展开"
     *
     * expandHeight 计算得出 ContentView 最后展开的高度
     */
    fun obtain(
        tv: TextView,
        maxLine: Int,
        content: String,
        upText: String?,
        @ColorInt upTextColor: Int,
        endText: String?,
        @ColorInt endTextColor: Int,
        endTextColorBound: Int,
        isDebug: Boolean = false,
        listener: OnClickListener? = null
    ) {
        if (content.isBlank() || endText.isNullOrBlank()) {
            return
        }
        //获取TextView的画笔对象
        val paint = tv.paint
        //endText 宽度测量 https://juejin.cn/post/6956801334930571294/
        val width: Int = tv.context.resources.displayMetrics.run {
            // (40F * tv.context.resources.displayMetrics.density + 0.5f).toInt()
            //this.widthPixels - Layout.getDesiredWidth(endText, paint).toInt() - tv.paddingLeft - tv.paddingRight
            this.widthPixels - 130
        }

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

        val realEndTextColorBound = if (endTextColorBound > 0) endTextColorBound else endText.length

        //判断 content 是行数是否超过最大限制行数 maxLine 行
        if (isDebug) {
            Log.i("StringExpand", "staticLayout.lineCount = ${staticLayout.lineCount} maxLine = $maxLine \n")
        }
        if (staticLayout.lineCount > maxLine) {
            //"收起" 后文本内容
            val upString = "$content$upText"
            val expandString: SpannableString = if (!upText.isNullOrBlank()) {
                SpannableString(upString).apply {
                    setSpan(
                        ForegroundColorSpan(upTextColor), content.length, upString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else SpannableString("")

            //"展开"
            //计算原文截取位置
            val index = staticLayout.getLineStart(maxLine) - 1
            //val index = staticLayout.getLineEnd(maxLine - 1)
            val substring = content.substring(0, index - endText.length) + endText
            val foldString = SpannableString(substring).apply {
                setSpan(
                    ForegroundColorSpan(endTextColor), substring.length - realEndTextColorBound, substring.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (isDebug) {
                Log.i(
                    "StringExpand",
                    "getLineStart=${staticLayout.getLineStart(maxLine)} " +
                            "getLineEnd=${staticLayout.getLineEnd(maxLine)} " +
                            "index = $index \n\n" +
                            "$content \n\n  $substring \n\n  $upString"
                )
            }

            //设置监听
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (isDebug) {
                        Log.w("StringExpand", "foldString=$foldString \n expandString=$expandString")
                    }
                    //计算出"展开/收起"后的高度
                    val foldHeight = staticLayout.height + tv.paddingTop + tv.paddingBottom
                    listener?.onSpanClick(foldString, expandString, foldHeight)
                }

                override fun updateDrawState(ds: TextPaint) {
                    //super.updateDrawState(ds)
                    //ds?.color = ds?.linkColor //解决 ClickableSpan 和 ForegroundColorSpan 颜色冲突问题
                    ds.isUnderlineText = false //去掉下划线
                }
            }
            //"展开"监听
            foldString.setSpan(
                clickableSpan, substring.length - realEndTextColorBound, substring.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //"收起"监听
            if (!upText.isNullOrBlank()) {
                expandString.setSpan(clickableSpan, content.length, upString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            //使 ClickableSpan 生效
            tv.movementMethod = LinkMovementMethod.getInstance()

            //设置收起后的文本内容
            tv.text = foldString
            tv.setOnClickListener(object : SimpleNoShakeListener() {
                override fun onSingleClick(v: View) {
                    listener?.onViewClick(v)
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