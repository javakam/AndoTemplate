package ando.library.widget.shadow

import ando.library.widget.shadow.ShadowDrawable.Companion.fromAttributeSet
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat

/**
 * 可设置阴影的按钮
 *
 * @author javakam
 * @date 2019/3/30 16:15
 */
class ShadowButton : AppCompatButton {
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
        val drawable = fromAttributeSet(context, attrs)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        ViewCompat.setBackground(this, drawable)
    }
}