package ando.repo.widget.indicator.titles;

import android.content.Context;

import ando.widget.indicator.navigator.titles.SimplePagerTitleView;

/**
 * Title:CustomScaleTransitionPagerTitleView
 * <p>
 * Description:带颜色渐变和缩放的指示器标题,字体变粗
 *
 * <pre>
 *     注: 若字体颜色也跟着变,使用 com.ando.indicator.navigator.titles.ColorTransitionPagerTitleView
 * </pre>
 * </p>
 *
 * @author javakam
 * @date 2019/11/11 10:55
 */
public class CustomScaleTransitionPagerTitleView extends SimplePagerTitleView {

    private float mMinScale = 0.85f;//缩放
    protected boolean isTextBoldWhenSelected;
    protected boolean enableScale = true;

    public CustomScaleTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
        if (enableScale) {
            setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
            setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
        if (enableScale) {
            setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
            setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
        }
    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        if (isTextBoldWhenSelected) {
            getPaint().setFakeBoldText(true);
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        if (isTextBoldWhenSelected) {
            getPaint().setFakeBoldText(false);
        }
    }

    public void disableTextScale() {
        this.enableScale = false;
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public boolean isTextBoldWhenSelected() {
        return isTextBoldWhenSelected;
    }

    public void setTextBoldWhenSelected(boolean textBoldWhenSelected) {
        isTextBoldWhenSelected = textBoldWhenSelected;
    }

}