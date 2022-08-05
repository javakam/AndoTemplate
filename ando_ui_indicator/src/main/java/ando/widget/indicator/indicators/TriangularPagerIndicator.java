package ando.widget.indicator.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.List;

import ando.widget.indicator.FragmentContainerHelper;
import ando.widget.indicator.IndicatorUtils;
import ando.widget.indicator.abs.IPagerIndicator;

/**
 * 带有小尖角的直线指示器
 */
public class TriangularPagerIndicator extends View implements IPagerIndicator {
    private List<PositionData> mIndicatorPositionList;
    private Paint mPaint;
    private int mLineHeight;
    private int mLineColor;
    private int mTriangleHeight;
    private int mTriangleWidth;
    private boolean mReverse;
    private float mYOffset;

    private final Path mPath = new Path();
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private float mAnchorX;

    public TriangularPagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mLineHeight = IndicatorUtils.dip2px(context, 3);
        mTriangleWidth = IndicatorUtils.dip2px(context, 14);
        mTriangleHeight = IndicatorUtils.dip2px(context, 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mLineColor);
        if (mReverse) {
            canvas.drawRect(0, getHeight() - mYOffset - mTriangleHeight, getWidth(), getHeight() - mYOffset - mTriangleHeight + mLineHeight, mPaint);
        } else {
            canvas.drawRect(0, getHeight() - mLineHeight - mYOffset, getWidth(), getHeight() - mYOffset, mPaint);
        }
        mPath.reset();
        if (mReverse) {
            mPath.moveTo(mAnchorX - mTriangleWidth / 2F, getHeight() - mYOffset - mTriangleHeight);
            mPath.lineTo(mAnchorX, getHeight() - mYOffset);
            mPath.lineTo(mAnchorX + mTriangleWidth / 2F, getHeight() - mYOffset - mTriangleHeight);
        } else {
            mPath.moveTo(mAnchorX - mTriangleWidth / 2F, getHeight() - mYOffset);
            mPath.lineTo(mAnchorX, getHeight() - mTriangleHeight - mYOffset);
            mPath.lineTo(mAnchorX + mTriangleWidth / 2F, getHeight() - mYOffset);
        }
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIndicatorPositionList == null || mIndicatorPositionList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativeIndicatorPosition(mIndicatorPositionList, position);
        PositionData next = FragmentContainerHelper.getImitativeIndicatorPosition(mIndicatorPositionList, position + 1);

        float leftX = current.mLeft + (current.mRight - current.mLeft) / 2F;
        float rightX = next.mLeft + (next.mRight - next.mLeft) / 2F;

        mAnchorX = leftX + (rightX - leftX) * mStartInterpolator.getInterpolation(positionOffset);

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onIndicatorPositionProvide(List<PositionData> dataList) {
        mIndicatorPositionList = dataList;
    }

    public int getLineHeight() {
        return mLineHeight;
    }

    public void setLineHeight(int lineHeight) {
        mLineHeight = lineHeight;
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
    }

    public int getTriangleHeight() {
        return mTriangleHeight;
    }

    public void setTriangleHeight(int triangleHeight) {
        mTriangleHeight = triangleHeight;
    }

    public int getTriangleWidth() {
        return mTriangleWidth;
    }

    public void setTriangleWidth(int triangleWidth) {
        mTriangleWidth = triangleWidth;
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

    public boolean isReverse() {
        return mReverse;
    }

    public void setReverse(boolean reverse) {
        mReverse = reverse;
    }

    public float getYOffset() {
        return mYOffset;
    }

    public void setYOffset(float yOffset) {
        mYOffset = yOffset;
    }
}