package ando.repo.ui.banner

import ando.widget.banner.IBannerImageLoadStrategy
import android.graphics.drawable.Drawable
import android.widget.ImageView

/**
 * # Banner 图片加载器
 *
 * @author javakam
 * @date 2021/3/23  13:32
 */
class BannerImageLoader : IBannerImageLoadStrategy {
    override fun loadImage(imageView: ImageView, path: Any?) {

    }

    override fun loadImage(imageView: ImageView, path: Any?, placeholder: Drawable?) {

    }

    override fun loadImage(imageView: ImageView, path: Any?, placeholder: Int) {
    }

    override fun loadImage(
        imageView: ImageView,
        path: Any?,
        width: Int,
        height: Int,
        placeholder: Drawable?
    ) {

    }
}