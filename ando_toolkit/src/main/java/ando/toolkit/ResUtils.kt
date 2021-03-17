package ando.toolkit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import ando.toolkit.AppUtils.getContext
import kotlin.math.max

/**
 * # ResUtils 获取res中的资源
 *
 * @author javakam
 * @date 2018/12/18 上午12:14
 */
object ResUtils {
    /**
     * 获取resources对象
     */
    private val resources: Resources get() = getContext().resources

    /**
     * 获取字符串
     */
    fun getString(@StringRes resId: Int): String = resources.getString(resId)

    /**
     * 获取资源图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getContext().getDrawable(resId)
        } else resources.getDrawable(resId)
    }

    /**
     * 获取资源图片【和主体有关】
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(resId)
        } else context.resources.getDrawable(resId)
    }

    /**
     * 获取svg资源图片
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getVectorDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(resId)
        } else AppCompatResources.getDrawable(context, resId)
    }

    /**
     * 获取Drawable属性（兼容VectorDrawable）
     */
    fun getDrawableAttrRes(
        context: Context?,
        typedArray: TypedArray,
        @StyleableRes index: Int
    ): Drawable? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return typedArray.getDrawable(index)
        } else {
            val resourceId = typedArray.getResourceId(index, -1)
            if (resourceId != -1) {
                return AppCompatResources.getDrawable(context!!, resourceId)
            }
        }
        return null
    }

    /**
     * 获取dimes值，返回的是精确的值
     */
    fun getDimens(@DimenRes resId: Int): Float = resources.getDimension(resId)

    /**
     * 获取Color值
     */
    fun getColor(@ColorRes resId: Int): Int = resources.getColor(resId)

    /**
     * 获取ColorStateList值
     */
    @SuppressLint("UseCompatLoadingForColorStateLists")
    fun getColors(@ColorRes resId: Int): ColorStateList =
        resources.getColorStateList(resId)

    /**
     * 获取dimes值，返回的是【去余取整】的值
     */
    fun getDimensionPixelOffset(@DimenRes resId: Int): Int =
        resources.getDimensionPixelOffset(resId)

    /**
     * 获取dimes值，返回的是【4舍5入】的值
     */
    fun getDimensionPixelSize(@DimenRes resId: Int): Int =
        resources.getDimensionPixelSize(resId)

    /**
     * 获取字符串的数组
     */
    fun getStringArray(@ArrayRes resId: Int): Array<String> = resources.getStringArray(resId)

    /**
     * 获取Drawable的数组
     */
    fun getDrawableArray(context: Context?, @ArrayRes resId: Int): Array<Drawable?> {
        val ta = resources.obtainTypedArray(resId)
        val icons = arrayOfNulls<Drawable>(ta.length())
        for (i in 0 until ta.length()) {
            val id = ta.getResourceId(i, 0)
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(context!!, id)
            }
        }
        ta.recycle()
        return icons
    }

    /**
     * 获取数字的数组
     */
    fun getIntArray(@ArrayRes resId: Int): IntArray = resources.getIntArray(resId)

    /**
     * 获取动画
     */
    fun getAnim(@AnimRes resId: Int): Animation = AnimationUtils.loadAnimation(getContext(), resId)

    /**
     * Check if layout direction is RTL
     *
     * @return `true` if the layout direction is right-to-left
     */
    val isRtl: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

    /**
     * Darkens a color by a given factor.
     *
     * @param color  the color to darken
     * @param factor The factor to darken the color.
     * @return darker version of specified color.
     */
    fun darker(color: Int, factor: Float): Int =
        Color.argb(
            Color.alpha(color), max(
                (Color.red(color) * factor).toInt(), 0
            ),
            max((Color.green(color) * factor).toInt(), 0),
            max((Color.blue(color) * factor).toInt(), 0)
        )

    /**
     * Lightens a color by a given factor.
     *
     * @param color  The color to lighten
     * @param factor The factor to lighten the color. 0 will make the color unchanged. 1 will make the
     * color white.
     * @return lighter version of the specified color.
     */
    fun lighter(color: Int, factor: Float): Int {
        val red = ((Color.red(color) * (1 - factor) / 255 + factor) * 255).toInt()
        val green = ((Color.green(color) * (1 - factor) / 255 + factor) * 255).toInt()
        val blue = ((Color.blue(color) * (1 - factor) / 255 + factor) * 255).toInt()
        return Color.argb(Color.alpha(color), red, green, blue)
    }
}