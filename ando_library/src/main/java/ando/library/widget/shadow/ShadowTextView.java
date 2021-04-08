package ando.library.widget.shadow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

/**
 * 可设置阴影的TextView
 *
 * @author javakam
 * @date 2019/3/31 下午7:09
 */
public class ShadowTextView extends AppCompatTextView {

    public ShadowTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ShadowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShadowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ShadowDrawable drawable = ShadowDrawable.fromAttributeSet(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(this, drawable);
    }

}