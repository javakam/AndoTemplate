package ando.library.widget.shadow

import ando.library.R
import ando.toolkit.ext.dp2px
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import kotlin.math.min

/**
 * 可以方便地生成圆角矩形/圆形的阴影
 * 1.默认样式
 *      <com.xxx.ShadowTextView></com.xxx.ShadowTextView>/ShadowButton
 *          android:layout_width="200dp"
 *          android:layout_height="60dp"
 *          android:layout_marginTop="10dp"
 *          android:gravity="center"
 *          android:paddingEnd="5dp"
 *          android:paddingBottom="5dp"
 *          android:text="ShadowTextView"
 *          android:textColor="@color/white"
 *          app:sd_bgColor="?attr/colorAccent"
 *          app:sd_offsetX="5dp"
 *          app:sd_offsetY="5dp"
 *          app:sd_shadowRadius="5dp"
 *          app:sd_shapeRadius="5dp" />
 * 2.渐变色
 *      <com.xxx.ShadowTextView></com.xxx.ShadowTextView>/ShadowButton
 *          android:layout_width="100dp"
 *          android:layout_height="100dp"
 *          android:layout_marginTop="10dp"
 *          android:gravity="center"
 *          android:paddingEnd="5dp"
 *          android:paddingBottom="5dp"
 *          android:text="渐变色"
 *          android:textColor="@color/white"
 *          app:sd_bgColor="?attr/colorAccent"
 *          app:sd_offsetX="5dp"
 *          app:sd_offsetY="5dp"
 *          app:sd_secondBgColor="@color/color_green"
 *          app:sd_shadowRadius="5dp"
 *          app:sd_shapeRadius="5dp"
 *          app:sd_shapeType="round" />
 *
 *
 * @author javakam
 * @date 2019/3/30 15:31
 */
