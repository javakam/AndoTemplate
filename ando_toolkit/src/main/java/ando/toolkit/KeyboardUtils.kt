package ando.toolkit

import ando.toolkit.ext.DeviceUtils.getNavBarHeight
import ando.toolkit.ext.DeviceUtils.getStatusBarHeight
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import ando.toolkit.ext.inputMethodManager
import ando.toolkit.log.L
import android.annotation.SuppressLint
import android.os.*
import kotlin.math.abs

/**
 * # 输入法管理器
 *
 * From QMUI
 *
 * @author javakam
 */
object KeyboardUtils {

    interface OnSoftInputChangedListener {
        fun onSoftInputChanged(height: Int)
    }

    private const val TAG_ON_GLOBAL_LAYOUT_LISTENER = -8

    private val imm: InputMethodManager by lazy { AppUtils.getContext().inputMethodManager }

    fun isSoftInputActive(): Boolean = imm.isActive

    fun showSoftInput() {
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun showSoftInput(activity: Activity) {
        if (!isSoftInputVisible(activity)) {
            toggleSoftInput()
        }
    }

    /**
     * Show the soft input.
     *
     * @param view  The view.
     * @param flags Provides additional operating flags.  Currently may be
     * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
     */
    fun showSoftInput(view: View, flags: Int = 0) {
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        imm.showSoftInput(view, flags, object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
                    || resultCode == InputMethodManager.RESULT_HIDDEN
                ) toggleSoftInput()
            }
        })
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideSoftInput(activity: Activity) = hideSoftInput(activity.window)

    fun hideSoftInput(window: Window) {
        var view = window.currentFocus
        if (view == null) {
            val decorView = window.decorView
            val focusView = decorView.findViewWithTag<View>("keyboardTagView")
            if (focusView == null) {
                view = EditText(window.context)
                view.setTag("keyboardTagView")
                (decorView as ViewGroup).addView(view, 0, 0)
            } else {
                view = focusView
            }
            view.requestFocus()
        }
        hideSoftInput(view)
    }

    fun hideSoftInput(view: View) {
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private var millis: Long = 0

    fun hideSoftInputByToggle(activity: Activity) {
        val nowMillis = System.currentTimeMillis()
        val delta = nowMillis - millis
        if (abs(delta) > 500 && isSoftInputVisible(activity)) {
            toggleSoftInput()
        }
        millis = nowMillis
    }

    /**
     * Toggle the soft input display or not.
     */
    fun toggleSoftInput() {
        imm.toggleSoftInput(0, 0)
    }

    private var sDecorViewDelta = 0

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSoftInputVisible(activity: Activity): Boolean {
        return getDecorViewInvisibleHeight(activity.window) > 0
    }

    private fun getDecorViewInvisibleHeight(window: Window): Int {
        val decorView = window.decorView
        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        Log.d(
            "KeyboardUtils", "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom)
        )
        val delta = abs(decorView.bottom - outRect.bottom)
        if (delta <= getNavBarHeight() + getStatusBarHeight()) {
            sDecorViewDelta = delta
            return 0
        }
        return delta - sDecorViewDelta
    }

    /**
     * Register soft input changed listener.
     *
     * @param activity The activity.
     * @param listener The soft input changed listener.
     */
    fun registerSoftInputChangedListener(
        activity: Activity,
        listener: OnSoftInputChangedListener
    ) {
        registerSoftInputChangedListener(activity.window, listener)
    }

    /**
     * Register soft input changed listener.
     *
     * @param window   The window.
     * @param listener The soft input changed listener.
     */
    fun registerSoftInputChangedListener(
        window: Window,
        listener: OnSoftInputChangedListener
    ) {
        val flags = window.attributes.flags
        if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val contentView = window.findViewById<FrameLayout>(R.id.content)
        val decorViewInvisibleHeightPre = intArrayOf(getDecorViewInvisibleHeight(window))
        val onGlobalLayoutListener = OnGlobalLayoutListener {
            val height = getDecorViewInvisibleHeight(window)
            if (decorViewInvisibleHeightPre[0] != height) {
                listener.onSoftInputChanged(height)
                decorViewInvisibleHeightPre[0] = height
            }
        }
        contentView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
        contentView.setTag(TAG_ON_GLOBAL_LAYOUT_LISTENER, onGlobalLayoutListener)
    }

    /**
     * Unregister soft input changed listener.
     *
     * @param window The window.
     */
    fun unregisterSoftInputChangedListener(window: Window) {
        val contentView = window.findViewById<FrameLayout>(R.id.content)
        val tag = contentView.getTag(TAG_ON_GLOBAL_LAYOUT_LISTENER)
        if (tag is OnGlobalLayoutListener) {
            @SuppressLint("ObsoleteSdkInt")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentView.viewTreeObserver.removeOnGlobalLayoutListener(tag)
            }
        }
    }

    /**
     * Fix the bug of 5497 in Android.
     *
     * Don't set adjustResize
     *
     * @param activity The activity.
     */
    fun fixAndroidBug5497(activity: Activity) {
        fixAndroidBug5497(activity.window)
    }

    /**
     * Fix the bug of 5497 in Android.
     *
     * It will clean the adjustResize
     *
     * @param window The window.
     */
    fun fixAndroidBug5497(window: Window) {
        val softInputMode = window.attributes.softInputMode
        @Suppress("DEPRECATION")
        window.setSoftInputMode(softInputMode and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE.inv())
        val contentView = window.findViewById<FrameLayout>(R.id.content)
        val contentViewChild = contentView.getChildAt(0)
        val paddingBottom = contentViewChild.paddingBottom
        val contentViewInvisibleHeightPre5497 = intArrayOf(getContentViewInvisibleHeight(window))
        contentView.viewTreeObserver
            .addOnGlobalLayoutListener {
                val height = getContentViewInvisibleHeight(window)
                if (contentViewInvisibleHeightPre5497[0] != height) {
                    contentViewChild.setPadding(
                        contentViewChild.paddingLeft,
                        contentViewChild.paddingTop,
                        contentViewChild.paddingRight,
                        paddingBottom + getDecorViewInvisibleHeight(window)
                    )
                    contentViewInvisibleHeightPre5497[0] = height
                }
            }
    }

    private fun getContentViewInvisibleHeight(window: Window): Int {
        val contentView = window.findViewById<View>(R.id.content) ?: return 0
        val outRect = Rect()
        contentView.getWindowVisibleDisplayFrame(outRect)
        L.d("getContentViewInvisibleHeight: " + (contentView.bottom - outRect.bottom))
        val delta = abs(contentView.bottom - outRect.bottom)
        return if (delta <= getStatusBarHeight() + getNavBarHeight()) 0 else delta
    }

    /**
     * Fix the leaks of soft input.
     *
     * @param activity The activity.
     */
    fun fixSoftInputLeaks(activity: Activity) = fixSoftInputLeaks(activity.window)

    /**
     * Fix the leaks of soft input.
     *
     * @param window The window.
     */
    fun fixSoftInputLeaks(window: Window) {
        val imm = AppUtils.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            try {
                val leakViewField = InputMethodManager::class.java.getDeclaredField(leakView)
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField[imm] as? View ?: continue
                if (obj.rootView === window.decorView.rootView) {
                    leakViewField[imm] = null
                }
            } catch (ignore: Throwable) {
            }
        }
    }

}