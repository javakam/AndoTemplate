package ando.widget.banner.banner.base;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ando.widget.banner.IBannerImageLoadStrategy;
import ando.widget.banner.R;
import ando.widget.banner.anim.BaseAnimator;

/**
 * 轮播下方的引导器
 *
 * @author javakam
 * @date 2018/11/25 下午7:01
 */
public abstract class BaseIndicatorBanner<E, T extends BaseIndicatorBanner<E, T>> extends BaseBanner<E, T> {
    public static final int STYLE_DRAWABLE_RESOURCE = 0;
    public static final int STYLE_CORNER_RECTANGLE = 1;

    private final List<ImageView> mIndicatorViews = new ArrayList<>();
    private int mIndicatorStyle;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mIndicatorGap;
    private int mIndicatorCornerRadius;

    private Drawable mSelectDrawable;
    private Drawable mUnSelectDrawable;
    private int mSelectColor;
    private int mUnSelectColor;

    private Class<? extends BaseAnimator> mSelectAnimClass;
    private Class<? extends BaseAnimator> mUnSelectAnimClass;

    private LinearLayout mLlIndicators;
    private IBannerImageLoadStrategy mImageLoader;

    public BaseIndicatorBanner(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public BaseIndicatorBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public BaseIndicatorBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseIndicatorBanner);
        mIndicatorStyle = ta.getInt(R.styleable.BaseIndicatorBanner_bb_indicatorStyle, STYLE_CORNER_RECTANGLE);
        mIndicatorWidth = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorWidth, dp2px(6));
        mIndicatorHeight = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorHeight, dp2px(6));
        mIndicatorGap = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorGap, dp2px(6));
        mIndicatorCornerRadius = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorCornerRadius, dp2px(3));
        mSelectColor = ta.getColor(R.styleable.BaseIndicatorBanner_bb_indicatorSelectColor, Color.WHITE);
        mUnSelectColor = ta.getColor(R.styleable.BaseIndicatorBanner_bb_indicatorUnSelectColor, Color.parseColor("#88FFFFFF"));

        int selectRes = ta.getResourceId(R.styleable.BaseIndicatorBanner_bb_indicatorSelectRes, 0);
        int unSelectRes = ta.getResourceId(R.styleable.BaseIndicatorBanner_bb_indicatorUnSelectRes, 0);
        ta.recycle();

        //create indicator container
        mLlIndicators = new LinearLayout(context);
        mLlIndicators.setGravity(Gravity.CENTER);

        setIndicatorSelectorRes(unSelectRes, selectRes);
    }

    @Override
    public View onCreateIndicator() {
        if (mIndicatorStyle == STYLE_CORNER_RECTANGLE) {//rectangle
            this.mUnSelectDrawable = getDrawable(mUnSelectColor, mIndicatorCornerRadius);
            this.mSelectDrawable = getDrawable(mSelectColor, mIndicatorCornerRadius);
        }

        int size = mData.size();
        mIndicatorViews.clear();

        mLlIndicators.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageDrawable(i == mCurrentPosition ? mSelectDrawable : mUnSelectDrawable);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIndicatorWidth,
                    mIndicatorHeight);
            lp.leftMargin = i == 0 ? 0 : mIndicatorGap;
            mLlIndicators.addView(iv, lp);
            mIndicatorViews.add(iv);
        }

        setCurrentIndicator(mCurrentPosition);
        return mLlIndicators;
    }

    @Override
    public void setCurrentIndicator(int position) {
        for (int i = 0; i < mIndicatorViews.size(); i++) {
            mIndicatorViews.get(i).setImageDrawable(i == position ? mSelectDrawable : mUnSelectDrawable);
        }
        try {
            if (mSelectAnimClass != null) {
                if (position == mLastPosition) {
                    mSelectAnimClass.newInstance().playOn(mIndicatorViews.get(position));
                } else {
                    mSelectAnimClass.newInstance().playOn(mIndicatorViews.get(position));
                    if (mUnSelectAnimClass == null) {
                        mSelectAnimClass.newInstance().interpolator(new ReverseInterpolator()).playOn(mIndicatorViews.get(mLastPosition));
                    } else {
                        mUnSelectAnimClass.newInstance().playOn(mIndicatorViews.get(mLastPosition));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置显示样式,STYLE_DRAWABLE_RESOURCE or STYLE_CORNER_RECTANGLE
     */
    public T setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        return (T) this;
    }

    /**
     * 设置显示宽度,单位dp,默认6dp
     */
    public T setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = dp2px(indicatorWidth);
        return (T) this;
    }

    /**
     * 设置显示器高度,单位dp,默认6dp
     */
    public T setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = dp2px(indicatorHeight);
        return (T) this;
    }

    /**
     * 设置两个显示器间距,单位dp,默认6dp
     */
    public T setIndicatorGap(float indicatorGap) {
        this.mIndicatorGap = dp2px(indicatorGap);
        return (T) this;
    }

    /**
     * 设置显示器选中颜色(for STYLE_CORNER_RECTANGLE),默认"#ffffff"
     */
    public T setIndicatorSelectColor(int selectColor) {
        this.mSelectColor = selectColor;
        return (T) this;
    }

    /**
     * 设置显示器未选中颜色(for STYLE_CORNER_RECTANGLE),默认"#88ffffff"
     */
    public T setIndicatorUnSelectColor(int unSelectColor) {
        this.mUnSelectColor = unSelectColor;
        return (T) this;
    }

    /**
     * 设置显示器圆角弧度(for STYLE_CORNER_RECTANGLE),单位dp,默认3dp
     */
    public T setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
        return (T) this;
    }

    /**
     * 设置显示器选中以及未选中资源(for STYLE_DRAWABLE_RESOURCE)
     */
    public T setIndicatorSelectorRes(int unSelectRes, int selectRes) {
        try {
            if (mIndicatorStyle == STYLE_DRAWABLE_RESOURCE) {
                if (selectRes != 0) {
                    this.mSelectDrawable = ContextCompat.getDrawable(getContext(), selectRes);
                }
                if (unSelectRes != 0) {
                    this.mUnSelectDrawable = ContextCompat.getDrawable(getContext(), unSelectRes);
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    /**
     * 设置显示器选中动画
     */
    public T setSelectAnimClass(Class<? extends BaseAnimator> selectAnimClass) {
        this.mSelectAnimClass = selectAnimClass;
        return (T) this;
    }

    /**
     * 设置显示器未选中动画
     */
    public T setUnSelectAnimClass(Class<? extends BaseAnimator> unSelectAnimClass) {
        this.mUnSelectAnimClass = unSelectAnimClass;
        return (T) this;
    }

    public IBannerImageLoadStrategy getImageLoader() {
        return mImageLoader;
    }

    public void setImageLoader(IBannerImageLoadStrategy imageLoader) {
        this.mImageLoader = imageLoader;
    }

    private static class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    private GradientDrawable getDrawable(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(color);
        return drawable;
    }
}