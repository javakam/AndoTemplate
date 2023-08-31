package ando.repo.ui

import ando.dialog.bottomsheet.ModalBottomSheetDialogFragment
import ando.toolkit.FixedCountDownTimer
import ando.repo.R
import ando.repo.config.AppRouter
import ando.repo.config.click
import ando.repo.ui.webview.WebViewCoordinatorActivity
import ando.repo.ui.webview.WebViewScrollViewActivity
import ando.repo.ui.webview.WebViewUsageActivity
import ando.repo.ui.webview.WebViewVideoActivity
import ando.widget.option.list.OptionItem
import ando.widget.option.list.OptionView
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlin.math.min

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private var dismissibleDialog: ModalBottomSheetDialogFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        siuuuuuuuuuu()
    }

    private fun siuuuuuuuuuu() {
        click(R.id.bt_tool_kit) {
            AppRouter.toToolKit(this)
        }

        click(R.id.bt_widget_tab_layout) {
            AppRouter.toWidgetTabLayout(this)
        }

        //ando indicator 👉 Google TabLayout 的替代品
        click(R.id.bt_widget_indicator) {
            AppRouter.toWidgetIndicator(this)
        }

        //ando banner & ando indicator
        click(R.id.bt_widget_banner) {
            AppRouter.toWidgetBanner(this)
        }

        //ando banner guide
        click(R.id.bt_widget_banner_guide) {
            AppRouter.toWidgetBannerGuide(this)
        }

        //SupperButton
        click(R.id.bt_widget_button) {
            AppRouter.toWidgetButton(this)
        }

        //RecyclerDecorationProviderActivity
        click(R.id.bt_widget_recycler_decoration) {
            AppRouter.toWidgetRecycler(this)
        }

        //Diff
        click(R.id.bt_widget_recycler_diff) {
            AppRouter.toWidgetRecyclerDiff(this)
        }

        //底部弹窗

        click(R.id.bt_widget_bottom_sheet) {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                //.setTitle("分享")
                //.setTitleLayout(R.layout.layout_bottom_sheet_fragment_header)
                .addItem(this, R.menu.options)
                .setItemViewDirection(true)
                .setTopRounded(true)//顶部圆角控制
                .setColumns(3)
                .setDraggable(true)
                .setOnItemClickListener(object : OptionView.OnItemClickListener {
                    override fun onItemSelected(item: OptionItem) {
                        Toast.makeText(applicationContext, "点击: " + item.title, Toast.LENGTH_LONG).show()
                    }
                })
                .build()

            dismissibleDialog?.show(supportFragmentManager, "分享")
        }

        //String Expand
        click(R.id.bt_widget_string_expand) {
            AppRouter.toStringExpand(this)
        }

        //CountDownTimer
        mTvDownTimer.text = "CountDownTimer 👉 $mCount"
        mTvFixedDownTimer.text = "FixedCountDownTimer 👉 ${mFixedCount / 1000}秒"
        click(R.id.bt_countdown_timer_start) {
            //mCountDownTimer.start()

            if (!mFixedCountDownTimer.isRunning) {
                if (mFixedCountDownTimer.isPaused) {
                    mFixedCountDownTimer.resume()
                } else {
                    mFixedCountDownTimer.start()
                }
            }
        }
        click(R.id.bt_countdown_timer_pause) {
            mFixedCountDownTimer.pause()
        }
        click(R.id.bt_countdown_timer_stop) {
            mFixedCountDownTimer.cancel()
        }
        //WebView
        click(R.id.bt_webview01) {
            startActivity(Intent(this, WebViewUsageActivity::class.java))
        }
        click(R.id.bt_webview02) {
            startActivity(Intent(this, WebViewScrollViewActivity::class.java))
        }
        click(R.id.bt_webview03) {
            startActivity(Intent(this, WebViewVideoActivity::class.java))
        }
        click(R.id.bt_webview04) {
            startActivity(Intent(this, WebViewCoordinatorActivity::class.java))
        }
    }

    private val mTvDownTimer: TextView by lazy { findViewById(R.id.tv_countdown_timer) }
    private val mTvFixedDownTimer: TextView by lazy { findViewById(R.id.tv_countdown_timer_fixed) }
    private val mCount: Long = 10
    private val mCountDownTimer: CountDownTimer = object : CountDownTimer(mCount * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val next: Long = millisUntilFinished + (1000 - millisUntilFinished % 1000)
            val showTime: Long = min(mCount, next / 1000)
            mTvDownTimer.text = "CountDownTimer 👉 $showTime"

            Log.w(
                "123",
                "onTick millisUntilFinished=$millisUntilFinished  next=$next  showTime=$showTime"
            )
        }

        override fun onFinish() {
            mTvDownTimer.text = "CountDownTimer 👉 0"
            Log.e("123", "CountDownTimer onFinish")
        }
    }

    //10 分钟 -> 10 * 60 * 1000
    //60 分钟 -> 60 * 60 * 1000
    private val mFixedCount: Long = 5 * 1000
    private val mFixedCountDownTimer: FixedCountDownTimer = FixedCountDownTimer(mFixedCount, 1000)
        .apply {
            setListener(object : FixedCountDownTimer.Listener {
                override fun onTick(fixedMillisUntilFinished: Long) {
                    val showTime: Long = min(mFixedCount, fixedMillisUntilFinished / 1000)
                    mTvFixedDownTimer.text = "FixedCountDownTimer 👉 $showTime"
                    Log.e(
                        "123",
                        "onTick fixedMillisUntilFinished=$fixedMillisUntilFinished  showTime=$showTime"
                    )
                }

                override fun onFinish() {
                    mTvFixedDownTimer.text = "FixedCountDownTimer 👉 0"
                    Log.e("123", "FixedCountDownTimer onFinish")
                }
            })
        }

}