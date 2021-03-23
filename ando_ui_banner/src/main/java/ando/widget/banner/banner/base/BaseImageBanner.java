package ando.widget.banner.banner.base;

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
import ando.widget.banner.banner.BannerItem;

/**
 * @author javakam
 * @date 2019-09-24 9:04
 */
public abstract class BaseImageBanner<T extends BaseImageBanner<T>> extends BaseIndicatorBanner<BannerItem, T> {

    /**
     * 默认加载图片
     */
    private Drawable mPlaceHolder;
    /**
     * 加载图片的高／宽比率
     */
    private double mScale;

    public BaseImageBanner(Context context) {
        super(context);
        initImageBanner(context);
    }

    public BaseImageBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageBanner(context);
    }

    public BaseImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initImageBanner(context);
    }

    /**
     * 初始化ImageBanner
     */
    protected void initImageBanner(Context context) {
        mPlaceHolder = new ColorDrawable(ContextCompat.getColor(context, R.color.banner_image_placeholder_color));
        mScale = getContainerScale();
    }

    @Override
    public void onTitleSelect(TextView tv, int position) {
        final BannerItem item = getItem(position);
        if (item != null) {
            tv.setText(item.title);
        }
    }

    /**
     * @return 轮播布局的ID
     */
    protected abstract int getItemLayoutId();

    /**
     * @return 图片控件的ID
     */
    protected abstract int getImageViewId();

    /**
     * 创建ViewPager的Item布局
     */
    @Override
    public View onCreateItemView(int position) {
        View inflate = View.inflate(getContext(), getItemLayoutId(), null);
        ImageView iv = inflate.findViewById(getImageViewId());

        //解决Glide资源释放的问题，详细见http://blog.csdn.net/shangmingchao/article/details/51125554
        WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(iv);
        ImageView target = imageViewWeakReference.get();

        BannerItem item = getItem(position);
        if (item != null && target != null) {
            loadingImageView(target, item);
        }
        return inflate;
    }

    /**
     * 默认加载图片的方法，可以重写
     */
    protected void loadingImageView(ImageView iv, BannerItem item) {
        String imgUrl = item.imgUrl;
        if (!TextUtils.isEmpty(imgUrl)) {
            if (mScale > 0) {
                int itemWidth = getItemWidth();
                int itemHeight = (int) (itemWidth * mScale);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));

                getImageLoader().loadImage(iv, imgUrl, itemWidth, itemHeight, mPlaceHolder);
            } else {
                getImageLoader().loadImage(iv, imgUrl, mPlaceHolder);
            }
        } else {
            iv.setImageDrawable(mPlaceHolder);
        }
    }

    public Drawable getPlaceHolderDrawable() {
        return mPlaceHolder;
    }

    public T setPlaceHolderDrawable(Drawable placeHolder) {
        mPlaceHolder = placeHolder;
        return (T) this;
    }

    public double getScale() {
        return mScale;
    }

    public T setScale(double scale) {
        mScale = scale;
        return (T) this;
    }

    @Override
    protected void onDetachedFromWindow() {
        //解决内存泄漏的问题
        pauseScroll();
        super.onDetachedFromWindow();
    }
}