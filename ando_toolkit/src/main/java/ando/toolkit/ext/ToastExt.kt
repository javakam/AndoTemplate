package ando.toolkit.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ando.toolkit.AppUtils.getContext

/**
 * Title: ToastExt
 *
 * Description:
 *
 * @author javakam
 * @date 2019/1/19
 */

fun Context.toastShort(text: String?) = ToastUtils.shortToast(text)
fun Context.toastLong(text: String?) = ToastUtils.longToast(text)
fun Context.toastShort(@StringRes resId: Int) = ToastUtils.shortToast(resId)
fun Context.toastLong(@StringRes resId: Int) = ToastUtils.longToast(resId)

fun Fragment.toastShort(text: String?) = activity?.toastShort(text)
fun Fragment.toastLong(text: String?) = activity?.toastLong(text)
fun Fragment.toastShort(@StringRes resId: Int) = activity?.toastShort(resId)
fun Fragment.toastLong(@StringRes resId: Int) = activity?.toastLong(resId)

fun View.toastShort(text: String?) = context?.toastShort(text)
fun View.toastLong(text: String?) = context?.toastLong(text)
fun View.toastShort(@StringRes resId: Int) = context?.toastShort(resId)
fun View.toastLong(@StringRes resId: Int) = context?.toastLong(resId)

object ToastUtils {

    private var toast: Toast? = null

    fun shortToast(@StringRes resId: Int) {
        shortToast(getContext().resources.getString(resId))
    }

    fun shortToast(text: String?) {
        if (text.isNullOrBlank())  return
        if (toast == null) {
            @SuppressLint("ShowToast")
            toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT)
        } else {
            toast?.setText(text)
        }
        toast?.show()
    }

    fun longToast(@StringRes resId: Int) {
        longToast(getContext().resources.getString(resId))
    }

    fun longToast(text: String?) {
        if (text.isNullOrBlank())  return
        if (toast == null) {
            @SuppressLint("ShowToast")
            toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG)
        } else {
            toast?.setText(text)
        }
        toast?.show()
    }

}