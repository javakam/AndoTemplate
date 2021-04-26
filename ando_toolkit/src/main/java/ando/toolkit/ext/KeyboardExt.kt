package ando.toolkit.ext

import ando.toolkit.KeyboardUtils
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

/**
 * 软键盘操作
 *
 * @author javakam
 * @date 2020/9/29  15:04
 */

/* ---------- Context ---------- */

fun Context.showSoftInput() {
    if (this is Activity) KeyboardUtils.showSoftInput(this)
    else KeyboardUtils.showSoftInput()
}

fun Context.showSoftInput(view: View) = KeyboardUtils.showSoftInput(view)

fun Context.hideSoftInput(activity: Activity) = KeyboardUtils.hideSoftInput(activity.window)

fun Context.hideSoftInput(view: View) = KeyboardUtils.hideSoftInput(view)

fun Context.isSoftInputActive(): Boolean = KeyboardUtils.isSoftInputActive()

/* ---------- Fragment ---------- */

fun Fragment.showSoftInput(view: View) {
    activity?.showSoftInput(view)
}

fun Fragment.hideSoftInput(view: View) {
    activity?.hideSoftInput(view)
}

fun Fragment.hideSoftInput() {
    activity?.hideSoftInput(requireActivity())
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