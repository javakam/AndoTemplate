package ando.repo.widget

import ando.dialog.usage.BaseDialog
import ando.repo.R
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import android.view.WindowManager

abstract class BottomDialog : BaseDialog {

    constructor(context: Context) : this(context, 0)
    constructor(context: Context, themeResId: Int = R.style.AndoDialog) : super(context, themeResId)

    override fun initConfig(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun initWindow(window: Window) {
        window.apply {
            setGravity(Gravity.BOTTOM)
            val params = attributes

            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION")
            manager.defaultDisplay.getMetrics(dm)

            params.width = dm.widthPixels
            attributes = params

            setWindowAnimations(R.style.AndoBottomDialogAnimation)
        }
    }

}