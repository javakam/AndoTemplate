package ando.toolkit

import ando.toolkit.AppUtils.getContext
import android.annotation.SuppressLint
import androidx.annotation.IntRange
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.*
import android.util.Log
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * # SpanUtils
 *
 * From QMUI_Android
 *
 * eg:
 * SpanUtils().append("")
 *      .setForegroundColor(Color.BLUE)
 *      .append(text1)
 *      .setFontProportion(1.1F)
 *      .setFontSize(ResUtils.getDimensionPixelSize(R.dimen.font_14))
 *      .setBold()
 *
 *      .append(text2)
 *      .setForegroundColor(Color.YELLOW)
 *      .setFontSize(14)
 *      .create()
 *
 * @author javakam
 * @date 2019/1/14 下午10:03
 */
class SpanUtils {
    @IntDef(ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER, ALIGN_TOP)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Align

    private var mText: CharSequence
    private var flag = 0
    private var foregroundColor = 0
    private var backgroundColor = 0
    private var lineHeight = 0
    private var alignLine = 0
    private var quoteColor = 0
    private var stripeWidth = 0
    private var quoteGapWidth = 0
    private var first = 0
    private var rest = 0
    private var bulletColor = 0
    private var bulletRadius = 0
    private var bulletGapWidth = 0
    private var iconMarginBitmap: Bitmap? = null
    private var iconMarginDrawable: Drawable? = null
    private var iconMarginUri: Uri? = null
    private var iconMarginResourceId = 0
    private var iconMarginGapWidth = 0
    private var alignIconMargin = 0
    private var fontSize = 0
    private var fontSizeIsDp = false
    private var proportion = 0f
    private var xProportion = 0f
    private var isStrikeThrough = false
    private var isUnderline = false
    private var isSuperscript = false
    private var isSubscript = false
    private var isBold = false
    private var isItalic = false
    private var isBoldItalic = false
    private var fontFamily: String? = null
    private var typeface: Typeface? = null
    private var alignment: Layout.Alignment? = null
    private var clickSpan: ClickableSpan? = null
    private var url: String? = null
    private var blurRadius = 0f
    private var style: Blur? = null
    private var shader: Shader? = null
    private var shadowRadius = 0f
    private var shadowDx = 0f
    private var shadowDy = 0f
    private var shadowColor = 0
    private var spans: Array<Any>? = null
    private var imageBitmap: Bitmap? = null
    private var imageDrawable: Drawable? = null
    private var imageUri: Uri? = null
    private var imageResourceId = 0
    private var alignImage = 0
    private var spaceSize = 0
    private var spaceColor = 0
    private val mBuilder: SpannableStringBuilder = SpannableStringBuilder()
    private var mType = 0
    private val mTypeCharSequence = 0
    private val mTypeImage = 1
    private val mTypeSpace = 2

    private fun setDefault() {
        flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        foregroundColor = COLOR_DEFAULT
        backgroundColor = COLOR_DEFAULT
        lineHeight = -1
        quoteColor = COLOR_DEFAULT
        first = -1
        bulletColor = COLOR_DEFAULT
        iconMarginBitmap = null
        iconMarginDrawable = null
        iconMarginUri = null
        iconMarginResourceId = -1
        iconMarginGapWidth = -1
        fontSize = -1
        proportion = -1f
        xProportion = -1f
        isStrikeThrough = false
        isUnderline = false
        isSuperscript = false
        isSubscript = false
        isBold = false
        isItalic = false
        isBoldItalic = false
        fontFamily = null
        typeface = null
        alignment = null
        clickSpan = null
        url = null
        blurRadius = -1f
        shader = null
        shadowRadius = -1f
        spans = null
        imageBitmap = null
        imageDrawable = null
        imageUri = null
        imageResourceId = -1
        spaceSize = -1
    }

    /**
     * 设置标识
     *
     * @param flag
     *  * [Spanned.SPAN_INCLUSIVE_EXCLUSIVE]
     *  * [Spanned.SPAN_INCLUSIVE_INCLUSIVE]
     *  * [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
     *  * [Spanned.SPAN_EXCLUSIVE_INCLUSIVE]
     *
     * @return [SpanUtils]
     */
    fun setFlag(flag: Int): SpanUtils {
        this.flag = flag
        return this
    }

    /**
     * 设置前景色
     *
     * @param color 前景色
     * @return [SpanUtils]
     */
    fun setForegroundColor(@ColorInt color: Int): SpanUtils {
        foregroundColor = color
        return this
    }

    /**
     * 设置背景色
     *
     * @param color 背景色
     * @return [SpanUtils]
     */
    fun setBackgroundColor(@ColorInt color: Int): SpanUtils {
        backgroundColor = color
        return this
    }

    /**
     * 设置行高
     *
     * 当行高大于字体高度时，字体在行中的位置默认居中
     *
     * @param lineHeight 行高
     * @return [SpanUtils]
     */
    fun setLineHeight(@IntRange(from = 0) lineHeight: Int): SpanUtils {
        return setLineHeight(lineHeight, ALIGN_CENTER)
    }

