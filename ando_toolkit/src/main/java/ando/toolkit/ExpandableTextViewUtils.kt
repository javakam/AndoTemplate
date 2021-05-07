package ando.toolkit

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.NoCopySpan.Concrete
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import androidx.annotation.ColorInt

/**
 * https://github.com/MrTrying/ExpandableText-Example
 *
 * https://juejin.cn/post/6844903952757047309
 */
class ExpandableTextViewUtils private constructor(private val mTextView: TextView) {

    companion object {
        val ELLIPSIS_STRING = String(charArrayOf('\u2026'))
        private const val DEFAULT_MAX_LINE = 3
        private const val DEFAULT_OPEN_SUFFIX = " 展开"
        private const val DEFAULT_CLOSE_SUFFIX = " 收起"
        fun obtain(textView: TextView): ExpandableTextViewUtils {
            return ExpandableTextViewUtils(textView)
        }
    }

    @Volatile
    var animating = false
    var isClosed = false
    private var mMaxLines: Int = DEFAULT_MAX_LINE

    /**
     * TextView可展示宽度，包含paddingLeft和paddingRight
     */
    private var initWidth: Int = 0
    private var mOpenSpannableStr: SpannableStringBuilder? = null
    private var mCloseSpannableStr: SpannableStringBuilder? = null
    private var isOpenOrCloseByUserHandle = false //false 自动触发"展开/收起";true 自定义事件控制
    private var hasAnimation = false
    private var mOpenAnim: Animation? = null
    private var mCloseAnim: Animation? = null
    private var mOpenHeight: Int = 0
    private var mCloseHeight: Int = 0
    private var mExpandable = false
    private var mCloseInNewLine = false
    private var mOpenSuffixSpan: SpannableString? = null
    private var mCloseSuffixSpan: SpannableString? = null
    private var mOpenSuffixStr = DEFAULT_OPEN_SUFFIX
    private var mCloseSuffixStr = DEFAULT_CLOSE_SUFFIX
    private var mOpenSuffixColor: Int = 0
    private var mCloseSuffixColor: Int = 0
    private var mCharSequenceToSpannableHandler: CharSequenceToSpannableHandler? = null
    private var mStateListener: OnStateListener? = null
    private var mClickListener: OnClickListener? = null

