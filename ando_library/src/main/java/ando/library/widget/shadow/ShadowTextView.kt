package ando.library.widget.shadow

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat

/**
 * 可设置阴影的TextView
 *
 * @author javakam
 * @date 2019/3/31 下午7:09
 */
class ShadowTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val drawable = ShadowDrawable.fromAttributeSet(context, attrs)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        ViewCompat.setBackground(this, drawable)
    }
}