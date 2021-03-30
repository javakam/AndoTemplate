package ando.library.widget

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * https://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650830296&idx=1&sn=a9f44ac6cd9ec53149e59a380403d04d&chksm=80b7a146b7c02850a96c5300e967f01fb4d58c1c9d320889fae043fd7b12cf42fde741722d52&scene=126&sessionid=1586088807&key=d8f7014a0480479388044a24ea66c7eddad0652250cd0ed1c25b01ad0e08f5171352f18457a060475e0d84e674982e1ced5c57763dac1ff52b817a5a0fdb5d22773d2b68a63568d73aa7246cc9c7b3ee&ascene=1&uin=MTc2MzYzMzc2MQ%3D%3D&devicetype=Windows+10&version=62080079&lang=zh_CN&exportkey=AUTJTMThNssgS87OFX3kMdA%3D&pass_ticket=TfZROikPmeKFmDh1qldS7qmv3lYejVybp0z9n3SjnamhHS5LCrgXaA617EMrRnK4
 *
 * WebView 部分情况下会显示异常 , 关闭硬件加速即可 :  mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
 *
 * @author javakam
 * @date 2020-09-28 10:51:53
 */
class GrayFrameLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val mPaint = Paint()
    override fun dispatchDraw(canvas: Canvas) {
        canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun draw(canvas: Canvas) {
        canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG)
        super.draw(canvas)
        canvas.restore()
    }

    init {
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        mPaint.colorFilter = ColorMatrixColorFilter(cm)

        // java.lang.ClassCastException: android.view.ContextThemeWrapper cannot be cast
        // to android.app.Activity
        if (context is Activity) {
            context.window.decorView.setLayerType(LAYER_TYPE_HARDWARE, mPaint)
        }
    }
}