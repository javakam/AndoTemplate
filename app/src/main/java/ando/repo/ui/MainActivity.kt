package ando.repo.ui

import ando.library.utils.FixedCountDownTimer
import ando.repo.R
import ando.repo.config.AppRouter
import ando.repo.config.click
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import kotlin.math.min

class MainActivity : AppCompatActivity() {
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

        //SmartDragLayout
        click(R.id.bt_widget_drag) {
            AppRouter.toWidgetDrag(this)
        }

        //底部弹窗
        click(R.id.bt_widget_bottom_sheet) {
        }

        //String Expand
        click(R.id.bt_widget_string_expand) {
            AppRouter.toStringExpand(this)
        }

        //CountDownTimer
        mTvDownTimer.text = "CountDownTimer 👉 $mCount"
        mTvFixedDownTimer.text = "FixedCountDownTimer 👉 $mCount"
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
            //mCountDownTimer.cancel()
            mFixedCountDownTimer.stop()
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

            Log.w("123", "onTick millisUntilFinished=$millisUntilFinished  next=$next  showTime=$showTime")
        }

        override fun onFinish() {
            mTvDownTimer.text = "CountDownTimer 👉 0"
            Log.e("123", "CountDownTimer onFinish")
        }
    }
    private val mFixedCountDownTimer: FixedCountDownTimer = FixedCountDownTimer(mCount * 1000, 1000)
        .apply {
            setListener(object : FixedCountDownTimer.Listener {
                override fun onTick(fixedMillisUntilFinished: Long) {
                    val showTime: Long = min(mCount, fixedMillisUntilFinished / 1000)
                    mTvFixedDownTimer.text = "FixedCountDownTimer 👉 $showTime"

                    Log.e("123", "onTick fixedMillisUntilFinished=$fixedMillisUntilFinished  showTime=$showTime")
                }

                override fun onFinish() {
                    mTvFixedDownTimer.text = "FixedCountDownTimer 👉 0"
                    Log.e("123", "FixedCountDownTimer onFinish")
                }
            })
        }

}