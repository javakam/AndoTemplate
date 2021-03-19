package ando.repo.widget.indicator.titles;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DimenRes;

import ando.repo.R;
import ando.widget.indicator.abs.IMeasurablePagerTitleView;

/**
 * Title:CustomPagerTitleView
 * <p>
 * Description: 带文本的指示器标题
 * </p>
 *
 * @author javakam
 * @date 2020/1/9 17:28
 */
public class CustomPagerTitleView extends FrameLayout implements IMeasurablePagerTitleView {

    protected TextView mTextView;
    protected int mSelectedTextColor;
    protected int mNormalTextColor;
    protected boolean enablePadding;

    public CustomPagerTitleView(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        setOverScrollMode(OVER_SCROLL_NEVER);

        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mTextView.setSingleLine();
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(mTextView);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        mTextView.setTextColor(mSelectedTextColor);
        mTextView.setSelected(true);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        mTextView.setTextColor(mNormalTextColor);
        mTextView.setSelected(false);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
    }

    @Override
    public int getContentLeft() {
        Rect bound = new Rect();
        String longestString = "";
        if (getText().toString().contains("\n")) {
            String[] brokenStrings = getText().toString().split("\\n");
            for (String each : brokenStrings) {
                if (each.length() > longestString.length()) {
                    longestString = each;
                }
            }
        } else {
            longestString = getText().toString();
        }
        getPaint().getTextBounds(longestString, 0, longestString.length(), bound);
        return getLeft() + getWidth() / 2 - bound.width() / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        Rect bound = new Rect();
        String longestString = "";
        if (getText().toString().contains("\n")) {
            String[] brokenStrings = getText().toString().split("\\n");
            for (String each : brokenStrings) {
                if (each.length() > longestString.length()) {
                    longestString = each;
                }
            }
        } else {
            longestString = getText().toString();
        }
        getPaint().getTextBounds(longestString, 0, longestString.length(), bound);
        int contentWidth = bound.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = getPaint().getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }

    public int getSelectedColor() {
        return mSelectedTextColor;
    }

    public void setSelectedColor(int selectedColor) {
        mSelectedTextColor = selectedColor;
    }

    public int getNormalColor() {
        return mNormalTextColor;
    }

    public void setNormalColor(int normalColor) {
        mNormalTextColor = normalColor;
    }

    /**
     * @see Gravity
     */
    public void setGravity(int gravity) {
        mTextView.setGravity(gravity);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setTextSize(@DimenRes int textSize) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(textSize));
    }

    public void setFakeBoldText() {
        TextPaint paint = mTextView.getPaint();
        paint.setFakeBoldText(true);
    }

    public void setSelectedBackground(int selectedBackground) {
        mTextView.setBackgroundResource(selectedBackground);
    }

    public void setMinWidth(int minPixels) {
        mTextView.setMinWidth(minPixels);
    }

    private TextPaint getPaint() {
        return mTextView.getPaint();
    }

    private CharSequence getText() {
        return mTextView.getText();
    }

    public void setEnablePadding(boolean enablePadding) {
        this.enablePadding = enablePadding;

        if (enablePadding) {
            //int padding = IndicatorUtils.dip2px(context, 10);
            int verPadding = getResources().getDimensionPixelSize(R.dimen.dp_5);
            int horPadding = getResources().getDimensionPixelSize(R.dimen.dp_8);
            setPadding(horPadding, verPadding, horPadding, verPadding);
            setGravity(Gravity.CENTER);
        }
    }

    public boolean isEnablePadding() {
        return enablePadding;
    }

}