    /**
     * 设置行高
     *
     * 当行高大于字体高度时，字体在行中的位置由`align`决定
     *
     * @param lineHeight 行高
     * @param align      对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun setLineHeight(@IntRange(from = 0) lineHeight: Int, @Align align: Int): SpanUtils {
        this.lineHeight = lineHeight
        alignLine = align
        return this
    }

    /**
     * 设置引用线的颜色
     *
     * @param color 引用线的颜色
     * @return [SpanUtils]
     */
    fun setQuoteColor(@ColorInt color: Int): SpanUtils {
        return setQuoteColor(color, 2, 2)
    }

    /**
     * 设置引用线的颜色
     *
     * @param color       引用线的颜色
     * @param stripeWidth 引用线线宽
     * @param gapWidth    引用线和文字间距
     * @return [SpanUtils]
     */
    fun setQuoteColor(
        @ColorInt color: Int,
        @IntRange(from = 1) stripeWidth: Int,
        @IntRange(from = 0) gapWidth: Int
    ): SpanUtils {
        quoteColor = color
        this.stripeWidth = stripeWidth
        quoteGapWidth = gapWidth
        return this
    }

    /**
     * 设置缩进
     *
     * @param first 首行缩进
     * @param rest  剩余行缩进
     * @return [SpanUtils]
     */
    fun setLeadingMargin(@IntRange(from = 0) first: Int, @IntRange(from = 0) rest: Int): SpanUtils {
        this.first = first
        this.rest = rest
        return this
    }

    /**
     * 设置列表标记
     *
     * @param gapWidth 列表标记和文字间距离
     * @return [SpanUtils]
     */
    fun setBullet(@IntRange(from = 0) gapWidth: Int): SpanUtils {
        return setBullet(0, 3, gapWidth)
    }

    /**
     * 设置列表标记
     *
     * @param color    列表标记的颜色
     * @param radius   列表标记颜色
     * @param gapWidth 列表标记和文字间距离
     * @return [SpanUtils]
     */
    fun setBullet(
        @ColorInt color: Int,
        @IntRange(from = 0) radius: Int,
        @IntRange(from = 0) gapWidth: Int
    ): SpanUtils {
        bulletColor = color
        bulletRadius = radius
        bulletGapWidth = gapWidth
        return this
    }

    /**
     * 设置图标
     *
     * 默认0边距，居中对齐
     *
     * @param bitmap 图标bitmap
     * @return [SpanUtils]
     */
    fun setIconMargin(bitmap: Bitmap?): SpanUtils {
        return setIconMargin(bitmap, 0, ALIGN_CENTER)
    }

    /**
     * 设置图标
     *
     * @param bitmap   图标bitmap
     * @param gapWidth 图标和文字间距离
     * @param align    对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun setIconMargin(bitmap: Bitmap?, gapWidth: Int, @Align align: Int): SpanUtils {
        iconMarginBitmap = bitmap
        iconMarginGapWidth = gapWidth
        alignIconMargin = align
        return this
    }

    /**
     * 设置图标
     *
     * 默认0边距，居中对齐
     *
     * @param drawable 图标drawable
     * @return [SpanUtils]
     */
    fun setIconMargin(drawable: Drawable?): SpanUtils {
        return setIconMargin(drawable, 0, ALIGN_CENTER)
    }

    /**
     * 设置图标
     *
     * @param drawable 图标drawable
     * @param gapWidth 图标和文字间距离
     * @param align    对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun setIconMargin(drawable: Drawable?, gapWidth: Int, @Align align: Int): SpanUtils {
        iconMarginDrawable = drawable
        iconMarginGapWidth = gapWidth
        alignIconMargin = align
        return this
    }

    /**
     * 设置图标
     *
     * 默认0边距，居中对齐
     *
     * @param uri 图标uri
     * @return [SpanUtils]
     */
    fun setIconMargin(uri: Uri?): SpanUtils {
        return setIconMargin(uri, 0, ALIGN_CENTER)
    }

    /**
     * 设置图标
     *
     * @param uri      图标uri
     * @param gapWidth 图标和文字间距离
     * @param align    对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun setIconMargin(uri: Uri?, gapWidth: Int, @Align align: Int): SpanUtils {
        iconMarginUri = uri
        iconMarginGapWidth = gapWidth
        alignIconMargin = align
        return this
    }

    /**
     * 设置图标
     *
     * 默认0边距，居中对齐
     *
     * @param resourceId 图标resourceId
     * @return [SpanUtils]
     */
    fun setIconMargin(@DrawableRes resourceId: Int): SpanUtils {
        return setIconMargin(resourceId, 0, ALIGN_CENTER)
    }

    /**
     * 设置图标
     *
     * @param resourceId 图标resourceId
     * @param gapWidth   图标和文字间距离
     * @param align      对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun setIconMargin(@DrawableRes resourceId: Int, gapWidth: Int, @Align align: Int): SpanUtils {
        iconMarginResourceId = resourceId
        iconMarginGapWidth = gapWidth
        alignIconMargin = align
        return this
    }

    /**
     * 设置字体尺寸
     *
     * @param size 尺寸
     * @return [SpanUtils]
     */
    fun setFontSize(@IntRange(from = 0) size: Int): SpanUtils {
        return setFontSize(size, false)
    }

