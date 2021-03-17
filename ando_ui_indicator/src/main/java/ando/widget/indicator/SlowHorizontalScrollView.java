package ando.widget.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Title: SlowHorizontalScrollView
 * <p>
 * Description: 降速版 HorizontalScrollView
 * </p>
 *
 * @author javakam
 * @date 2020/1/2  15:32
 */
public class SlowHorizontalScrollView extends HorizontalScrollView {
    public SlowHorizontalScrollView(Context context) {
        super(context);
    }

    public SlowHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void fling(int velocityX) {
        super.fling(velocityX / 3);
    }
}