package ando.toolkit.ext

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import ando.toolkit.NoShakeClickListener
import android.annotation.SuppressLint

/**
 * 为view添加OnGlobalLayoutListener监听并在测量完成后自动取消监听同时执行[globalAction]函数
 *
 * @author javakam
 * @date 2020/9/30  15:16
 */
inline fun <T : View> T.afterMeasured(crossinline globalAction: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                @SuppressLint("ObsoleteSdkInt")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    @Suppress("DEPRECATION")
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                globalAction()
            }
        }
    })
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

var View.leftPadding: Int
    inline get() = paddingLeft
    set(value) = setPadding(value, paddingTop, paddingRight, paddingBottom)

var View.topPadding: Int
    inline get() = paddingTop
    set(value) = setPadding(paddingLeft, value, paddingRight, paddingBottom)

var View.rightPadding: Int
    inline get() = paddingRight
    set(value) = setPadding(paddingLeft, paddingTop, value, paddingBottom)

var View.bottomPadding: Int
    inline get() = paddingBottom
    set(value) = setPadding(paddingLeft, paddingTop, paddingRight, value)

inline var TextView.isSelectable: Boolean
    get() = isTextSelectable
    set(value) = setTextIsSelectable(value)


fun View.visibleOrGone(visible: Boolean) {
    this.run {
        if (visible) {
            if (!isVisible) visibility = View.VISIBLE
        } else {
            if (isVisible) visibility = View.GONE
        }
    }
}

fun View.visibleOrInvisible(visible: Boolean) {
    this.run {
        if (visible) {
            if (!isVisible) visibility = View.VISIBLE
        } else {
            if (isVisible) visibility = View.INVISIBLE
        }
    }
}

fun View.visible() {
    this.run {
        if (!isVisible) visibility = View.VISIBLE
    }
}

fun View.invisible() {
    this.run {
        if (isVisible) visibility = View.INVISIBLE
    }
}

fun View.gone() {
    this.run {
        if (isVisible) visibility = View.GONE
    }
}

fun View.noShake(interval: Long = 500L, block: (v: View?) -> Unit) {
    this.apply {
        setOnClickListener(object : NoShakeClickListener(interval) {
            override fun onSingleClick(v: View) {
                block.invoke(v)
            }
        })
    }
}