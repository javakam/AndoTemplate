package ando.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 轮播组件
 *
 * @author javakam
 * @date 2018/11/25 下午7:18
 */
public abstract class BaseBanner<T> extends RelativeLayout {
    /**
     * 单线程池定时任务
     */
    private ScheduledExecutorService mExecutorService;
    /**
     * 设备密度
     */
    protected DisplayMetrics mDisplayMetrics;
    /**
     * ViewPager
     */
    protected ViewPager mViewPager;
    /**
     * 数据源
     */
    protected List<T> mData = new ArrayList<>();
    /**
     * 当前position
     */
    protected int mCurrentPosition;
    /**
     * 上一个position
     */
    protected int mLastPosition;
    /**
     * 多久后开始滚动
     */
    private long mDelay;
    /**
     * 滚动间隔
     */
    private long mPeriod;
    /**
     * 是否自动滚动
     */
    private boolean mIsAutoScrollEnable;
    /**
     * 是否正在自动滚动中
     */
    private boolean mIsAutoScrolling;
    /**
     * 滚动速度
     */
    private int mScrollSpeed = 450;
    /**
     * 切换动画
     */
    private Class<? extends ViewPager.PageTransformer> mTransformerClass;

    private int mItemWidth;
    private int mItemHeight;
    /**
     * 数据是否发生变化
     */
    private boolean mIsDataChanged = false;
    /**
     * 当页面只有一条时，是否轮播
     */
    private boolean mIsOnePageLoop = true;
    /**
     * Banner容器的高／宽比率
     */
    private float mContainerScale;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnBannerItemClickListener mOnBannerItemClickListener;
    private IBannerImageLoadStrategy mImageLoader;

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            scrollToNextItem(mCurrentPosition);
        }
    };

    public BaseBanner(Context context) {
        this(context, null, 0, 0);
    }

    public BaseBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public BaseBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        mDisplayMetrics = context.getResources().getDisplayMetrics();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseBanner, defStyleAttr, defStyleRes);
        mContainerScale = ta.getFloat(R.styleable.BaseBanner_banner_scale, -1);
        boolean isLoopEnable = ta.getBoolean(R.styleable.BaseBanner_banner_isLoopEnable, true);
        mDelay = ta.getInt(R.styleable.BaseBanner_banner_delay, 5);
        mPeriod = ta.getInt(R.styleable.BaseBanner_banner_period, 5);
        mIsAutoScrollEnable = ta.getBoolean(R.styleable.BaseBanner_banner_isAutoScrollEnable, true);
        ta.recycle();

        //create ViewPager
        mViewPager = isLoopEnable ? new LoopViewPager(context) : new ViewPager(context);
        mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        mItemWidth = mDisplayMetrics.widthPixels;
        //scale not set in xml
        if (mContainerScale < 0) {
            try {
                //get layout_height
                final String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
                if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "")) {
                    mItemHeight = LayoutParams.MATCH_PARENT;
                } else if (height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
                    mItemHeight = LayoutParams.WRAP_CONTENT;
                } else {
                    int[] systemAttrs = {android.R.attr.layout_height};
                    TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
                    int h = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                    a.recycle();
                    mItemHeight = h;
                }
            } catch (Exception e) {
                print(e.getMessage());
                mItemHeight = LayoutParams.WRAP_CONTENT;
            }
        } else {
            if (mContainerScale > 1) {
                mContainerScale = 1;
            }
            mItemHeight = (int) (mItemWidth * mContainerScale);
        }

        LayoutParams lp = new LayoutParams(mItemWidth, mItemHeight);
        addView(mViewPager, lp);
    }

    /**
     * 创建ViewPager的Item布局
     */
    public abstract View onCreateItemView(int position);

    /**
     * 设置数据源
     */
    public BaseBanner<T> setSource(List<T> list) {
        this.mData = list;
        mIsDataChanged = true;
        return this;
    }

    public T getItem(int position) {
        return size() > 0 ? mData.get(position) : null;
    }

    public int size() {
        return mData != null ? mData.size() : 0;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public int getItemWidth() {
        return mItemWidth;
    }

    /**
     * 获取容器的宽高比
     */
    public float getContainerScale() {
        return mContainerScale;
    }

    /**
     * 滚动延时,默认5秒
     */
    public BaseBanner<T> setDelay(long delay) {
        this.mDelay = delay;
        return this;
    }

    /**
     * 滚动间隔,默认5秒
     */
    public BaseBanner<T> setPeriod(long period) {
        this.mPeriod = period;
        return this;
    }

    /**
     * 设置是否支持自动滚动,默认true.仅对LoopViewPager有效
     */
    public BaseBanner<T> setAutoScrollEnable(boolean isAutoScrollEnable) {
        this.mIsAutoScrollEnable = isAutoScrollEnable;
        return this;
    }

    /**
     * 设置页面切换动画
     */
    public BaseBanner<T> setTransformerClass(Class<? extends ViewPager.PageTransformer> transformerClass) {
        this.mTransformerClass = transformerClass;
        return this;
    }

    /**
     * 更新ViewPager
     */
    private void updateViewPager() {
        if (isLoopViewPager()) {
            ((LoopViewPager) mViewPager).getPageAdapterWrapper().notifyDataSetChanged();
        } else {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * 设置viewpager
     */
    private void setViewPager() {
        final InnerBannerAdapter innerAdapter = new InnerBannerAdapter();
        mViewPager.setAdapter(innerAdapter);
        mViewPager.setOffscreenPageLimit(mData.size() - 1);

        try {
            if (mTransformerClass != null) {
                mViewPager.setPageTransformer(true, mTransformerClass.newInstance());
                if (isLoopViewPager()) {
                    mScrollSpeed = 550;
                    setScrollSpeed();
                }
            } else {
                if (isLoopViewPager()) {
                    mScrollSpeed = 450;
                    setScrollSpeed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mInternalPageListener != null) {
            mViewPager.removeOnPageChangeListener(mInternalPageListener);
            mViewPager.addOnPageChangeListener(mInternalPageListener);
        }
    }

    /**
     * 页面变换监听
     */
    private final ViewPager.OnPageChangeListener mInternalPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentPosition = position % mData.size();

            mLastPosition = mCurrentPosition;
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    /**
     * 滚动到下一个item
     */
    private void scrollToNextItem(int position) {
        if (isValid()) {
            if (mIsDataChanged) {
                updateViewPager();
            }
            position++;
            mViewPager.setCurrentItem(position);
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        if (mData == null) {
            throw new IllegalStateException("Data source is empty,you must setSource() before startScroll()");
        }

        if (mData.size() > 0 && mCurrentPosition > mData.size() - 1) {
            mCurrentPosition = 0;
        }

        setViewPager();
        goOnScroll();
    }

    /**
     * 继续滚动(for LoopViewPager)
     */
    public void goOnScroll() {
        if (!isValid()) {
            return;
        }

        if (mIsAutoScrolling) {
            return;
        }
        if (isLoopViewPager() && mIsAutoScrollEnable) {
            pauseScroll();
            //noinspection AlibabaThreadPoolCreation
            mExecutorService = Executors.newSingleThreadScheduledExecutor();
            mExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    mHandler.obtainMessage().sendToTarget();
                }
            }, mDelay, mPeriod, TimeUnit.SECONDS);
            mIsAutoScrolling = true;
            print(this.getClass().getSimpleName() + "--->goOnScroll()");
        } else {
            mIsAutoScrolling = false;
        }
    }

    /**
     * 停止滚动(for LoopViewPager)
     */
    public void pauseScroll() {
        stopScroll();
        print(this.getClass().getSimpleName() + "--->pauseScroll()");

        mIsAutoScrolling = false;
    }

    private void stopScroll() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseScroll();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                goOnScroll();
                break;
            default:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置滚动速率
     */
    private void setScrollSpeed() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            FixedSpeedScroller speedScroller = new FixedSpeedScroller(mContext, interpolator, mScrollSpeed);
            mScroller.set(mViewPager, speedScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isLoopViewPager() {
        return mViewPager instanceof LoopViewPager;
    }

    protected boolean isValid() {
        if (mViewPager == null) {
            print("ViewPager is not exist!");
            return false;
        }

        if (mData == null || mData.size() <= 0) {
            print("DataList must be not empty!");
            return false;
        }

        return mIsOnePageLoop || mData.size() != 1;
    }

    /**
     * 获取ViewPager对象
     */
    @Nullable
    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * 设置当页面只有一条时，是否轮播
     */
    public BaseBanner<T> setIsOnePageLoop(boolean isOnePageLoop) {
        mIsOnePageLoop = isOnePageLoop;
        return this;
    }

    /**
     * 资源回收
     */
    public void recycle() {
        pauseScroll();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        if (mViewPager != null) {
            mLastPosition = mCurrentPosition;
            mCurrentPosition = position;
            mViewPager.setCurrentItem(mCurrentPosition, smoothScroll);
        }
    }

    public IBannerImageLoadStrategy getImageLoader() {
        return mImageLoader;
    }

    public void setImageLoader(IBannerImageLoadStrategy imageLoader) {
        this.mImageLoader = imageLoader;
    }

    public BaseBanner<T> addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
        return this;
    }

    public BaseBanner<T> setOnItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.mOnBannerItemClickListener = onBannerItemClickListener;
        return this;
    }

    protected void print(String msg) {
        //Log.d("123", msg);
    }

    public interface OnBannerItemClickListener {
        void onClick(int position);
    }

    private abstract static class NoShakeListener implements OnClickListener {
        private long mLastClickTime = 0;

        private boolean isFastDoubleClick() {
            long nowTime = System.currentTimeMillis();
            if (Math.abs(nowTime - mLastClickTime) < 500) {
                return true;  // 快速点击事件
            } else {
                mLastClickTime = nowTime;
                return false; // 单次点击事件
            }
        }

        @Override
        public void onClick(View v) {
            if (!isFastDoubleClick()) {
                onSingleClick(v);
            }
        }

        protected abstract void onSingleClick(View v);
    }

    private class InnerBannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            final View view = onCreateItemView(position);
            view.setOnClickListener(new NoShakeListener() {
                @Override
                protected void onSingleClick(View v) {
                    if (mOnBannerItemClickListener != null) {
                        mOnBannerItemClickListener.onClick(position);
                    }
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    private static class FixedSpeedScroller extends Scroller {
        private final int mScrollSpeed;

        public FixedSpeedScroller(Context context, Interpolator interpolator, int scrollSpeed) {
            super(context, interpolator);
            this.mScrollSpeed = scrollSpeed;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, this.mScrollSpeed);
        }
    }

}