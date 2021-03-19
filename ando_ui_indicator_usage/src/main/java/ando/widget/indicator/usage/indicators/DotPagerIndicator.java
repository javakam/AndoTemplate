package ando.widget.indicator.usage.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

import ando.widget.indicator.IndicatorUtils;
import ando.widget.indicator.abs.IPagerIndicator;
import ando.widget.indicator.indicators.PagerIndicatorPosition;

/**
 * 非手指跟随的小圆点指示器
 */
public class DotPagerIndicator extends View implements IPagerIndicator {
    private List<PagerIndicatorPosition> mDataList;
    private float mRadius;
    private float mYOffset;
    private int mDotColor;

    private float mCircleCenterX;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DotPagerIndicator(Context context) {
        super(context);
        mRadius = IndicatorUtils.dip2px(context, 3);
        mYOffset = IndicatorUtils.dip2px(context, 3);
        mDotColor = Color.WHITE;
    }

    @Override
    public void onPageSelected(int position) {
        if (mDataList == null || mDataList.isEmpty()) {
            return;
        }
        PagerIndicatorPosition data = mDataList.get(position);
        mCircleCenterX = data.mLeft + data.width() / 2;
        invalidate();
    }

    @Override
    public void onIndicatorPositionProvide(List<PagerIndicatorPosition> dataList) {
        mDataList = dataList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mDotColor);
        canvas.drawCircle(mCircleCenterX, getHeight() - mYOffset - mRadius, mRadius, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
        invalidate();
    }

    public float getYOffset() {
        return mYOffset;
    }

    public void setYOffset(float yOffset) {
        mYOffset = yOffset;
        invalidate();
    }

    public int getDotColor() {
        return mDotColor;
    }

    public void setDotColor(int dotColor) {
        mDotColor = dotColor;
        invalidate();
    }
}
