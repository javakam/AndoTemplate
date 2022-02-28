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
        super(context);
    }

    public BaseWebIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWebIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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