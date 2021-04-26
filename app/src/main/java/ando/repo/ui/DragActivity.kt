package ando.repo.ui

import ando.dialog.core.DialogManager
import ando.repo.R
import ando.repo.widget.BottomDialog
import ando.repo.widget.SmartDragLayout
import ando.toolkit.ext.screenHeight
import ando.toolkit.ext.screenWidth
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * # 拖拽布局
 *
 * @author javakam
 * @date 2021/3/31  11:22
 */
class DragActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_drag)

        findViewById<View>(R.id.tv_show_draggable_dialog).setOnClickListener {
            draggableDialog(this)
                .show()
        }
    }

    //todo 2021年3月31日 16:33:00 拖拽Dialog
    fun draggableDialog(context: Context): DialogManager {
        val bottomDialog = object : BottomDialog(context, R.style.AndoLoadingDialogLight) {
            private val dragLayout: SmartDragLayout by lazy { findViewById(R.id.dragLayout) }

            override fun getLayoutId(): Int = R.layout.dialog_widget_drag
            override fun initView() {
                dragLayout.enableDrag(true)
                dragLayout.isThreeDrag(true)
                dragLayout.dismissOnTouchOutside(false)
                dragLayout.setOnCloseListener(object : SmartDragLayout.OnCloseListener {
                    override fun onClose() {
                        Log.i("123", "onClose...")
                    }

                    override fun onDrag(y: Int, percent: Float, isScrollUp: Boolean) {
                        printW("[${window.attributes.height}] onDrag y = [${y}], percent = [${percent}], isScrollUp = [${isScrollUp}]")

                        window?.apply {
                            val param = attributes
                            param.height = y
                            attributes = param
                        }
                    }

                    override fun onOpen() {
                        Log.i("123", "onOpen...")
                    }
                })
            }

            override fun onStart() {
                dragLayout.open()
                super.onStart()
            }

            override fun onDetachedFromWindow() {
                super.onDetachedFromWindow()
                dragLayout.close()
            }
        }

        return DialogManager.with(context, R.style.AndoLoadingDialog)
            .replaceDialog(bottomDialog)
            .setDimmedBehind(true)
            .setCancelable(true)
            .setSize(screenWidth, screenHeight / 2)
            .setCanceledOnTouchOutside(true)

    }

    private fun printW(msg: String) {
        //L.w("123", msg)
        Log.w("123", msg)
    }

}