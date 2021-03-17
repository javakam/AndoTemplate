package ando.toolkit

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * 工具类（不建议外部调用)
 *
 * @author javakam
 * @date 2018/11/26 下午5:07
 */
object Utils {
    /**
     * View 设备背景
     */
    fun setBackground(context: Context, v: View, res: Int) {
        val bm = BitmapFactory.decodeResource(context.resources, res)
        val bd = BitmapDrawable(context.resources, bm)
        v.background = bd
    }

    /**
     * 释放图片资源
     */
    fun recycleBackground(v: View) {
        val d = v.background
        //别忘了把背景设为null，避免onDraw刷新背景时候出现used a recycled bitmap错误
        v.setBackgroundResource(0)
        if (d != null && d is BitmapDrawable) {
            val bmp = d.bitmap
            if (bmp != null && !bmp.isRecycled) {
                bmp.recycle()
            }
        }
        if (d != null) {
            d.callback = null
        }
    }

    /**
     * 遍历View,清除所有ImageView的缓存
     */
    fun clearImageView(view: View?) {
        if (view is ViewGroup) {
            val count = view.childCount
            for (i in 0 until count) {
                clearImageView(view.getChildAt(i))
            }
        } else if (view is ImageView) {
            clearImageMemory(view)
        }
    }

    /**
     * 清空图片的内存
     */
    fun clearImageMemory(imageView: ImageView) {
        val d = imageView.drawable
        if (d is BitmapDrawable) {
            val bmp = d.bitmap
            if (bmp != null && !bmp.isRecycled) bmp.recycle()
        }
        imageView.setImageBitmap(null)
        if (d != null) {
            d.callback = null
        }
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap 源 Bitmap
     * @param w      宽
     * @param h      高
     * @return 目标Bitmap
     */
    fun zoom(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 要旋转的图片
     * @return 旋转后的图片
     */
    fun rotate(bitmap: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Drawable -> Bitmap
     */
    fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(
            width, height, if (drawable.opacity != PixelFormat.OPAQUE
            ) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Drawable -> Bitmap
     */
    fun getBitmapFromDrawable(drawable: Drawable, color: Int): Bitmap {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        @Suppress("DEPRECATION")
        var bitmap = Bitmap.createBitmap(
            width, height,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        var canvas = Canvas(bitmap)
        canvas.drawColor(color, PorterDuff.Mode.SRC_IN)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(bitmap)
        canvas.drawColor(color, PorterDuff.Mode.SRC_IN)
        return bitmap
    }

    /**
     * 支持?attrs属性  http://stackoverflow.com/questions/27986204  ：As mentioned here on API < 21 you can't use attrs to color in xml drawable.
     */
    fun isSupportColorAttrs(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    fun isLight(color: Int): Boolean =
        sqrt(
            Color.red(color) * Color.red(color) * .241 +
                    Color.green(color) * Color.green(color) * .691 +
                    Color.blue(color) * Color.blue(color) * .068
        ) > 130

    /**
     * 获取数值的位数，例如9返回1，99返回2，999返回3
     *
     * @param number 要计算位数的数值，必须>0
     * @return 数值的位数，若传的参数小于等于0，则返回0
     */
    fun getNumberDigits(number: Int): Int =
        if (number <= 0) 0 else (log10(number.toDouble()) + 1).toInt()

    /**
     * 设置Drawable的颜色
     * <p>
     * **这里不对Drawable进行mutate()，会影响到所有用到这个Drawable的地方，如果要避免，请先自行mutate()**
     */
    fun setDrawableTintColor(drawable: Drawable?, @ColorInt tintColor: Int): ColorFilter {
        val colorFilter = LightingColorFilter(Color.argb(255, 0, 0, 0), tintColor)
        if (drawable != null) {
            drawable.colorFilter = colorFilter
        }
        return colorFilter
    }

}