class ShadowDrawable private constructor(
    private val mShape: Int,//形状: SHAPE_RECTANGLE OR SHAPE_ROUND
    bgColor: IntArray,
    shapeRadius: Int,
    shadowColor: Int,
    shadowRadius: Int,
    offsetX: Int,
    offsetY: Int
) : Drawable() {

    private val mShadowPaint: Paint
    private val mBgPaint: Paint

    /**
     * 阴影圆角弧度
     */
    private val mShadowRadius: Int

    /**
     * 圆角弧度
     */
    private val mShapeRadius: Int

    /**
     * X轴阴影偏移
     */
    private val mOffsetX: Int

    /**
     * Y轴阴影偏移
     */
    private val mOffsetY: Int

    /**
     * 背景颜色(大于1为渐变色)
     */
    private val mBgColor: IntArray?
    private var mRect: RectF? = null

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        mRect = RectF(
            (left + mShadowRadius - mOffsetX).toFloat(),
            (top + mShadowRadius - mOffsetY).toFloat(),
            (right - mShadowRadius - mOffsetX).toFloat(),
            (bottom - mShadowRadius - mOffsetY).toFloat()
        )
    }

    override fun draw(canvas: Canvas) {
        if (mBgColor != null) {
            if (mBgColor.size == 1) {
                mBgPaint.color = mBgColor[0]
            } else {
                mBgPaint.shader = LinearGradient(
                    mRect?.left ?: 0F, mRect?.height() ?: 0F / 2, mRect?.right ?: 0F,
                    mRect?.height() ?: 0F / 2, mBgColor, null, Shader.TileMode.CLAMP
                )
            }
        }
        if (mShape == SHAPE_RECTANGLE) {
            mRect?.apply {
                canvas.drawRoundRect(this, mShapeRadius.toFloat(), mShapeRadius.toFloat(), mShadowPaint)
                canvas.drawRoundRect(this, mShapeRadius.toFloat(), mShapeRadius.toFloat(), mBgPaint)
            }
        } else {
            canvas.drawCircle(
                mRect?.centerX() ?: 0F,
                mRect?.centerY() ?: 0F,
                min(mRect?.width() ?: 0F, mRect?.height() ?: 0F) / 2,
                mShadowPaint
            )
            canvas.drawCircle(
                mRect?.centerX() ?: 0F,
                mRect?.centerY() ?: 0F,
                min(mRect?.width() ?: 0F, mRect?.height() ?: 0F) / 2,
                mBgPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        mShadowPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mShadowPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    class Builder {
        private var mShape: Int
        private var mShapeRadius: Int
        private var mShadowColor: Int
        private var mShadowRadius: Int
        private var mOffsetX: Int
        private var mOffsetY: Int
        private var mBgColor = IntArray(1)
        fun setShape(mShape: Int): Builder {
            this.mShape = mShape
            return this
        }

        fun setShapeRadius(shapeRadius: Int): Builder {
            mShapeRadius = shapeRadius
            return this
        }

        fun setShadowColor(shadowColor: Int): Builder {
            mShadowColor = shadowColor
            return this
        }

        fun setShadowRadius(shadowRadius: Int): Builder {
            mShadowRadius = shadowRadius
            return this
        }

        fun setOffsetX(offsetX: Int): Builder {
            mOffsetX = offsetX
            return this
        }

        fun setOffsetY(offsetY: Int): Builder {
            mOffsetY = offsetY
            return this
        }

        fun setBgColor(bgColor: Int): Builder {
            mBgColor[0] = bgColor
            return this
        }

        fun setBgColor(bgColor: IntArray): Builder {
            mBgColor = bgColor
            return this
        }

        fun build(): ShadowDrawable {
            return ShadowDrawable(mShape, mBgColor, mShapeRadius, mShadowColor, mShadowRadius, mOffsetX, mOffsetY)
        }

        companion object {
            val DEFAULT_SHADOW_COLOR = Color.parseColor("#4d000000")
        }

        init {
            mShape = SHAPE_RECTANGLE
            mShapeRadius = 10
            mShadowColor = DEFAULT_SHADOW_COLOR
            mShadowRadius = 16
            mOffsetX = 0
            mOffsetY = 0
            mBgColor[0] = Color.TRANSPARENT
        }
    }

    companion object {
        /**
         * 方形
         */
        const val SHAPE_RECTANGLE = 0

        /**
         * 圆形
         */
        const val SHAPE_ROUND = 1
        fun setShadowDrawable(view: View, drawable: Drawable?) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .build()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            bgColor: Int,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .build()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            shape: Int,
            bgColor: Int,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setShape(shape)
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .build()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun setShadowDrawable(
            view: View,
            bgColor: IntArray,
            shapeRadius: Int,
            shadowColor: Int,
            shadowRadius: Int,
            offsetX: Int,
            offsetY: Int
        ) {
            val drawable = Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .build()
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            ViewCompat.setBackground(view, drawable)
        }

        fun fromAttributeSet(context: Context, attrs: AttributeSet?): ShadowDrawable {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowDrawable)
            val shadowRadius =
                typedArray.getDimensionPixelSize(R.styleable.ShadowDrawable_sd_shadowRadius, context.dp2px(8f))
            val shadowColor =
                typedArray.getColor(R.styleable.ShadowDrawable_sd_shadowColor, Builder.DEFAULT_SHADOW_COLOR)
            val shape = typedArray.getInt(R.styleable.ShadowDrawable_sd_shapeType, SHAPE_RECTANGLE)
            val shapeRadius =
                typedArray.getDimensionPixelSize(R.styleable.ShadowDrawable_sd_shapeRadius, context.dp2px(5f))
            val offsetX = typedArray.getDimensionPixelSize(R.styleable.ShadowDrawable_sd_offsetX, 0)
            val offsetY = typedArray.getDimensionPixelSize(R.styleable.ShadowDrawable_sd_offsetY, 0)
            val bgColor = typedArray.getColor(R.styleable.ShadowDrawable_sd_bgColor, Color.WHITE)
            val secondBgColor = typedArray.getColor(R.styleable.ShadowDrawable_sd_secondBgColor, -1)
            typedArray.recycle()
            val builder = Builder()
                .setShape(shape)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
            if (secondBgColor != -1) {
                builder.setBgColor(intArrayOf(bgColor, secondBgColor))
            } else {
                builder.setBgColor(bgColor)
            }
            return builder.build()
        }
    }

    init {
        mBgColor = bgColor
        mShapeRadius = shapeRadius
        mShadowRadius = shadowRadius
        mOffsetX = offsetX
        mOffsetY = offsetY
        mShadowPaint = Paint()
        mShadowPaint.color = Color.TRANSPARENT
        mShadowPaint.isAntiAlias = true
        mShadowPaint.setShadowLayer(shadowRadius.toFloat(), offsetX.toFloat(), offsetY.toFloat(), shadowColor)
        mShadowPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
        mBgPaint = Paint()
        mBgPaint.isAntiAlias = true
    }
}