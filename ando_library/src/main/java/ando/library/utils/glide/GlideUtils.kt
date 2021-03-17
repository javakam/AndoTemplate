package ando.library.utils.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

/**
 * Title: GlideUtils
 *
 * Description: 图片加载
 *
 * 禁用内存缓存功能
 * diskCacheStrategy()方法基本上就是Glide硬盘缓存功能的一切，它可以接收五种参数：
 *
 * DiskCacheStrategy.NONE： 表示不缓存任何内容。
 * DiskCacheStrategy.DATA： 表示只缓存原始图片。
 * DiskCacheStrategy.RESOURCE： 表示只缓存转换过后的图片。
 * DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。
 * DiskCacheStrategy.AUTOMATIC： 表示让Glide根据图片资源智能地选择使用哪一种缓存策略（默认选项）
 *
 * 功能包括加载图片，圆形图片，圆角图片，指定圆角图片，模糊图片，灰度图片等等
 * 目前只加了这几个常用功能，其他请参考glide-transformations
 * https://github.com/wasabeef/glide-transformations
 *
 * @author javakam
 * @date 2018/10/19  14:46
 */
object GlideUtils {

    /**
     * Glide+RecyclerView卡在placeHolder视图 , 不显示加载成功图片的问题
     * <pre>
     * https://www.cnblogs.com/jooy/p/12186977.html
    </pre> *
     */
    fun noAnimate(placeholder: Int): RequestOptions {
        var options = RequestOptions()
            .centerCrop()
            .dontAnimate()
        if (placeholder > 0) {
            options = options.placeholder(placeholder)
        }
        return options
    }

    fun noAnimate(placeholder: Int, error: Int): RequestOptions {
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

    fun loadImage(imageView: ImageView, url: String?, placeholder: Int) {
        if (url != null && url.startsWith("http")) {
            Glide.with(imageView.context)
                .asBitmap()
                .centerCrop()
                .load(url)
                .apply(noAnimate(placeholder))
        } else {
            Glide.with(imageView.context).asBitmap().centerCrop().load(placeholder)
        }.into(imageView)
    }

    fun loadImage(imageView: ImageView, path: Any?) {
        Glide.with(imageView.context)
            .load(path)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun loadGifImage(imageView: ImageView, path: Any?) {
        Glide.with(imageView.context)
            .asGif()
            .load(path)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, path: Any?, strategy: DiskCacheStrategy?) {
        val options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(strategy!!)
        Glide.with(imageView.context)
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun loadGifImage(imageView: ImageView, path: Any?, strategy: DiskCacheStrategy?) {
        val options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(strategy!!)
        Glide.with(imageView.context)
            .asGif()
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, path: Any?, placeholder: Drawable?) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(placeholder)
        Glide.with(imageView.context)
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .dontAnimate()
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, path: Any?, options: RequestOptions) {
        Glide.with(imageView.context)
            .load(path)
            .apply(options)
            .into(imageView)
    }

    fun loadImage(imageView: ImageView, path: Any?, @DrawableRes placeholder: Int) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(placeholder)
        Glide.with(imageView.context)
            .load(path)
            .apply(options) // .transition(DrawableTransitionOptions.withCrossFade())
            .dontAnimate()
            .into(imageView)
    }

    fun loadImage(
        imageView: ImageView,
        path: Any?,
        placeholder: Drawable?,
        strategy: DiskCacheStrategy?
    ) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(placeholder)
            .diskCacheStrategy(strategy!!)
        Glide.with(imageView.context)
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .dontAnimate()
            .into(imageView)
    }

    fun loadImage(
        imageView: ImageView,
        path: Any?,
        @DrawableRes placeholder: Int,
        strategy: DiskCacheStrategy?
    ) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(placeholder)
            .diskCacheStrategy(strategy!!)
        Glide.with(imageView.context)
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .dontAnimate()
            .into(imageView)
    }

    fun loadGifImage(
        imageView: ImageView,
        path: Any?,
        placeholder: Drawable?,
        strategy: DiskCacheStrategy?
    ) {
        val options = RequestOptions()
            .centerCrop()
            .placeholder(placeholder)
            .diskCacheStrategy(strategy!!)
        Glide.with(imageView.context)
            .asGif()
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    fun loadImage(
        imageView: ImageView,
        path: Any?,
        width: Int,
        height: Int,
        placeholder: Drawable?,
        strategy: DiskCacheStrategy?
    ) {
        val options = RequestOptions()
            .centerCrop()
            .override(width, height)
            .placeholder(placeholder)
            .diskCacheStrategy(strategy!!)
        Glide.with(imageView.context)
            .load(path)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
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

}