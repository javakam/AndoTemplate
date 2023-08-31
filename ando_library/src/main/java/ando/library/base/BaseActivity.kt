package ando.library.base

import ando.library.base.BaseApplication.Companion.exit
import ando.library.base.BaseApplication.Companion.isGray
import ando.library.widget.GrayFrameLayout
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * # BaseActivity
 *
 * @author javakam
 * @date 2019/3/17 13:17
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * 系统DecorView的根View
     */
    protected lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        initActivityStyle()
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    protected open fun initActivityStyle() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // 灰度处理 : WebView 有时会显示异常
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if (isGray) {
            if ("FrameLayout" == name) {
                val count = attrs.attributeCount
                for (i in 0 until count) {
                    val attributeName = attrs.getAttributeName(i)
                    val attributeValue = attrs.getAttributeValue(i)
                    if ("id" == attributeName) {
                        val id = attributeValue.substring(1).toInt()
                        val idVal = resources.getResourceName(id)
                        if ("android:id/content" == idVal) {
                            //因为这是API 23之后才能改变的，所以你的判断版本
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                //获取窗口区域
                                val window = this.window
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                                //设置状态栏颜色
                                window.statusBarColor = Color.parseColor("#858585")
                                //设置显示为白色背景，黑色字体
//                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                            }
                            // grayFrameLayout.setBackgroundDrawable(getWindow().getDecorView().getBackground());
                            return GrayFrameLayout(context, attrs)
                        }
                    }
                }
            }
        }
        return super.onCreateView(name, context, attrs)
    }

    /**
     * 重写 getResource 方法，防止app字体大小受系统字体大小影响
     *
     * https://www.jianshu.com/p/5effff3db399
     */
    override fun getResources(): Resources {
        val resources = super.getResources()
        if (resources != null && resources.configuration.fontScale != 1.0f) {
            val configuration = resources.configuration
            configuration.fontScale = 1.0f
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        return resources
    }

    fun setUpToolBar(toolbarId: Int, toolBarTitleId: Int, title: String?) {
        val toolbar = findViewById<View>(toolbarId) as Toolbar
        val toolBarTitle = toolbar.findViewById<View>(toolBarTitleId) as? TextView
        toolBarTitle?.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        toolBarTitle?.text = title
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            it?.let {
                if ((it as? IBackPressed)?.onBackPressed() == true) {
                    return
                }
            }
        }
        super.onBackPressed()
    }

    /**
     * 连续点击两次退出 APP
     */
    private var exitTime: Long = 0

    protected fun exitBy2Click(delay: Long = 2000L, @StringRes text: Int, block: () -> Unit) {
        if (System.currentTimeMillis() - exitTime > delay) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            block.invoke()
            exit()
        }
    }
}

abstract class BaseMvcActivity : BaseActivity(), IBaseInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = getLayoutId()
        if (layoutId > 0) {
            setContentView(layoutId)
        } else {
            setContentView(getLayoutView())
        }
        mView = findViewById(android.R.id.content)
        initView(savedInstanceState)
        initListener()
        initData()
    }
}