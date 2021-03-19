package ando.repo.ui.indicator

import ando.repo.R
import ando.repo.config.AppRouter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * # IndicatorActivity
 *
 * Modify https://github.com/hackware1993/MagicIndicator
 *
 * @author javakam
 * @date 2021/3/17  16:24
 */
class IndicatorActivity : AppCompatActivity() {

    private val mBtVpNo: Button by lazy { findViewById(R.id.bt_indicator_vp_no) }
    private val mBtVp: Button by lazy { findViewById(R.id.bt_indicator_vp) }
    private val mBtOrigin: Button by lazy { findViewById(R.id.bt_indicator_origin) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_indicator)
        displayUsage()
    }

    private fun displayUsage() {
        //顶部导航 + Fragment + 不带ViewPager
        mBtVpNo.setOnClickListener {
            AppRouter.toWidgetIndicatorViewPagerNo(this)
        }

        //顶部导航 + Fragment + ViewPager
        mBtVp.setOnClickListener {
            AppRouter.toWidgetIndicatorViewPager(this)
        }

        //
        mBtOrigin.setOnClickListener {
            AppRouter.toWidgetIndicatorOrigin(this)
        }

    }

}