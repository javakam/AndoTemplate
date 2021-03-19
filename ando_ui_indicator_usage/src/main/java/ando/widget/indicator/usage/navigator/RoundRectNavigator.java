package ando.widget.indicator.usage.navigator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import ando.widget.indicator.abs.IPagerNavigator;
import ando.widget.indicator.IndicatorUtils;

/**
 * # RoundRectNavigator  圆角矩形指示器
 *
 * <pre>
 *     使用:
 *
 *      private void initMagicIndicator4() {
 *         MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator4);
 *         RoundRectNavigator circleNavigator = new RoundRectNavigator(this);
 *         circleNavigator.setFollowTouch(true);//是否跟随手指滑动
 *         circleNavigator.setTotalCount(count);
 *
 *         circleNavigator.setItemColor(Color.LTGRAY);
 *         circleNavigator.setIndicatorColor(Color.parseColor("#BA0022"));
 *
 *         circleNavigator.setItemWidth(18D);
 *         circleNavigator.setItemSpacing(4D);
 *         circleNavigator.setItemHeight(3D);
 *         circleNavigator.setItemRadius(3D);
 *
 *         circleNavigator.setOnItemClickListener(new RoundRectNavigator.OnItemClickListener() {
 *             @ Override
 *             public void onClick(int index) {
 *                 mViewPager.setCurrentItem(index);
 *             }
 *         });
 *         circleNavigator.notifyDataSetChanged();
 *         magicIndicator.setNavigator(circleNavigator);
 *         ViewPagerHelper.bind(magicIndicator, mViewPager);
 *     }
 * </pre>
 *
 * @author javakam
 * @date 2020/1/10 15:08
 */
public class RoundRectNavigator extends View implements IPagerNavigator {

    private int mItemWidth;   //每一个 Item 宽度
    private int mItemHeight;  //Item 高度
    private int mItemRadius;  //圆角 度数
    private int mItemColor;   //Item 背景色
    private int mIndicatorColor;// Indicator 背景色
    private int mItemSpacing; //Item 间距
    //
    private int mCurrentIndex;
    private int mTotalCount;
    private Interpolator mStartInterpolator = new LinearInterpolator();

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<PointF> mRectPoints = new ArrayList<PointF>();
    private float mIndicatorX;

    // 事件回调
    private boolean mTouchable;
    private OnItemClickListener onItemClickListener;
    private float mDownX;
    private float mDownY;
    private int mTouchSlop;

    private boolean mFollowTouch = true;    // 是否跟随手指滑动

