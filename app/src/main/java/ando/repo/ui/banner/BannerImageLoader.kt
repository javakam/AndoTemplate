package ando.repo.ui.banner

import ando.widget.banner.IBannerImageLoadStrategy
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * # Banner 图片加载器
 *
 * @author javakam
 * @date 2021/3/23  13:32
 */
class BannerImageLoader : IBannerImageLoadStrategy {
    override fun loadImage(imageView: ImageView, path: Any?) {
        val options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        Glide.with(imageView.context)
            .asBitmap()
            .load(path)
            .apply(options)
            .centerCrop()
            .into(imageView)
    }

    override fun loadImage(imageView: ImageView, path: Any?, placeholder: Drawable?) {
        val options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        Glide.with(imageView.context)
            .asBitmap()
            .load(path)
            .apply(options)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }

    override fun loadImage(imageView: ImageView, path: Any?, placeholder: Int) {
        val options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        Glide.with(imageView.context)
            .asBitmap()
            .load(path)
            .apply(options)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }

    override fun loadImage(
        imageView: ImageView,
        path: Any?,
        width: Int,
        height: Int,
        placeholder: Drawable?
    ) {
        val options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)

        Glide.with(imageView.context)
            .asBitmap()
            .load(path)
            .apply(options)
            .override(width, height)
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }
}