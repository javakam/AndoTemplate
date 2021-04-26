package ando.library.widget.float

import ando.toolkit.ext.DeviceUtils.getStatusBarHeight
import ando.toolkit.ext.dp2px
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * 悬浮窗控件（解决滑动冲突）
 *
 * val mFloatView = FloatView(applicationContext, 0, 0)
 * mFloatView.addView(layoutInflater.inflate(R.layout.layout_float, null, false))
 * mFloatView.addToWindow()
 *
 * mFloatView.removeAllViews()
 * mFloatView.removeFromWindow()
 */
@SuppressLint("ViewConstructor")
class FloatView(
    context: Context,
    private var mDownX: Int = 20, //手指按下时相对于悬浮窗的坐标
    private var mDownY: Int = 20  //手指按下时相对于屏幕的坐标
) : FrameLayout(context) {
    private var mWindowManager: WindowManager? = null
    private lateinit var mParams: WindowManager.LayoutParams
    private var mDownRawX = 0
    private var mDownRawY = 0

    private fun initWindow() {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        // 设置图片格式，效果为背景透明
        mParams.format = PixelFormat.TRANSLUCENT
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mParams.gravity = Gravity.START or Gravity.TOP // 调整悬浮窗口至右下角
        // 设置悬浮窗口长宽数据
        val width = dp2px(65F)
        mParams.width = width
        mParams.height = width
        mParams.x = mDownX
        mParams.y = mDownY
    }

    /**
     * 添加至窗口
     */
    @SuppressLint("ObsoleteSdkInt")
    fun addToWindow(): Boolean {
        return if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isAttachedToWindow) {
                    mWindowManager?.addView(this, mParams)
                    true
                } else false
            } else {
                try {
                    if (parent == null) {
                        mWindowManager?.addView(this, mParams)
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }
        } else false
    }

    /**
     * 从窗口移除
     */
    @SuppressLint("ObsoleteSdkInt")
    fun removeFromWindow(): Boolean {
        return if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow) {
                    mWindowManager?.removeViewImmediate(this)
                    true
                } else false
            } else {
                try {
                    if (parent != null) {
                        mWindowManager?.removeViewImmediate(this)
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }
        } else false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = false
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                mDownRawX = ev.rawX.toInt()
                mDownRawY = ev.rawY.toInt()
                mDownX = ev.x.toInt()
                mDownY = (ev.y + getStatusBarHeight()).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val absDeltaX = abs(ev.rawX - mDownRawX)
                val absDeltaY = abs(ev.rawY - mDownRawY)
                intercepted = absDeltaX > ViewConfiguration.get(context).scaledTouchSlop ||
                        absDeltaY > ViewConfiguration.get(context).scaledTouchSlop
            }
            else -> {
            }
        }
        return intercepted
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            mParams.x = x - mDownX
            mParams.y = y - mDownY
            mWindowManager?.updateViewLayout(this, mParams)
        } else if (event.action == MotionEvent.ACTION_UP) {
            //Log.i("2333", "mDownX =$mDownX  mDownY =$mDownY")
            UserSetting.setFloatPosition(context, mDownX, mDownY)
        }
        return super.onTouchEvent(event)
    }

    init {
        setPadding(0, 0, 0, 0)
        UserSetting.getFloatPosition(context).let {
            mDownX = it.mDownX
            mDownY = it.mDownY
        }
        initWindow()
    }
}