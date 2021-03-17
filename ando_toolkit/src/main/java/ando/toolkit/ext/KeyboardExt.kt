package ando.toolkit.ext

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * 软键盘操作
 *
 * @author javakam
 * @date 2020/9/29  15:04
 */

/* ---------- Context ---------- */

private val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun Context.showSoftInput(view: View) {
    view.isFocusable = true
    view.isFocusableInTouchMode = true
    view.requestFocus()
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

fun Context.hideSoftInput(view: View) {
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.hideSoftInput(activity: Activity) {
    val view: View = activity.currentFocus ?: activity.window.decorView
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.isSoftInputActive(): Boolean {
    return inputMethodManager.isActive ?: false
}

/* ---------- Fragment ---------- */

fun Fragment.showSoftInput(view: View) {
    activity?.showSoftInput(view)
}

fun Fragment.hideSoftInput(view: View) {
    activity?.hideSoftInput(view)
}

fun Fragment.hideSoftInput() {
    activity?.hideSoftInput(activity!!)
}

fun Fragment.isSoftInputActive() {
    activity?.isSoftInputActive()
}

/* ---------- View ---------- */

fun View.showSoftInput(view: View) {
    context.showSoftInput(view)
}

fun View.hideSoftInput(view: View) {
    context.hideSoftInput(view)
}

fun View.isSoftInputActive() {
    context.isSoftInputActive()
}