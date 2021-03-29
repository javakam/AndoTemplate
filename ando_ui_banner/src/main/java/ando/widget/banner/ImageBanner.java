package ando.widget.banner.banner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

import ando.widget.banner.R;

/**
 * @author javakam
 * @date 2021/3/29  9:53
 */
public class CustomImageBanner extends CustomBanner<BannerItem> {
    /**
     * 默认加载图片
     */
    private Drawable mColorDrawable;
    /**
     * 高／宽比率
     */
    private double mScale;

    public CustomImageBanner(Context context) {
        this(context, null, 0);
    }

    public CustomImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBanner();
    }

    /**
     * 初始化ImageBanner
     */
    protected void initBanner() {
        mColorDrawable = new ColorDrawable(Color.TRANSPARENT);
        mScale = getContainerScale();
    }

    @Override
    protected void onDetachedFromWindow() {
        //解决内存泄漏的问题
        pauseScroll();
        super.onDetachedFromWindow();
    }

    @Override
    public View onCreateItemView(int position) {
        View view = inflate(mContext, R.layout.banner_adapter_simple_image, null);
        ImageView iv = view.findViewById(R.id.iv_banner);

        //解决Glide资源释放的问题，详细见 http://blog.csdn.net/shangmingchao/article/details/51125554
        WeakReference<ImageView> imageViewWeakReference = new WeakReference<>(iv);
        ImageView target = imageViewWeakReference.get();

        BannerItem item = getItem(position);
        if (item != null && target != null) {
            loadingImageView(target, item);
        }
        return view;
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
            getImageLoader().loadImage(iv, imgUrl, itemWidth, itemHeight, mColorDrawable);
        } else {
            iv.setImageDrawable(mColorDrawable);
        }
    }

    public Drawable getColorDrawable() {
        return mColorDrawable;
    }

    public void setColorDrawable(Drawable colorDrawable) {
        mColorDrawable = colorDrawable;
    }

    public double getScale() {
        return mScale;
    }

    public void setScale(double scale) {
        mScale = scale;
    }
}