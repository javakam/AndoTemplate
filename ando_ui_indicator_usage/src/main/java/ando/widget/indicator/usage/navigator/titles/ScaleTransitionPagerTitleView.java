package ando.widget.indicator.usage.navigator.titles;

import android.content.Context;

import ando.widget.indicator.navigator.titles.ColorTransitionPagerTitleView;

/**
 * 带颜色渐变和缩放的指示器标题, 字体变粗
 * <p>
 * 注: 若字体颜色也跟着变,使用 ColorTransitionPagerTitleView
 */
public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {

    private float mMinScale = 0.85F;
    protected boolean enableScale = true;       //是否开启文字大小渐变
    protected boolean isTextBoldWhenSelected;   //选中的标题文字是否加粗
    protected boolean enableColorTrans = true;  //是否开启文字颜色渐变

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        if (enableColorTrans) {
            super.onEnter(index, totalCount, enterPercent, leftToRight);// 实现颜色渐变
        }
        if (enableScale) {
            setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
            setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        if (enableColorTrans) {
            super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
        }
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

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public boolean isEnableScale() {
        return enableScale;
    }

    /**
     * 文字大小渐变
     */
    public void setEnableScale(boolean enableScale) {
        this.enableScale = enableScale;
    }

    public boolean isTextBoldWhenSelected() {
        return isTextBoldWhenSelected;
    }

    public void setTextBoldWhenSelected(boolean textBoldWhenSelected) {
        isTextBoldWhenSelected = textBoldWhenSelected;
    }

    public boolean isEnableColorTrans() {
        return enableColorTrans;
    }

    /**
     * 关闭文字颜色渐变
     */
    public void setEnableColorTrans(boolean enableColorTrans) {
        this.enableColorTrans = enableColorTrans;
    }

}