    /**
     * 设置字体尺寸
     *
     * @param size 尺寸
     * @param isDp 是否使用dip
     * @return [SpanUtils]
     */
    fun setFontSize(@IntRange(from = 0) size: Int, isDp: Boolean): SpanUtils {
        fontSize = size
        fontSizeIsDp = isDp
        return this
    }

    /**
     * 设置字体比例
     *
     * @param proportion 比例
     * @return [SpanUtils]
     */
    fun setFontProportion(
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        ) proportion: Float
    ): SpanUtils {
        this.proportion = proportion
        return this
    }

    /**
     * 设置字体横向比例
     *
     * @param proportion 比例
     * @return [SpanUtils]
     */
    fun setFontXProportion(
        @FloatRange(
            from = 0.0,
            fromInclusive = false
        ) proportion: Float
    ): SpanUtils {
        xProportion = proportion
        return this
    }

    /**
     * 设置删除线
     *
     * @return [SpanUtils]
     */
    fun setStrikeThrough(): SpanUtils {
        isStrikeThrough = true
        return this
    }

    /**
     * 设置下划线
     *
     * @return [SpanUtils]
     */
    fun setUnderline(): SpanUtils {
        isUnderline = true
        return this
    }

    /**
     * 设置上标
     *
     * @return [SpanUtils]
     */
    fun setSuperscript(): SpanUtils {
        isSuperscript = true
        return this
    }

    /**
     * 设置下标
     *
     * @return [SpanUtils]
     */
    fun setSubscript(): SpanUtils {
        isSubscript = true
        return this
    }

    /**
     * 设置粗体
     *
     * @return [SpanUtils]
     */
    fun setBold(): SpanUtils {
        isBold = true
        return this
    }

    /**
     * 设置斜体
     *
     * @return [SpanUtils]
     */
    fun setItalic(): SpanUtils {
        isItalic = true
        return this
    }

    /**
     * 设置粗斜体
     *
     * @return [SpanUtils]
     */
    fun setBoldItalic(): SpanUtils {
        isBoldItalic = true
        return this
    }

    /**
     * 设置字体系列
     *
     * @param fontFamily 字体系列
     *
     *  * monospace
     *  * serif
     *  * sans-serif
     *
     * @return [SpanUtils]
     */
    fun setFontFamily(fontFamily: String): SpanUtils {
        this.fontFamily = fontFamily
        return this
    }

    /**
     * 设置字体
     *
     * @param typeface 字体
     * @return [SpanUtils]
     */
    fun setTypeface(typeface: Typeface): SpanUtils {
        this.typeface = typeface
        return this
    }

    /**
     * 设置对齐
     *
     * @param alignment 对其方式
     *
     *  * [Alignment.ALIGN_NORMAL]正常
     *  * [Alignment.ALIGN_OPPOSITE]相反
     *  * [Alignment.ALIGN_CENTER]居中
     *
     * @return [SpanUtils]
     */
    fun setAlign(alignment: Layout.Alignment): SpanUtils {
        this.alignment = alignment
        return this
    }

    /**
     * 设置点击事件
     *
     * 需添加view.setMovementMethod(LinkMovementMethod.get())
     *
     * @param clickSpan 点击事件
     * @return [SpanUtils]
     */
    fun setClickSpan(clickSpan: ClickableSpan): SpanUtils {
        this.clickSpan = clickSpan
        return this
    }

    /**
     * 设置超链接
     *
     * 需添加view.setMovementMethod(LinkMovementMethod.get())
     *
     * @param url 超链接
     * @return [SpanUtils]
     */
    fun setUrl(url: String): SpanUtils {
        this.url = url
        return this
    }

    /**
     * 设置模糊
     *
     * 尚存bug，其他地方存在相同的字体的话，相同字体出现在之前的话那么就不会模糊，出现在之后的话那会一起模糊
     *
     * 以上bug关闭硬件加速即可
     *
     * @param radius 模糊半径（需大于0）
     * @param style  模糊样式
     *  * [Blur.NORMAL]
     *  * [Blur.SOLID]
     *  * [Blur.OUTER]
     *  * [Blur.INNER]
     *
     * @return [SpanUtils]
     */
    fun setBlur(
        @FloatRange(from = 0.0, fromInclusive = false) radius: Float,
        style: Blur?
    ): SpanUtils {
        blurRadius = radius
        this.style = style
        return this
    }

    /**
     * 设置着色器
     *
     * @param shader 着色器
     * @return [SpanUtils]
     */
    fun setShader(shader: Shader): SpanUtils {
        this.shader = shader
        return this
    }

    /**
     * 设置阴影
     *
     * @param radius      阴影半径
     * @param dx          x轴偏移量
     * @param dy          y轴偏移量
     * @param shadowColor 阴影颜色
     * @return [SpanUtils]
     */
    fun setShadow(
        @FloatRange(from = 0.0, fromInclusive = false) radius: Float,
        dx: Float,
        dy: Float,
        shadowColor: Int
    ): SpanUtils {
        shadowRadius = radius
        shadowDx = dx
        shadowDy = dy
        this.shadowColor = shadowColor
        return this
    }

    /**
     * 设置样式
     *
     * @param spans 样式
     * @return [SpanUtils]
     */
    fun setSpans(vararg spans: Any?): SpanUtils {
        if (spans.isNotEmpty()) {
            this.spans = arrayOf(spans)
        }
        return this
    }

    /**
     * 追加样式字符串
     *
     * @param text 样式字符串文本
     * @return [SpanUtils]
     */
    fun append(text: CharSequence): SpanUtils {
        apply(mTypeCharSequence)
        mText = text
        return this
    }

    /**
     * 追加一行
     *
     * @return [SpanUtils]
     */
    fun appendLine(): SpanUtils {
        apply(mTypeCharSequence)
        mText = LINE_SEPARATOR
        return this
    }

    /**
     * 追加一行样式字符串
     *
     * @return [SpanUtils]
     */
    fun appendLine(text: CharSequence): SpanUtils {
        apply(mTypeCharSequence)
        mText = text.toString() + LINE_SEPARATOR
        return this
    }

    /**
     * 追加图片
     *
     * @param bitmap 图片位图
     * @return [SpanUtils]
     */
    fun appendImage(bitmap: Bitmap): SpanUtils? {
        return appendImage(bitmap, SpanUtils.ALIGN_BOTTOM)
    }

    /**
     * 追加图片
     *
     * @param bitmap 图片位图
     * @param align  对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BASELINE]基线对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun appendImage(bitmap: Bitmap, @Align align: Int): SpanUtils? {
        apply(mTypeImage)
        imageBitmap = bitmap
        alignImage = align
        return this
    }

    /**
     * 追加图片
     *
     * @param drawable 图片资源
     * @return [SpanUtils]
     */
    fun appendImage(drawable: Drawable): SpanUtils? {
        return appendImage(drawable, SpanUtils.ALIGN_BOTTOM)
    }

    /**
     * 追加图片
     *
     * @param drawable 图片资源
     * @param align    对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BASELINE]基线对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun appendImage(drawable: Drawable, @Align align: Int): SpanUtils? {
        apply(mTypeImage)
        imageDrawable = drawable
        alignImage = align
        return this
    }

    /**
     * 追加图片
     *
     * @param uri 图片uri
     * @return [SpanUtils]
     */
    fun appendImage(uri: Uri): SpanUtils? {
        return appendImage(uri, SpanUtils.ALIGN_BOTTOM)
    }

    /**
     * 追加图片
     *
     * @param uri   图片uri
     * @param align 对齐
     *
     *  * [Align.ALIGN_TOP]顶部对齐
     *  * [Align.ALIGN_CENTER]居中对齐
     *  * [Align.ALIGN_BASELINE]基线对齐
     *  * [Align.ALIGN_BOTTOM]底部对齐
     *
     * @return [SpanUtils]
     */
    fun appendImage(uri: Uri, @Align align: Int): SpanUtils? {
        apply(mTypeImage)
        imageUri = uri
        alignImage = align
        return this
    }

    /**
     * 追加图片
     *
     * @param resourceId 图片资源id
     * @return [SpanUtils]
     */
    fun appendImage(@DrawableRes resourceId: Int): SpanUtils? {
        return appendImage(resourceId, SpanUtils.ALIGN_BOTTOM)
    }

    /**
     * 追加图片
     *
     * @param resourceId 图片资源id
     * @param align      对齐
     * @return [SpanUtils]
     */
    fun appendImage(@DrawableRes resourceId: Int, @Align align: Int): SpanUtils? {
        apply(mTypeImage)
        imageResourceId = resourceId
        alignImage = align
        return this
    }

    /**
     * 追加空白
     *
     * @param size 间距
     * @return [SpanUtils]
     */
    fun appendSpace(
        @IntRange(from = 0) size: Int,
        @ColorInt color: Int = Color.TRANSPARENT
    ): SpanUtils {
        apply(mTypeSpace)
        spaceSize = size
        spaceColor = color
        return this
    }

    private fun apply(type: Int) {
        applyLast()
        mType = type
    }

    /**
     * 创建样式字符串
     *
     * @return 样式字符串
     */
    fun create(): SpannableStringBuilder {
        applyLast()
        return mBuilder
    }

    /**
     * 设置上一次的样式
     */
    private fun applyLast() {
        when (mType) {
            mTypeCharSequence -> {
                updateCharCharSequence()
            }
            mTypeImage -> {
                updateImage()
            }
            mTypeSpace -> {
                updateSpace()
            }
        }
        setDefault()
    }

    private fun updateCharCharSequence() {
        if (mText.isEmpty()) {
            return
        }
        val start = mBuilder.length
        mBuilder.append(mText)
        val end = mBuilder.length
        if (foregroundColor != COLOR_DEFAULT) {
            mBuilder.setSpan(ForegroundColorSpan(foregroundColor), start, end, flag)
        }
        if (backgroundColor != COLOR_DEFAULT) {
            mBuilder.setSpan(BackgroundColorSpan(backgroundColor), start, end, flag)
        }
        if (first != -1) {
            mBuilder.setSpan(LeadingMarginSpan.Standard(first, rest), start, end, flag)
        }
        if (quoteColor != COLOR_DEFAULT) {
            mBuilder.setSpan(
                CustomQuoteSpan(quoteColor, stripeWidth, quoteGapWidth),
                start, end, flag
            )
        }
        if (bulletColor != COLOR_DEFAULT) {
            mBuilder.setSpan(
                CustomBulletSpan(bulletColor, bulletRadius, bulletGapWidth),
                start, end, flag
            )
        }
        if (iconMarginGapWidth != -1) {
            when {
                iconMarginBitmap != null -> {
                    iconMarginBitmap?.apply {
                        mBuilder.setSpan(
                            CustomIconMarginSpan(this, iconMarginGapWidth, alignIconMargin),
                            start, end, flag
                        )
                    }
                }
                iconMarginDrawable != null -> {
                    iconMarginDrawable?.apply {
                        mBuilder.setSpan(
                            CustomIconMarginSpan(this, iconMarginGapWidth, alignIconMargin),
                            start, end, flag
                        )
                    }
                }
                iconMarginUri != null -> {
                    iconMarginUri?.apply {
                        mBuilder.setSpan(
                            CustomIconMarginSpan(this, iconMarginGapWidth, alignIconMargin),
                            start, end, flag
                        )
                    }
                }
                iconMarginResourceId != -1 -> {
                    mBuilder.setSpan(
                        CustomIconMarginSpan(
                            iconMarginResourceId,
                            iconMarginGapWidth,
                            alignIconMargin
                        ), start, end, flag
                    )
                }
            }
        }
        if (fontSize != -1) {
            mBuilder.setSpan(AbsoluteSizeSpan(fontSize, fontSizeIsDp), start, end, flag)
        }
        if (proportion != -1f) {
            mBuilder.setSpan(RelativeSizeSpan(proportion), start, end, flag)
        }
        if (xProportion != -1f) {
            mBuilder.setSpan(ScaleXSpan(xProportion), start, end, flag)
        }
        if (lineHeight != -1) {
            mBuilder.setSpan(CustomLineHeightSpan(lineHeight, alignLine), start, end, flag)
        }
        if (isStrikeThrough) {
            mBuilder.setSpan(StrikethroughSpan(), start, end, flag)
        }
        if (isUnderline) {
            mBuilder.setSpan(UnderlineSpan(), start, end, flag)
        }
        if (isSuperscript) {
            mBuilder.setSpan(SuperscriptSpan(), start, end, flag)
        }
        if (isSubscript) {
            mBuilder.setSpan(SubscriptSpan(), start, end, flag)
        }
        if (isBold) {
            mBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
        }
        if (isItalic) {
            mBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
        }
        if (isBoldItalic) {
            mBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
        }
        if (fontFamily != null) {
            mBuilder.setSpan(TypefaceSpan(fontFamily), start, end, flag)
        }
        typeface?.apply {
            mBuilder.setSpan(CustomTypefaceSpan(this), start, end, flag)
        }

        if (alignment != null) {
            mBuilder.setSpan(AlignmentSpan.Standard(alignment), start, end, flag)
        }
        if (clickSpan != null) {
            mBuilder.setSpan(clickSpan, start, end, flag)
        }
        if (url != null) {
            mBuilder.setSpan(URLSpan(url), start, end, flag)
        }
        if (blurRadius != -1f) {
            mBuilder.setSpan(MaskFilterSpan(BlurMaskFilter(blurRadius, style)), start, end, flag)
        }
        shader?.apply {
            mBuilder.setSpan(ShaderSpan(this), start, end, flag)
        }
        if (shadowRadius != -1f) {
            mBuilder.setSpan(
                ShadowSpan(shadowRadius, shadowDx, shadowDy, shadowColor),
                start, end, flag
            )
        }
        spans?.apply {
            for (span in this) {
                mBuilder.setSpan(span, start, end, flag)
            }
        }
    }

    private fun updateImage() {
        val start = mBuilder.length
        mBuilder.append("<img>")
        val end = start + 5
        when {
            imageBitmap != null -> {
                imageBitmap?.apply {
                    mBuilder.setSpan(CustomImageSpan(this, alignImage), start, end, flag)
                }
            }
            imageDrawable != null -> {
                imageDrawable?.apply {
                    mBuilder.setSpan(CustomImageSpan(this, alignImage), start, end, flag)
                }
            }
            imageUri != null -> {
                imageUri?.apply {
                    mBuilder.setSpan(CustomImageSpan(this, alignImage), start, end, flag)
                }
            }
            imageResourceId != -1 -> {
                mBuilder.setSpan(CustomImageSpan(imageResourceId, alignImage), start, end, flag)
            }
        }
    }

    private fun updateSpace() {
        val start = mBuilder.length
        mBuilder.append("< >")
        val end = start + 3
        mBuilder.setSpan(SpaceSpan(spaceSize, spaceColor), start, end, flag)
    }

    /**
     * 行高
     */
    internal inner class CustomLineHeightSpan(
        private val height: Int,
        private val mVerticalAlignment: Int
    ) : CharacterStyle(), LineHeightSpan {
        override fun chooseHeight(
            text: CharSequence,
            start: Int,
            end: Int,
            spanstartv: Int,
            v: Int,
            fm: FontMetricsInt
        ) {
            var need = height - (v + fm.descent - fm.ascent - spanstartv)
            if (need > 0) {
                when (mVerticalAlignment) {
                    Companion.ALIGN_TOP -> {
                        fm.descent += need
                    }
                    Companion.ALIGN_CENTER -> {
                        fm.descent += need / 2
                        fm.ascent -= need / 2
                    }
                    else -> {
                        fm.ascent -= need
                    }
                }
            }
            need = height - (v + fm.bottom - fm.top - spanstartv)
            if (need > 0) {
                when (mVerticalAlignment) {
                    Companion.ALIGN_TOP -> {
                        fm.top += need
                    }
                    Companion.ALIGN_CENTER -> {
                        fm.bottom += need / 2
                        fm.top -= need / 2
                    }
                    else -> {
                        fm.top -= need
                    }
                }
            }
        }

        override fun updateDrawState(tp: TextPaint) {}


    }

    /**
     * 空格
     */
    internal inner class SpaceSpan(
        private val width: Int,
        private val color: Int = Color.TRANSPARENT
    ) : ReplacementSpan() {
        override fun getSize(
            paint: Paint, text: CharSequence,
            @IntRange(from = 0) start: Int,
            @IntRange(from = 0) end: Int,
            fm: FontMetricsInt?
        ): Int {
            return width
        }

        override fun draw(
            canvas: Canvas, text: CharSequence,
            @IntRange(from = 0) start: Int,
            @IntRange(from = 0) end: Int,
            x: Float, top: Int, y: Int, bottom: Int,
            paint: Paint
        ) {
            val style = paint.style
            val color = paint.color
            paint.style = Paint.Style.FILL
            paint.color = this.color
            canvas.drawRect(x, top.toFloat(), x + width, bottom.toFloat(), paint)
            paint.style = style
            paint.color = color
        }
    }

    /**
     * 引用
     */
    internal inner class CustomQuoteSpan(
        private val color: Int,
        private val stripeWidth: Int,
        private val gapWidth: Int
    ) : LeadingMarginSpan {
        override fun getLeadingMargin(first: Boolean): Int {
            return stripeWidth + gapWidth
        }

        override fun drawLeadingMargin(
            c: Canvas, p: Paint, x: Int, dir: Int,
            top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int,
            first: Boolean, layout: Layout
        ) {
            val style = p.style
            val color = p.color
            p.style = Paint.Style.FILL
            p.color = this.color
            c.drawRect(
                x.toFloat(),
                top.toFloat(),
                x + dir * stripeWidth.toFloat(),
                bottom.toFloat(),
                p
            )
            p.style = style
            p.color = color
        }
    }

    /**
     * 列表项
     */
    internal inner class CustomBulletSpan(
        private val color: Int,
        private val radius: Int,
        private val gapWidth: Int
    ) : LeadingMarginSpan {
        private lateinit var sBulletPath: Path
        override fun getLeadingMargin(first: Boolean): Int {
            return 2 * radius + gapWidth
        }

        override fun drawLeadingMargin(
            c: Canvas, p: Paint, x: Int, dir: Int,
            top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int,
            first: Boolean, l: Layout
        ) {
            if ((text as Spanned).getSpanStart(this) == start) {
                val style = p.style
                var oldColor = 0
                oldColor = p.color
                p.color = color
                p.style = Paint.Style.FILL
                if (c.isHardwareAccelerated) {
                    if (!this::sBulletPath.isInitialized) {
                        sBulletPath = Path()
                        // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                        sBulletPath.addCircle(0.0f, 0.0f, radius.toFloat(), Path.Direction.CW)
                    }
                    c.save()
                    c.translate(x + dir * radius.toFloat(), (top + bottom) / 2.0f)
                    c.drawPath(sBulletPath, p)
                    c.restore()
                } else {
                    c.drawCircle(
                        x + dir * radius.toFloat(),
                        (top + bottom) / 2.0f, radius.toFloat(), p
                    )
                }
                p.color = oldColor
                p.style = style
            }
        }
    }

    internal inner class CustomIconMarginSpan : LeadingMarginSpan, LineHeightSpan {
        private var mBitmap: Bitmap
        private val mVerticalAlignment: Int
        private var mPad: Int
        private var totalHeight = 0
        private var lineHeight = 0
        private var need0 = 0
        private var need1 = 0
        private var flag = false

        constructor(b: Bitmap, pad: Int, verticalAlignment: Int) {
            mBitmap = b
            mPad = pad
            mVerticalAlignment = verticalAlignment
        }

        constructor(drawable: Drawable, pad: Int, verticalAlignment: Int) {
            mBitmap = drawable2Bitmap(drawable)
            mPad = pad
            mVerticalAlignment = verticalAlignment
        }

        constructor(uri: Uri, pad: Int, verticalAlignment: Int) {
            mBitmap = uri2Bitmap(uri)
            mPad = pad
            mVerticalAlignment = verticalAlignment
        }

        constructor(resourceId: Int, pad: Int, verticalAlignment: Int) {
            mBitmap = resource2Bitmap(resourceId)
            mPad = pad
            mVerticalAlignment = verticalAlignment
        }

        private fun drawable2Bitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap =
                if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                    Bitmap.createBitmap(
                        1, 1,
                        if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                    )
                } else {
                    Bitmap.createBitmap(
                        drawable.intrinsicWidth, drawable.intrinsicHeight,
                        if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
                    )
                }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        private fun uri2Bitmap(uri: Uri): Bitmap {
            return try {
                MediaStore.Images.Media.getBitmap(getContext().contentResolver, uri)
            } catch (e: IOException) {
                e.printStackTrace()
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            }
        }

        private fun resource2Bitmap(resourceId: Int): Bitmap {
            val drawable: Drawable = ContextCompat.getDrawable(getContext(), resourceId)
                ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            canvas.setBitmap(bitmap)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            return bitmap
        }

        override fun getLeadingMargin(first: Boolean): Int {
            return mBitmap.width + mPad
        }

        override fun drawLeadingMargin(
            c: Canvas, p: Paint, x: Int, dir: Int,
            top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int,
            first: Boolean, layout: Layout
        ) {
            var x = x
            val st = (text as Spanned).getSpanStart(this)
            val itop = layout.getLineTop(layout.getLineForOffset(st))
            if (dir < 0) {
                x -= mBitmap.width
            }
            val delta = totalHeight - mBitmap.height
            if (delta > 0) {
                when (mVerticalAlignment) {
                    Companion.ALIGN_TOP -> {
                        c.drawBitmap(mBitmap, x.toFloat(), itop.toFloat(), p)
                    }
                    Companion.ALIGN_CENTER -> {
                        c.drawBitmap(mBitmap, x.toFloat(), itop + delta / 2.toFloat(), p)
                    }
                    else -> {
                        c.drawBitmap(mBitmap, x.toFloat(), itop + delta.toFloat(), p)
                    }
                }
            } else {
                c.drawBitmap(mBitmap, x.toFloat(), itop.toFloat(), p)
            }
        }

        override fun chooseHeight(
            text: CharSequence, start: Int, end: Int,
            istartv: Int, v: Int, fm: FontMetricsInt
        ) {
            if (lineHeight == 0) {
                lineHeight = v - istartv
            }
            if (need0 == 0 && end == (text as Spanned).getSpanEnd(this)) {
                val ht = mBitmap.height
                need0 = ht - (v + fm.descent - fm.ascent - istartv)
                need1 = ht - (v + fm.bottom - fm.top - istartv)
                totalHeight = v - istartv + lineHeight
                return
            }
            if (need0 > 0 || need1 > 0) {
                if (mVerticalAlignment == Companion.ALIGN_TOP) {
                    // the rest space should be filled with the end of line
                    if (end == (text as Spanned).getSpanEnd(this)) {
                        if (need0 > 0) {
                            fm.descent += need0
                        }
                        if (need1 > 0) {
                            fm.bottom += need1
                        }
                    }
                } else if (mVerticalAlignment == Companion.ALIGN_CENTER) {
                    if (start == (text as Spanned).getSpanStart(this)) {
                        if (need0 > 0) {
                            fm.ascent -= need0 / 2
                        }
                        if (need1 > 0) {
                            fm.top -= need1 / 2
                        }
                    } else {
                        if (!flag) {
                            if (need0 > 0) {
                                fm.ascent += need0 / 2
                            }
                            if (need1 > 0) {
                                fm.top += need1 / 2
                            }
                            flag = true
                        }
                    }
                    if (end == text.getSpanEnd(this)) {
                        if (need0 > 0) {
                            fm.descent += need0 / 2
                        }
                        if (need1 > 0) {
                            fm.bottom += need1 / 2
                        }
                    }
                } else {
                    // the top space should be filled with the first of line
                    if (start == (text as Spanned).getSpanStart(this)) {
                        if (need0 > 0) {
                            fm.ascent -= need0
                        }
                        if (need1 > 0) {
                            fm.top -= need1
                        }
                    } else {
                        if (!flag) {
                            if (need0 > 0) {
                                fm.ascent += need0
                            }
                            if (need1 > 0) {
                                fm.top += need1
                            }
                            flag = true
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ParcelCreator")
    internal inner class CustomTypefaceSpan(private val newType: Typeface) : TypefaceSpan("") {
        override fun updateDrawState(textPaint: TextPaint) {
            apply(textPaint, newType)
        }

        override fun updateMeasureState(paint: TextPaint) {
            apply(paint, newType)
        }

        private fun apply(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            val old = paint.typeface
            oldStyle = old?.style ?: 0
            val fake = oldStyle and tf.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }
            paint.shader
            paint.typeface = tf
        }
    }

    internal inner class CustomImageSpan : CustomDynamicDrawableSpan {
        private var mDrawable: Drawable? = null
        private var mContentUri: Uri? = null
        private var mResourceId = 0

        constructor(b: Bitmap, verticalAlignment: Int) : super(verticalAlignment) {
            mDrawable = BitmapDrawable(getContext().resources, b)
            mDrawable?.let {
                it.setBounds(
                    0, 0,
                    it.intrinsicWidth, it.intrinsicHeight
                )
            }
        }

        constructor(d: Drawable, verticalAlignment: Int) : super(verticalAlignment) {
            mDrawable = d
            mDrawable?.let {
                it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            }
        }

        constructor(uri: Uri, verticalAlignment: Int) : super(verticalAlignment) {
            mContentUri = uri
        }

        constructor(
            @DrawableRes resourceId: Int,
            verticalAlignment: Int
        ) : super(verticalAlignment) {
            mResourceId = resourceId
        }

        override val drawable: Drawable?
            get() {
                var drawable: Drawable? = null
                when {
                    mDrawable != null -> {
                        drawable = mDrawable
                    }
                    mContentUri != null -> {
                        val bitmap: Bitmap
                        try {
                            val ins = getContext().contentResolver.openInputStream(mContentUri)
                            bitmap = BitmapFactory.decodeStream(ins)
                            drawable = BitmapDrawable(getContext().resources, bitmap)
                            drawable.setBounds(
                                0, 0,
                                drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()
                            )
                            ins?.close()
                        } catch (e: Exception) {
                            Log.e("sms", "Failed to loaded content $mContentUri", e)
                        }
                    }
                    else -> {
                        try {
                            drawable = ContextCompat.getDrawable(getContext(), mResourceId)
                            drawable?.setBounds(
                                0, 0,
                                drawable.intrinsicWidth, drawable.intrinsicHeight
                            )
                        } catch (e: Exception) {
                            Log.e("sms", "Unable to find resource: $mResourceId")
                        }
                    }
                }
                return drawable
            }
    }

    internal abstract inner class CustomDynamicDrawableSpan : ReplacementSpan {
        private val mVerticalAlignment: Int

        constructor() {
            mVerticalAlignment = ALIGN_BOTTOM
        }

        constructor(verticalAlignment: Int) {
            mVerticalAlignment = verticalAlignment
        }

        abstract val drawable: Drawable?
        override fun getSize(
            paint: Paint, text: CharSequence,
            start: Int, end: Int,
            fm: FontMetricsInt?
        ): Int {
            val d = cachedDrawable
            val rect = d?.bounds ?: return 0
            val fontHeight = (paint.fontMetrics.descent - paint.fontMetrics.ascent).toInt()
            if (fm != null) { // this is the fucking code which I waste 3 days
                if (rect.height() > fontHeight) {
                    when (mVerticalAlignment) {
                        ALIGN_TOP -> {
                            fm.descent += rect.height() - fontHeight
                        }
                        ALIGN_CENTER -> {
                            fm.ascent -= (rect.height() - fontHeight) / 2
                            fm.descent += (rect.height() - fontHeight) / 2
                        }
                        else -> {
                            fm.ascent -= rect.height() - fontHeight
                        }
                    }
                }
            }
            return rect.right
        }

        override fun draw(
            canvas: Canvas, text: CharSequence,
            start: Int, end: Int, x: Float,
            top: Int, y: Int, bottom: Int, paint: Paint
        ) {
            val d = cachedDrawable
            val rect = d?.bounds ?: return
            canvas.save()
            val fontHeight: Float = paint.fontMetrics.descent - paint.fontMetrics.ascent
            var transY: Int = bottom - rect.bottom
            if (rect.height() < fontHeight) {
                // this is the fucking code which I waste 3 days
                when (mVerticalAlignment) {
                    ALIGN_BASELINE -> {
                        transY -= paint.fontMetricsInt.descent
                    }
                    ALIGN_CENTER -> {
                        transY -= ((fontHeight - rect.height()) / 2F).toInt()
                    }
                    ALIGN_TOP -> {
                        transY -= (fontHeight - rect.height()).toInt()
                    }
                }
            }
            canvas.translate(x, transY.toFloat())
            d.draw(canvas)
            canvas.restore()
        }

        private val cachedDrawable: Drawable?
            get() {
                val wr = mDrawableRef
                var d: Drawable? = null
                if (wr != null) {
                    d = wr.get()
                }
                if (d == null) {
                    d = drawable
                    mDrawableRef = WeakReference(d)
                }
                return drawable
            }
        private var mDrawableRef: WeakReference<Drawable?>? = null

    }

    internal inner class ShaderSpan(private val mShader: Shader) : CharacterStyle(),
        UpdateAppearance {
        override fun updateDrawState(tp: TextPaint) {
            tp.shader = mShader
        }
    }

    internal inner class ShadowSpan(
        private val radius: Float,
        private val dx: Float,
        private val dy: Float,
        private val shadowColor: Int
    ) : CharacterStyle(), UpdateAppearance {
        override fun updateDrawState(tp: TextPaint) {
            tp.setShadowLayer(radius, dx, dy, shadowColor)
        }
    }

    companion object {
        private const val COLOR_DEFAULT = -0x1000001
        const val ALIGN_BOTTOM = 0
        const val ALIGN_BASELINE = 1
        const val ALIGN_CENTER = 2
        const val ALIGN_TOP = 3
        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }

    init {
        mText = ""
        setDefault()
    }
}