    init {
        mCloseSuffixColor = Color.parseColor("#F23030")
        mOpenSuffixColor = mCloseSuffixColor
        mTextView.movementMethod = OverLinkMovementMethod.instance
        mTextView.includeFontPadding = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTextView.forceHasOverlappingRendering(false)
        }
    }

    fun setOriginalText(originalText: CharSequence): ExpandableTextViewUtils {
        this.mExpandable = false

        updateOpenSuffixSpan()
        updateCloseSuffixSpan()

        mCloseSpannableStr = SpannableStringBuilder()
        mOpenSpannableStr = charSequenceToSpannable(originalText)
        if (mMaxLines != -1) {
            val layout: StaticLayout = createStaticLayout()
            this.mExpandable = layout.lineCount > mMaxLines

            if (mExpandable) {
                //拼接展开内容
                if (mCloseInNewLine) {
                    mOpenSpannableStr?.append("\n")
                }
                if (mCloseSuffixSpan != null) {
                    mOpenSpannableStr?.append(mCloseSuffixSpan)
                }
                //计算原文截取位置
                val endPos = layout.getLineStart(mMaxLines - 1)
                mCloseSpannableStr = if (originalText.length <= endPos) {
                    charSequenceToSpannable(originalText)
                } else {
                    charSequenceToSpannable(originalText.subSequence(0, endPos))
                }
                if (mOpenSuffixSpan != null) {
                    charSequenceToSpannable(mCloseSpannableStr).append(ELLIPSIS_STRING).append(mOpenSuffixSpan)
                }
                var lastSpace: Int = mCloseSpannableStr?.length ?: 0 - (mOpenSuffixSpan?.length ?: 0)
                if (lastSpace >= 0 && originalText.length > lastSpace) {
                    val redundantChar = originalText.subSequence(lastSpace, lastSpace + (mOpenSuffixSpan?.length ?: 0))
                    val offset = hasEnCharCount(redundantChar) - hasEnCharCount(mOpenSuffixSpan) + 1
                    lastSpace = if (offset <= 0) lastSpace else lastSpace - offset
                    mCloseSpannableStr = charSequenceToSpannable(originalText.subSequence(0, lastSpace))
                }
                //计算收起的文本高度
                mCloseHeight = mTextView.height + mTextView.paddingTop + mTextView.paddingBottom
                mCloseSpannableStr?.append(ELLIPSIS_STRING)
                mCloseSpannableStr?.append(mOpenSuffixSpan)

                Log.w("123", "$mCloseHeight  mOpenSpannableStr= $mOpenSpannableStr \n mCloseSpannableStr=$mCloseSpannableStr")
            }
        }
        isClosed = mExpandable
        if (mExpandable) {
            mTextView.text = mCloseSpannableStr
            //设置监听
            mTextView.setOnClickListener { v ->
                mClickListener?.onViewClick(v)
            }
        } else {
            mTextView.text = mOpenSpannableStr
        }
        return this
    }

    private fun hasEnCharCount(str: CharSequence?): Int {
        var count = 0
        if (str.isNullOrBlank()) return count

        for (i in str.indices) {
            val c = str[i]
            if (c in ' '..'~') {
                count++
            }
        }
        return count
    }

    fun switchOpenClose() {
        if (animating) {
            return
        }
        if (mExpandable) {
            isClosed = !isClosed
            if (isClosed) {
                close()
            } else {
                open()
            }
        }
    }

    /**
     * 设置是否有动画
     */
    fun setHasAnimation(hasAnimation: Boolean): ExpandableTextViewUtils {
        this.hasAnimation = hasAnimation
        return this
    }

    /**
     * 展开
     */
    private fun open() {
        if (hasAnimation) {
            mOpenHeight = mStaticLayout.height + mTextView.paddingTop + mTextView.paddingBottom
            executeOpenAnim()
            Log.e("123", "open=${mTextView.height}  mCloseHeight= $mCloseHeight  mOpenHeight=$mOpenHeight")
        } else {
            mTextView.maxLines = Int.MAX_VALUE
            mTextView.text = mOpenSpannableStr
            mStateListener?.onOpen()
        }
    }

    /**
     * 收起
     */
    private fun close() {
        if (hasAnimation) {
            executeCloseAnim()
            Log.e("123", "close=${mTextView.height}  mCloseHeight= $mCloseHeight  mOpenHeight=$mOpenHeight")
        } else {
            mTextView.maxLines = mMaxLines
            mTextView.text = mCloseSpannableStr
            mStateListener?.onClose()
        }
    }

    /**
     * 执行展开动画
     */
    private fun executeOpenAnim() {
        //创建展开动画
        if (mOpenAnim == null) {
            mOpenAnim = ExpandCollapseAnimation(mTextView, mCloseHeight, mOpenHeight)
            mOpenAnim?.fillAfter = true
            mOpenAnim?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    mTextView.maxLines = Int.MAX_VALUE
                    mTextView.text = mOpenSpannableStr
                }

                override fun onAnimationEnd(animation: Animation) {
                    //动画结束后textview设置展开的状态
                    mTextView.layoutParams.height = mOpenHeight
                    mTextView.requestLayout()
                    //animating = false
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        if (animating) {
            return
        }
        animating = true
        mTextView.clearAnimation()
        mTextView.startAnimation(mOpenAnim)
    }

    /**
     * 执行收起动画
     */
    private fun executeCloseAnim() {
        //创建收起动画
        if (mCloseAnim == null) {
            mCloseAnim = ExpandCollapseAnimation(mTextView, mOpenHeight, mCloseHeight)
            mCloseAnim?.fillAfter = true
            mCloseAnim?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    mTextView.layoutParams.height = mCloseHeight
                    mTextView.requestLayout()

                    mTextView.maxLines = mMaxLines
                    mTextView.text = mCloseSpannableStr
                    //animating = false
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        if (animating) {
            return
        }
        animating = true
        mTextView.clearAnimation()
        mTextView.startAnimation(mCloseAnim)
    }

    private lateinit var mStaticLayout: StaticLayout

    @SuppressLint("ObsoleteSdkInt")
    private fun createStaticLayout(): StaticLayout {
        if (this::mStaticLayout.isInitialized) return mStaticLayout

        val spannable: SpannableStringBuilder? = mOpenSpannableStr
        val contentWidth = initWidth - mTextView.paddingLeft - mTextView.paddingRight
        mStaticLayout = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val builder = StaticLayout.Builder
                    .obtain(spannable ?: "", 0, spannable?.length ?: 0, mTextView.paint, contentWidth)

                builder.setAlignment(Layout.Alignment.ALIGN_NORMAL)
                builder.setIncludePad(mTextView.includeFontPadding)
                builder.setLineSpacing(mTextView.lineSpacingExtra, mTextView.lineSpacingMultiplier)
                builder.build()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> {
                @Suppress("DEPRECATION")
                StaticLayout(
                    spannable, mTextView.paint, contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    mTextView.lineSpacingMultiplier, mTextView.lineSpacingExtra, mTextView.includeFontPadding
                )
            }
            else -> { //兼容16以下版本
                @Suppress("DEPRECATION")
                StaticLayout(
                    spannable, mTextView.paint, contentWidth, Layout.Alignment.ALIGN_NORMAL,
                    getFloatField("mSpacingMult", 1F), getFloatField("mSpacingAdd", 0f), mTextView.includeFontPadding
                )
            }
        }
        return mStaticLayout
    }

    private fun getFloatField(fieldName: String, defaultValue: Float): Float {
        var value = defaultValue
        if (TextUtils.isEmpty(fieldName)) {
            return value
        }
        try {
            // 获取该类的所有属性值域
            val fields = this.javaClass.declaredFields
            for (field in fields) {
                if (TextUtils.equals(fieldName, field.name)) {
                    value = field.getFloat(this)
                    break
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return value
    }

    private fun charSequenceToSpannable(charSequence: CharSequence?): SpannableStringBuilder {
        var spannableStringBuilder: SpannableStringBuilder? = null
        if (mCharSequenceToSpannableHandler != null) {
            spannableStringBuilder = mCharSequenceToSpannableHandler?.charSequenceToSpannable(charSequence)
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = if (!charSequence.isNullOrBlank() && charSequence is SpannableStringBuilder) {
                charSequence
            } else {
                SpannableStringBuilder(charSequence)
            }
        }
        return spannableStringBuilder
    }

    /**
     * 初始化TextView的可展示宽度
     */
    fun initWidth(width: Int): ExpandableTextViewUtils {
        initWidth = width
        return this
    }

    fun setMaxLines(maxLines: Int): ExpandableTextViewUtils {
        mMaxLines = maxLines
        mTextView.maxLines = maxLines
        return this
    }

    /**
     * 设置展开后缀text
     */
    fun setOpenSuffix(openSuffix: String): ExpandableTextViewUtils {
        mOpenSuffixStr = openSuffix
        return this
    }

    /**
     * 设置展开后缀文本颜色
     */
    fun setOpenSuffixColor(@ColorInt openSuffixColor: Int): ExpandableTextViewUtils {
        mOpenSuffixColor = openSuffixColor
        return this
    }

    /**
     * 设置收起后缀text
     */
    fun setCloseSuffix(closeSuffix: String): ExpandableTextViewUtils {
        mCloseSuffixStr = closeSuffix
        return this
    }

    /**
     * 设置收起后缀文本颜色
     */
    fun setCloseSuffixColor(@ColorInt closeSuffixColor: Int): ExpandableTextViewUtils {
        mCloseSuffixColor = closeSuffixColor
        return this
    }

    /**
     * 收起后缀是否另起一行
     */
    fun setCloseInNewLine(closeInNewLine: Boolean): ExpandableTextViewUtils {
        mCloseInNewLine = closeInNewLine
        return this
    }

    fun setOpenOrCloseByUserHandle(isHandleByUser: Boolean): ExpandableTextViewUtils {
        isOpenOrCloseByUserHandle = isHandleByUser
        return this
    }

    /**
     * 设置文本内容处理
     */
    fun setCharSequenceToSpannableHandler(handler: CharSequenceToSpannableHandler?): ExpandableTextViewUtils {
        mCharSequenceToSpannableHandler = handler
        return this
    }

    fun setOnStateListener(listener: OnStateListener?): ExpandableTextViewUtils {
        mStateListener = listener
        return this
    }

    fun setOnClickListener(listener: OnClickListener?): ExpandableTextViewUtils {
        mClickListener = listener
        return this
    }

    /**
     * 更新展开后缀Spannable
     */
    private fun updateOpenSuffixSpan() {
        if (mOpenSuffixStr.isBlank()) {
            mOpenSuffixSpan = SpannableString("")
            return
        }
        mOpenSuffixSpan = SpannableString(mOpenSuffixStr)
        mOpenSuffixSpan?.setSpan(StyleSpan(Typeface.BOLD), 0, mOpenSuffixStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mOpenSuffixSpan?.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (!isOpenOrCloseByUserHandle) {
                    switchOpenClose()
                }
                mClickListener?.onOpenClick()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = mOpenSuffixColor
                ds.isUnderlineText = false
            }
        }, 0, mOpenSuffixStr.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
    }

    /**
     * 更新收起后缀Spannable
     */
    private fun updateCloseSuffixSpan() {
        if (TextUtils.isEmpty(mCloseSuffixStr)) {
            mCloseSuffixSpan = SpannableString("")
            return
        }
        mCloseSuffixSpan = SpannableString(mCloseSuffixStr)
        mCloseSuffixSpan?.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            mCloseSuffixStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (mCloseInNewLine) {
            val alignmentSpan: AlignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)
            mCloseSuffixSpan?.setSpan(alignmentSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        mCloseSuffixSpan?.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (!isOpenOrCloseByUserHandle) {
                    switchOpenClose()
                }
                mClickListener?.onCloseClick()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = mCloseSuffixColor
                ds.isUnderlineText = false
            }
        }, 1, mCloseSuffixStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    interface OnStateListener {
        fun onOpen()
        fun onClose()
    }

    interface OnClickListener {
        fun onOpenClick()
        fun onCloseClick()
        fun onViewClick(v: View)
    }

    interface CharSequenceToSpannableHandler {
        fun charSequenceToSpannable(charSequence: CharSequence?): SpannableStringBuilder
    }

    /**
     * 修复文本可滑动问题
     */
    private class OverLinkMovementMethod : LinkMovementMethod() {
        override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
            val action = event.action
            if (action == MotionEvent.ACTION_MOVE) {
                if (!canScroll) {
                    return true
                }
            }
            return super.onTouchEvent(widget, buffer, event)
        }

        companion object {
            var canScroll = false
            val instance: MovementMethod?
                get() {
                    if (sInstance == null) {
                        sInstance = OverLinkMovementMethod()
                    }
                    return sInstance
                }
            private var sInstance: OverLinkMovementMethod? = null
            private val FROM_BELOW: Any = Concrete()
        }
    }

    private class ExpandCollapseAnimation(//动画执行view
        private val mTargetView: View, //动画执行的开始高度
        private val mStartHeight: Int, //动画结束后的高度
        private val mEndHeight: Int
    ) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            mTargetView.scrollY = 0
            //计算出每次应该显示的高度,改变执行view的高度，实现动画
            mTargetView.layoutParams.height = ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight).toInt()
            mTargetView.requestLayout()
        }

        init {
            duration = 400
        }
    }

}