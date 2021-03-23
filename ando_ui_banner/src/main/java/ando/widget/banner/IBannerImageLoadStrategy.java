package ando.widget.banner;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * 图片加载策略
 *
 * @author javakam
 * @date 2019-07-26 00:06
 */
public interface IBannerImageLoadStrategy {

    void loadImage(@NonNull ImageView imageView, Object path);

    void loadImage(@NonNull ImageView imageView, Object path, Drawable placeholder);

    void loadImage(@NonNull ImageView imageView, Object path, @DrawableRes int placeholder);

    /**
     * 加载指定宽高的图片
     */
    void loadImage(@NonNull ImageView imageView, Object path, int width, int height, Drawable placeholder);

}