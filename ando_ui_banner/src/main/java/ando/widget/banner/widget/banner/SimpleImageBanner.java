package ando.widget.banner.widget.banner;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

import ando.widget.banner.R;
import ando.widget.banner.widget.banner.base.BaseIndicatorBanner;

/**
 * 简单的图片轮播
 * <p>
 * https://github.com/xuexiangjys/XUI/wiki/Banner
 * </p>
 * <pre>
 *     注意:使用时外面包一层FrameLayout,不要裸奔,否则会出现高度异常
 * </pre>
 *
 * @author javakam
 * @date 2019/1/14 下午10:07
 */
public class SimpleImageBanner extends BaseIndicatorBanner<BannerItem, SimpleImageBanner> {
    /**
     * 默认加载图片
     */
    private Drawable mColorDrawable;
    /**
     * 是否允许进行缓存
     */
    private boolean mEnableCache;
    /**
     * 高／宽比率
     */
    private double mScale;

    public SimpleImageBanner(Context context) {
        super(context);
        initImageBanner(context);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageBanner(context);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initImageBanner(context);
    }

    /**
     * 初始化ImageBanner
     */
    protected void initImageBanner(Context context) {
        mColorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.default_image_banner_placeholder_color));
        mEnableCache = true;
        mScale = getContainerScale();
    }

    @Override
    public void onTitleSelect(TextView tv, int position) {
        final BannerItem item = getItem(position);
        if (item != null) {
            tv.setText(item.title);
        }
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = inflate(mContext, R.layout.ando_adapter_simple_image, null);
        ImageView iv = inflate.findViewById(R.id.iv);

        //解决Glide资源释放的问题，详细见 http://blog.csdn.net/shangmingchao/article/details/51125554
        WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(iv);
        ImageView target = imageViewWeakReference.get();

        BannerItem item = getItem(position);
        if (item != null && target != null) {
            loadingImageView(target, item);
        }
        return inflate;
    }

    /**
     * 加载图片
     */
    protected void loadingImageView(ImageView iv, BannerItem item) {
        int itemWidth = mDisplayMetrics.widthPixels;
        int itemHeight = (int) (itemWidth * mScale);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));

        String imgUrl = item.imgUrl;

        if (!TextUtils.isEmpty(imgUrl)) {
            //todo 2021年3月22日 17:04:56
//            ImageLoader.get().loadImage(iv, imgUrl,
//                    itemWidth, itemHeight, mColorDrawable,
//                    mEnableCache ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.NONE);
        } else {
            iv.setImageDrawable(mColorDrawable);
        }
    }

    /**
     * 设置是否允许缓存
     */
    public SimpleImageBanner enableCache(boolean enableCache) {
        mEnableCache = enableCache;
        return this;
    }

    /**
     * 获取是否允许缓存
     */
    public boolean getEnableCache() {
        return mEnableCache;
    }

    public Drawable getColorDrawable() {
        return mColorDrawable;
    }

    public SimpleImageBanner setColorDrawable(Drawable colorDrawable) {
        mColorDrawable = colorDrawable;
        return this;
    }

    public double getScale() {
        return mScale;
    }

    public SimpleImageBanner setScale(double scale) {
        mScale = scale;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        //解决内存泄漏的问题
        pauseScroll();
        super.onDetachedFromWindow();
    }
}