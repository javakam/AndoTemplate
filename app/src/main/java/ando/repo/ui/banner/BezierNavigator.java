package ando.repo.ui.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ando.widget.indicator.IndicatorUtils;
import ando.widget.indicator.abs.IPagerNavigator;

/**
 * @author javakam
 * @date 2021/3/30  10:04
 */
public class BezierNavigator extends View implements IPagerNavigator {

    private float mLeftCircleRadius;
    private float mLeftCircleX;
    private float mRightCircleRadius;
    private float mRightCircleX;

    private float mYOffset;
    private float mMaxCircleRadius;
    private float mMinCircleRadius;
    private final Path mPath = new Path();

    //
    private int mCircleSpacing;
    private int mCurrentIndex;
    private int mTotalCount;
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new DecelerateInterpolator();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<PointF> mCirclePoints = new ArrayList<PointF>();

    // 事件回调
    private boolean mTouchable;
    private OnCircleClickListener mCircleClickListener;
    private float mDownX;
    private float mDownY;
    private int mTouchSlop;

    private boolean mFollowTouch = true;    // 是否跟随手指滑动

    public BezierNavigator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mCircleSpacing = IndicatorUtils.dip2px(context, 8);

        mPaint.setStyle(Paint.Style.FILL);
        mMaxCircleRadius = IndicatorUtils.dip2px(context, 3.5);
        mMinCircleRadius = IndicatorUtils.dip2px(context, 2);
        mYOffset = IndicatorUtils.dip2px(context, 1.5);
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
                result = mTotalCount *(int) mMinCircleRadius * 2 + (mTotalCount - 1) * mCircleSpacing + getPaddingLeft() + getPaddingRight();
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
                result = (int) mMinCircleRadius * 2 + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircles(canvas);
        drawIndicator(canvas);
    }

    private void drawCircles(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0, j = mCirclePoints.size(); i < j; i++) {
            PointF pointF = mCirclePoints.get(i);
            //canvas.drawCircle(pointF.x, pointF.y, mRadius, mPaint);

            canvas.drawCircle(mLeftCircleX, getHeight() - mYOffset - mMaxCircleRadius, mLeftCircleRadius, mPaint);
            canvas.drawCircle(mRightCircleX, getHeight() - mYOffset - mMaxCircleRadius, mRightCircleRadius, mPaint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        if (mCirclePoints.size() > 0) {
            //canvas.drawCircle(mIndicatorX, (int) (getHeight() / 2.0f + 0.5f), mRadius, mPaint);
            mPath.reset();
            float y = getHeight() - mYOffset - mMaxCircleRadius;
            mPath.moveTo(mRightCircleX, y);
            mPath.lineTo(mRightCircleX, y - mRightCircleRadius);
            mPath.quadTo(mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f, y, mLeftCircleX, y - mLeftCircleRadius);
            mPath.lineTo(mLeftCircleX, y + mLeftCircleRadius);
            mPath.quadTo(mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f, y, mRightCircleX, y + mRightCircleRadius);
            mPath.close();  // 闭合
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void prepareCirclePoints() {
        mCirclePoints.clear();
        if (mTotalCount > 0) {
            int y = (int) (getHeight() / 2.0f);
            int centerSpacing = (int) mMinCircleRadius * 2 + mCircleSpacing;
            int startX = (int) ((int) mMinCircleRadius + 0.5F + getPaddingLeft());
            for (int i = 0; i < mTotalCount; i++) {
                PointF pointF = new PointF(startX, y);
                mCirclePoints.add(pointF);
                startX += centerSpacing;
            }
            mLeftCircleX = mCirclePoints.get(mCurrentIndex).x;
        }
    }

    private List<Integer> mColors;

    public void setColors(Integer... colors) {
        mColors = Arrays.asList(colors);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mFollowTouch) {
            if (mCirclePoints.isEmpty()) {
                return;
            }

            int currentPosition = Math.min(mCirclePoints.size() - 1, position);
            int nextPosition = Math.min(mCirclePoints.size() - 1, position + 1);
            PointF current = mCirclePoints.get(currentPosition);
            PointF next = mCirclePoints.get(nextPosition);

            mLeftCircleX = current.x + (next.x - current.x) * mStartInterpolator.getInterpolation(positionOffset);

            // 计算颜色
            if (mColors != null && mColors.size() > 0) {
                int currentColor = mColors.get(Math.abs(position) % mColors.size());
                int nextColor = mColors.get(Math.abs(position + 1) % mColors.size());
                int color = IndicatorUtils.eval(positionOffset, currentColor, nextColor);
                mPaint.setColor(color);
            }

            mLeftCircleX = current.x + (next.x - current.x) * mStartInterpolator.getInterpolation(positionOffset);
            mRightCircleX = current.x + (next.x - current.x) * mEndInterpolator.getInterpolation(positionOffset);
            mLeftCircleRadius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * mEndInterpolator.getInterpolation(positionOffset);
            mRightCircleRadius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * mStartInterpolator.getInterpolation(positionOffset);

            invalidate();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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
                if (mCircleClickListener != null) {
                    if (Math.abs(x - mDownX) <= mTouchSlop && Math.abs(y - mDownY) <= mTouchSlop) {
                        float max = Float.MAX_VALUE;
                        int index = 0;
                        for (int i = 0; i < mCirclePoints.size(); i++) {
                            PointF pointF = mCirclePoints.get(i);
                            float offset = Math.abs(pointF.x - x);
                            if (offset < max) {
                                max = offset;
                                index = i;
                            }
                        }
                        mCircleClickListener.onClick(index);
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
            mLeftCircleX = mCirclePoints.get(mCurrentIndex).x;
            invalidate();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        prepareCirclePoints();
    }

    @Override
    public void onAttachIndicator() {
    }

    @Override
    public void onDetachIndicator() {
    }

    @Override
    public void notifyDataSetChanged() {
        prepareCirclePoints();
        invalidate();
    }

    public int getCircleSpacing() {
        return mCircleSpacing;
    }

    public void setCircleSpacing(int circleSpacing) {
        mCircleSpacing = circleSpacing;
        prepareCirclePoints();
        invalidate();
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

    public void setEndInterpolator(Interpolator endInterpolator) {
        mEndInterpolator = endInterpolator;
        if (mEndInterpolator == null) {
            mEndInterpolator = new DecelerateInterpolator();
        }
    }

    public int getCircleCount() {
        return mTotalCount;
    }

    public void setCircleCount(int count) {
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

    public OnCircleClickListener getCircleClickListener() {
        return mCircleClickListener;
    }

    public void setCircleClickListener(OnCircleClickListener circleClickListener) {
        if (!mTouchable) {
            mTouchable = true;
        }
        mCircleClickListener = circleClickListener;
    }

    public interface OnCircleClickListener {
        void onClick(int index);
    }
}