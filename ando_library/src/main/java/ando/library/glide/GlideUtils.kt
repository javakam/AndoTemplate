package ando.library.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions

/**
 * # GlideUtils
 *
 * 禁用内存缓存功能
 * diskCacheStrategy()方法基本上就是Glide硬盘缓存功能的一切，它可以接收五种参数：
 *
 * DiskCacheStrategy.NONE 表示不缓存任何内容
 * DiskCacheStrategy.DATA 表示只缓存原始图片
 * DiskCacheStrategy.RESOURCE  表示只缓存转换过后的图片
 * DiskCacheStrategy.ALL       表示既缓存原始图片，也缓存转换过后的图片
 * DiskCacheStrategy.AUTOMATIC 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）
 *
 * 圆形图片，圆角图片，指定圆角图片，模糊图片，灰度图片 :
 * https://github.com/wasabeef/glide-transformations
 *
 * @author javakam
 * @date 2018/10/19  14:46
 */
object GlideUtils {

    /**
     * Glide+RecyclerView卡在placeHolder视图 , 不显示加载成功图片的问题
     *
     * https://www.cnblogs.com/jooy/p/12186977.html
     */
    fun noAnimate(placeholder: Int = -1): RequestOptions {
        var options = RequestOptions()
            .centerCrop()
            .dontAnimate()

        if (placeholder > 0) {
            options = options.placeholder(placeholder)
        }
        return options
    }

    fun noAnimate(placeholder: Int = -1, error: Int = -1): RequestOptions {
        var options = RequestOptions()
            .centerCrop()
            .dontAnimate()

        if (placeholder > 0) {
            options = options.placeholder(placeholder)
        }
        if (error > 0) {
            options = options.error(error)
        }
        return options
    }

    fun loadImage(iv: ImageView, url: String?, placeholder: Int = -1) {
        if (url != null && url.startsWith("http")) {
            Glide.with(iv.context)
                .asBitmap()
                .centerCrop()
                .load(url)
                .apply(noAnimate(placeholder))
        } else {
            Glide.with(iv.context)
                .asBitmap()
                .centerCrop()
                .load(placeholder)
                .transition(BitmapTransitionOptions.withCrossFade())
        }.into(iv)
    }

    fun loadImage(iv: ImageView, path: Any?) {
        loadImage(iv, path, placeholder = null, error = null, strategy = null, options = null).into(iv)
    }

    fun loadImage(iv: ImageView, path: Any?, strategy: DiskCacheStrategy?) {
        loadImage(iv, path, placeholder = null, error = null, strategy = strategy, options = null).into(iv)
    }

    fun loadImage(iv: ImageView, path: Any?, placeholder: Drawable?) {
        loadImage(iv, path, placeholder = placeholder, error = null, strategy = null, options = null).into(iv)
    }

    fun loadImage(iv: ImageView, path: Any?, placeholder: Drawable?, strategy: DiskCacheStrategy?) {
        loadImage(iv, path, placeholder = placeholder, error = null, strategy = strategy, options = null).into(iv)
    }

    fun loadImage(iv: ImageView, path: Any?, @DrawableRes placeholder: Int = -1) {
        loadImage(
            iv, path, placeholder = getDrawable(iv, placeholder),
            error = null, strategy = null, options = null
        ).into(iv)
    }

    fun loadImage(iv: ImageView, path: Any?, @DrawableRes placeholder: Int, strategy: DiskCacheStrategy?) {
        loadImage(
            iv, path, placeholder = getDrawable(iv, placeholder),
            error = null, strategy = strategy, options = null
        ).into(iv)
    }

    fun loadImage(
        iv: ImageView, path: Any?, width: Int, height: Int,
        placeholder: Drawable?, strategy: DiskCacheStrategy?
    ) {
        loadImage(iv, path, placeholder = placeholder, error = null, strategy = strategy, options = null)
            .override(width, height)
            .into(iv)
    }

    fun loadImage(
        iv: ImageView, path: Any?, placeholder: Drawable?, error: Drawable?,
        strategy: DiskCacheStrategy?, options: RequestOptions?
    ): RequestBuilder<Bitmap> {
        var builder: RequestBuilder<Bitmap> = Glide.with(iv.context)
            .asBitmap()
            .centerCrop()
            .placeholder(placeholder)
            .error(error)
            .load(path)
            .transition(BitmapTransitionOptions.withCrossFade())

        var realOptions = options ?: RequestOptions()
            .centerCrop()
            .dontAnimate()

        if (strategy != null) realOptions = realOptions.diskCacheStrategy(strategy)

        builder = builder.apply(realOptions)
        return builder
    }

    fun loadGif(iv: ImageView, path: Any?) {
        loadGif(iv, path, placeholder = null, error = null, strategy = null, options = null).into(iv)
    }

    fun loadGif(iv: ImageView, path: Any?, strategy: DiskCacheStrategy) {
        loadGif(iv, path, placeholder = null, error = null, strategy = strategy, options = null).into(iv)
    }

    fun loadGif(iv: ImageView, path: Any?, placeholder: Drawable?, strategy: DiskCacheStrategy) {
        loadGif(iv, path, placeholder = placeholder, error = null, strategy = strategy, options = null).into(iv)
    }

    fun loadGif(
        iv: ImageView, path: Any?, placeholder: Drawable?, error: Drawable?,
        strategy: DiskCacheStrategy?, options: RequestOptions?
    ): RequestBuilder<GifDrawable> {
        var builder: RequestBuilder<GifDrawable> = Glide.with(iv.context)
            .asGif()
            .centerCrop()
            .placeholder(placeholder)
            .error(error)
            .load(path)
            .transition(DrawableTransitionOptions.withCrossFade())

        var realOptions = options ?: RequestOptions()
            .centerCrop()
            .dontAnimate()

        if (strategy != null) realOptions = realOptions.diskCacheStrategy(strategy)

        builder = builder.apply(realOptions)
        return builder
    }

    fun clearCache(context: Context) {
        Glide.get(context).clearMemory()
        Glide.get(context).clearDiskCache()
    }

    fun clearMemoryCache(context: Context) {
        Glide.get(context).clearMemory()
    }

    fun clearDiskCache(context: Context) {
        Glide.get(context).clearDiskCache()
    }

    private fun getDrawable(@NonNull iv: ImageView, @DrawableRes placeholder: Int = -1): Drawable? =
        if (placeholder != -1) ContextCompat.getDrawable(iv.context, placeholder) else null

}