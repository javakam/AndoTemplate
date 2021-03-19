package ando.widget.indicator.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.List;

import ando.widget.indicator.FragmentContainerHelper;
import ando.widget.indicator.abs.IPagerIndicator;


/**
 * 用于测试的指示器，可用来检测自定义的IMeasurablePagerTitleView是否正确测量内容区域
 */
public class TestPagerIndicator extends View implements IPagerIndicator {
    private Paint mPaint;
    private int mOutRectColor;
    private int mInnerRectColor;
    private RectF mOutRect = new RectF();
    private RectF mInnerRect = new RectF();

    private List<PagerIndicatorPosition> mIndicatorPositionList;

    public TestPagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mOutRectColor = Color.RED;
        mInnerRectColor = Color.GREEN;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mOutRectColor);
        canvas.drawRect(mOutRect, mPaint);
        mPaint.setColor(mInnerRectColor);
        canvas.drawRect(mInnerRect, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mIndicatorPositionList == null || mIndicatorPositionList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PagerIndicatorPosition current = FragmentContainerHelper.getImitativeIndicatorPosition(mIndicatorPositionList, position);
        PagerIndicatorPosition next = FragmentContainerHelper.getImitativeIndicatorPosition(mIndicatorPositionList, position + 1);

        mOutRect.left = current.mLeft + (next.mLeft - current.mLeft) * positionOffset;
        mOutRect.top = current.mTop + (next.mTop - current.mTop) * positionOffset;
        mOutRect.right = current.mRight + (next.mRight - current.mRight) * positionOffset;
        mOutRect.bottom = current.mBottom + (next.mBottom - current.mBottom) * positionOffset;

        mInnerRect.left = current.mContentLeft + (next.mContentLeft - current.mContentLeft) * positionOffset;
        mInnerRect.top = current.mContentTop + (next.mContentTop - current.mContentTop) * positionOffset;
        mInnerRect.right = current.mContentRight + (next.mContentRight - current.mContentRight) * positionOffset;
        mInnerRect.bottom = current.mContentBottom + (next.mContentBottom - current.mContentBottom) * positionOffset;

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onIndicatorPositionProvide(List<PagerIndicatorPosition> dataList) {
        mIndicatorPositionList = dataList;
    }

    public int getOutRectColor() {
        return mOutRectColor;
    }

    public void setOutRectColor(int outRectColor) {
        mOutRectColor = outRectColor;
    }

    public int getInnerRectColor() {
        return mInnerRectColor;
    }

    public void setInnerRectColor(int innerRectColor) {
        mInnerRectColor = innerRectColor;
    }
}