    public RoundRectNavigator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mItemSpacing = IndicatorUtils.dip2px(context, 4);
        mItemWidth = IndicatorUtils.dip2px(context, 16);
        mItemHeight = IndicatorUtils.dip2px(context, 3);
        mItemRadius = IndicatorUtils.dip2px(context, 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = width;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = mTotalCount * mItemWidth + (mTotalCount - 1) * mItemSpacing + getPaddingLeft() + getPaddingRight();
                break;
            default:
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = height;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = mItemHeight * 2 + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mItemColor);
        drawItems(canvas);
        drawIndicator(canvas);
    }


    private void drawItems(Canvas canvas) {
        mPaint.setColor(mItemColor);

        for (int i = 0, j = mRectPoints.size(); i < j; i++) {
            PointF pointF = mRectPoints.get(i);
            canvas.drawRoundRect(pointF.x, pointF.y - mItemHeight / 2F,
                    pointF.x + mItemWidth, pointF.y + mItemHeight / 2F, mItemRadius, mItemRadius, mPaint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        mPaint.setColor(mIndicatorColor);

        if (mRectPoints.size() > 0) {
            final float y = (getHeight() / 2.0f + 0.5f);
            canvas.drawRoundRect(mIndicatorX, y - mItemHeight / 2F,
                    mIndicatorX + mItemWidth, y + mItemHeight / 2F, mItemRadius, mItemRadius, mPaint);
        }
    }

    private void prepareItemPoints() {
        mRectPoints.clear();
        if (mTotalCount > 0) {
            int y = (int) (getHeight() / 2.0f + 0.5f);
            int distanceFromNowStart2NextStart = mItemWidth + mItemSpacing;
            int startX = getPaddingLeft();
            for (int i = 0; i < mTotalCount; i++) {
                PointF pointF = new PointF(startX, y);
                mRectPoints.add(pointF);
                startX += distanceFromNowStart2NextStart;
            }
            mIndicatorX = mRectPoints.get(mCurrentIndex).x;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mFollowTouch) {
            if (mRectPoints.isEmpty()) {
                return;
            }

            int currentPosition = Math.min(mRectPoints.size() - 1, position);
            int nextPosition = Math.min(mRectPoints.size() - 1, position + 1);
            PointF current = mRectPoints.get(currentPosition);
            PointF next = mRectPoints.get(nextPosition);

            mIndicatorX = current.x + (next.x - current.x) * mStartInterpolator.getInterpolation(positionOffset);

            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mTouchable) {
                    mDownX = x;
                    mDownY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onItemClickListener != null) {
                    if (Math.abs(x - mDownX) <= mTouchSlop && Math.abs(y - mDownY) <= mTouchSlop) {
                        float max = Float.MAX_VALUE;
                        int index = 0;
                        for (int i = 0; i < mRectPoints.size(); i++) {
                            PointF pointF = mRectPoints.get(i);
                            float offset = Math.abs(pointF.x - x);
                            if (offset < max) {
                                max = offset;
                                index = i;
                            }
                        }
                        onItemClickListener.onClick(index);
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentIndex = position;
        if (!mFollowTouch) {
            mIndicatorX = mRectPoints.get(mCurrentIndex).x;
            invalidate();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        prepareItemPoints();
    }

    @Override
    public void onAttachToMagicIndicator() {
    }

    @Override
    public void notifyDataSetChanged() {
        prepareItemPoints();
        invalidate();
    }

    @Override
    public void onDetachFromMagicIndicator() {
    }

    public int getItemWidth() {
        return mItemWidth;
    }

    public void setItemWidth(double itemWidth) {
        mItemWidth = IndicatorUtils.dip2px(getContext(), itemWidth);
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public void setItemHeight(double itemHeight) {
        this.mItemHeight = IndicatorUtils.dip2px(getContext(), itemHeight);
    }

    public int getItemRadius() {
        return mItemRadius;
    }

    public void setItemRadius(double itemRadius) {
        this.mItemRadius = IndicatorUtils.dip2px(getContext(), itemRadius);
    }

    public int getItemColor() {
        return mItemColor;
    }

    public void setItemColor(int itemColor) {
        mItemColor = itemColor;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setIndicatorColor(int circleColor) {
        mIndicatorColor = circleColor;
    }

    public int getItemSpacing() {
        return mItemSpacing;
    }

    public void setItemSpacing(double itemSpacing) {
        mItemSpacing = IndicatorUtils.dip2px(getContext(), itemSpacing);
    }

    public Interpolator getStartInterpolator() {
        return mStartInterpolator;
    }

    public void setStartInterpolator(Interpolator startInterpolator) {
        mStartInterpolator = startInterpolator;
        if (mStartInterpolator == null) {
            mStartInterpolator = new LinearInterpolator();
        }
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int count) {
        mTotalCount = count;  // 此处不调用invalidate，让外部调用notifyDataSetChanged
    }

    public boolean isTouchable() {
        return mTouchable;
    }

    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    public boolean isFollowTouch() {
        return mFollowTouch;
    }

    public void setFollowTouch(boolean followTouch) {
        mFollowTouch = followTouch;
    }

    public OnItemClickListener getRectAngleClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener circleClickListener) {
        if (!mTouchable) {
            mTouchable = true;
        }
        onItemClickListener = circleClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int index);
    }
}
