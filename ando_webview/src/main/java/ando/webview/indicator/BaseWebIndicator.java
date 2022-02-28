package ando.webview.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * # BaseWebIndicator
 *
 * @author javakam
 * 2020/8/24 15:43
 */
public abstract class BaseWebIndicator extends FrameLayout implements IWebIndicator {

    public BaseWebIndicator(Context context) {
        this(context, null, 0, 0);
    }

    public BaseWebIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public BaseWebIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseWebIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void reset() {
    }

    @Override
    public void setProgress(int newProgress) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }
}