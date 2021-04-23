package ando.library.widget.switcher

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources

internal object SwitchButtonUtils {
    private const val ENABLE_ATTR = android.R.attr.state_enabled
    private const val CHECKED_ATTR = android.R.attr.state_checked
    private const val PRESSED_ATTR = android.R.attr.state_pressed

    fun generateThumbColorWithTintColor(tintColor: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(-ENABLE_ATTR, CHECKED_ATTR), intArrayOf(-ENABLE_ATTR), intArrayOf(
                PRESSED_ATTR, -CHECKED_ATTR
            ), intArrayOf(PRESSED_ATTR, CHECKED_ATTR), intArrayOf(CHECKED_ATTR), intArrayOf(-CHECKED_ATTR)
        )
        val colors = intArrayOf(
            tintColor - -0x56000000,
            -0x454546,
            tintColor - -0x67000000,
            tintColor - -0x67000000,
            tintColor or -0x1000000,
            -0x111112
        )
        return ColorStateList(states, colors)
    }

    fun generateBackColorWithTintColor(tintColor: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(-ENABLE_ATTR, CHECKED_ATTR), intArrayOf(-ENABLE_ATTR), intArrayOf(
                CHECKED_ATTR, PRESSED_ATTR
            ), intArrayOf(-CHECKED_ATTR, PRESSED_ATTR), intArrayOf(CHECKED_ATTR), intArrayOf(-CHECKED_ATTR)
        )
        val colors = intArrayOf(
            tintColor - -0x1f000000,
            0x10000000,
            tintColor - -0x30000000,
            0x20000000,
            tintColor - -0x30000000,
            0x20000000
        )
        return ColorStateList(states, colors)
    }

    /**
     * 获取Drawable属性（兼容VectorDrawable）
     */
    fun getDrawableAttrRes(
        context: Context,
        typedArray: TypedArray,
        @StyleableRes index: Int
    ): Drawable? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return typedArray.getDrawable(index)
        } else {
            val resourceId = typedArray.getResourceId(index, -1)
            if (resourceId != -1) {
                return AppCompatResources.getDrawable(context, resourceId)
            }
        }
        return null
    }
}