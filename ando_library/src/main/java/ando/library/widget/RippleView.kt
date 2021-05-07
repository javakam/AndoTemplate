package ando.library.widget

import ando.library.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import kotlin.math.max

/**
 * # 点击可出现水波效果的控件，作为父布局使用
 *
 * ```
 * <style name="RippleViewStyle">
 *       <item name="rv_alpha">90</item>
 *       <item name="rv_frameRate">10</item>
 *       <item name="rv_rippleDuration">400</item>
 *       <item name="rv_color">@color/ando_config_color_white</item>
 *       <item name="rv_centered">false</item>
 *       <item name="rv_type">simpleRipple</item>
 *       <item name="rv_ripplePadding">0dp</item>
 *       <item name="rv_zoom">false</item>
 *       <item name="rv_zoomScale">1.03</item>
 *       <item name="rv_zoomDuration">200</item>
 *  </style>
 * ```
 *
 * @author javakam
 * @date 2019-05-15 00:10
 */
class RippleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.RippleViewStyle
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    /**
     * Set framerate for Ripple animation, default is 10
     */
    var frameRate: Int = 10

    /**
     * Duration of the Ripple animation in ms, default is 400ms
     */
    var rippleDuration: Int = 400

    /**
     * Set alpha for ripple effect color -> 0~255 default is 90
     */
    var rippleAlpha: Int = 90
    private var timer: Int = 0
    private var timerEmpty: Int = 0
    private var durationEmpty: Int = -1
    private var radiusMax: Float = 0F
    private var animationRunning = false

    private var mX: Float = -1F
    private var mY: Float = -1F

    /**
     * Duration of the ending animation in ms, default is 200ms
     */
    var zoomDuration: Int = 0

    /**
     * Scale of the end animation, default is 1.03f
     */
    var zoomScale: Float = 0F
    private var scaleAnimation: ScaleAnimation? = null

    /**
     * At the end of Ripple effect, the child views has to zoom
     */
    var isZooming: Boolean = false

    /**
     * Set if ripple animation has to be centered in its parent view or not, default is false
     */
    var isCentered: Boolean = false

    /**
     * Set Ripple padding if you want to avoid some graphic glitch
     */
    var ripplePadding: Int = 0
    private var rippleType: Int = 0
    private var rippleColor: Int = 0

    private lateinit var paint: Paint
    private var originBitmap: Bitmap? = null
    private var gestureDetector: GestureDetector? = null
    private var onCompletionListener: OnRippleCompleteListener? = null

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (isInEditMode) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView, defStyleAttr, 0)
        rippleColor = typedArray.getColor(R.styleable.RippleView_rv_color, Color.WHITE)
        rippleType = typedArray.getInt(R.styleable.RippleView_rv_type, 0)
        isZooming = typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false)
        isCentered = typedArray.getBoolean(R.styleable.RippleView_rv_centered, false)
        rippleDuration = typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, rippleDuration)
        frameRate = typedArray.getInteger(R.styleable.RippleView_rv_frameRate, frameRate)
        rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, rippleAlpha)
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, 0)
        zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, 1.03F)
        zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, 200)
        typedArray.recycle()
    }

    private fun initPaint() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = rippleColor
        paint.alpha = rippleAlpha
        setWillNotDraw(false)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (animationRunning) {
            canvas.save()
            if (rippleDuration <= timer * frameRate) {
                animationRunning = false
                timer = 0
                durationEmpty = -1
                timerEmpty = 0
                // There is problem on Android M where canvas.restore() seems to be called automatically
                // For now, don't call canvas.restore() manually on Android M (API 23)
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                    canvas.restore()
                }
                invalidate()
                onCompletionListener?.onComplete(this)
                return
            } else {
                Handler(Looper.myLooper() ?: Looper.getMainLooper()).postDelayed({ invalidate() }, frameRate.toLong())
            }
            if (timer == 0) {
                canvas.save()
            }
            canvas.drawCircle(mX, mY, radiusMax * (timer.toFloat() * frameRate / rippleDuration), paint)
            paint.color = Color.parseColor("#FFFF4444")
            if (rippleType == 1 && originBitmap != null && timer.toFloat() * frameRate / rippleDuration > 0.4f) {
                if (durationEmpty == -1) {
                    durationEmpty = rippleDuration - timer * frameRate
                }
                timerEmpty++
                val tmpBitmap =
                    getCircleBitmap((radiusMax * (timerEmpty.toFloat() * frameRate / durationEmpty)).toInt())
                canvas.drawBitmap(tmpBitmap, 0f, 0f, paint)
                tmpBitmap.recycle()
            }
            paint.color = rippleColor
            if (rippleType == 1) {
                if (timer.toFloat() * frameRate / rippleDuration > 0.6f) {
                    paint.alpha =
                        (rippleAlpha - rippleAlpha * (timerEmpty.toFloat() * frameRate / durationEmpty)).toInt()
                } else {
                    paint.alpha = rippleAlpha
                }
            } else {
                paint.alpha =
                    (rippleAlpha - rippleAlpha * (timer.toFloat() * frameRate / rippleDuration)).toInt()
            }
            timer++
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        scaleAnimation = ScaleAnimation(
            1.0f, zoomScale, 1.0f, zoomScale,
            (w shr 1).toFloat(), (h shr 1).toFloat()
        )
        scaleAnimation?.duration = zoomDuration.toLong()
        scaleAnimation?.repeatMode = Animation.REVERSE
        scaleAnimation?.repeatCount = 1
    }

    /**
     * Launch Ripple animation for the current view with a MotionEvent
     *
     * @param event MotionEvent registered by the Ripple gesture listener
     */
    fun animateRipple(event: MotionEvent) {
        createAnimation(event.x, event.y)
    }

    /**
     * Launch Ripple animation for the current view centered at x and y position
     *
     * @param x Horizontal position of the ripple center
     * @param y Vertical position of the ripple center
     */
    fun animateRipple(x: Float, y: Float) {
        createAnimation(x, y)
    }

    /**
     * Create Ripple animation centered at x, y
     *
     * @param x Horizontal position of the ripple center
     * @param y Vertical position of the ripple center
     */
    private fun createAnimation(x: Float, y: Float) {
        if (this.isEnabled && !animationRunning) {
            if (isZooming) {
                startAnimation(scaleAnimation)
            }
            radiusMax = max(mWidth, mHeight).toFloat()
            if (rippleType != 2) {
                radiusMax /= 2f
            }
            radiusMax -= ripplePadding.toFloat()
            if (isCentered || rippleType == 1) {
                this.mX = (measuredWidth shr 1).toFloat()
                this.mY = (measuredHeight shr 1).toFloat()
            } else {
                this.mX = x
                this.mY = y
            }
            animationRunning = true
            if (rippleType == 1 && originBitmap == null) {
                @Suppress("DEPRECATION")
                originBitmap = getDrawingCache(true)
            }
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector?.onTouchEvent(event) == true) {
            animateRipple(event)
            sendClickEvent(false)
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        onTouchEvent(event)
        return super.onInterceptTouchEvent(event)
    }

    /**
     * Send a click event if parent view is a Listview instance
     *
     * @param isLongClick Is the event a long click ?
     */
    private fun sendClickEvent(isLongClick: Boolean) {
        if (parent is AdapterView<*>) {
            val adapterView = parent as AdapterView<*>
            val position = adapterView.getPositionForView(this)
            val id = adapterView.getItemIdAtPosition(position)
            if (isLongClick) {
                adapterView.onItemLongClickListener?.onItemLongClick(
                    adapterView, this, position, id
                )
            } else {
                adapterView.onItemClickListener?.onItemClick(adapterView, this, position, id)
            }
        }
    }

    private fun getCircleBitmap(radius: Int): Bitmap {
        val output = Bitmap.createBitmap(
            originBitmap?.width ?: 0, originBitmap?.height ?: 0, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(
            (mX - radius).toInt(), (mY - radius).toInt(),
            (mX + radius).toInt(), (mY + radius).toInt()
        )
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(mX, mY, radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(originBitmap ?: return output, rect, rect, paint)
        return output
    }

    /**
     * Set Ripple color, default is #FFFFFF
     *
     * @param rippleColor New color resource
     */
    fun setRippleColor(@ColorRes rippleColor: Int) {
        this.rippleColor = resources.getColor(rippleColor)
    }

    fun getRippleColor(): Int {
        return rippleColor
    }

    fun getRippleType(): RippleType {
        return RippleType.values()[rippleType]
    }

    /**
     * Set Ripple type, default is RippleType.SIMPLE
     *
     * @param rippleType New Ripple type for next animation
     */
    fun setRippleType(rippleType: RippleType) {
        this.rippleType = rippleType.ordinal
    }

    fun setOnRippleCompleteListener(listener: OnRippleCompleteListener?) {
        onCompletionListener = listener
    }

    /**
     * Defines a callback called at the end of the Ripple effect
     */
    interface OnRippleCompleteListener {
        /**
         * 波纹结束
         */
        fun onComplete(rippleView: RippleView?)
    }

    /**
     * 波纹类型
     */
    enum class RippleType(var type: Int) {
        /**
         * 单波纹
         */
        SIMPLE(0),

        /**
         * 双波纹
         */
        DOUBLE(1),

        /**
         * 方形波纹
         */
        RECTANGLE(2);
    }

    init {
        initAttrs(context, attrs, defStyleAttr)
        initPaint()
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onLongPress(event: MotionEvent) {
                super.onLongPress(event)
                animateRipple(event)
                sendClickEvent(true)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
        @Suppress("DEPRECATION")
        this.isDrawingCacheEnabled = true
        this.isClickable = true
    }
}