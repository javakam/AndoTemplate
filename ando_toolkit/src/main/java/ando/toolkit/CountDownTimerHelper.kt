package ando.toolkit

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.widget.TextView

/**
 * # 倒计时Button帮助类
 *
 * @author javakam
 * @date 2018/11/22 上午12:38
 */
class CountDownTimerHelper constructor(
    private val mButton: TextView,      //需要显示倒计时的Button
    private val mCountDownTime: Int,    //倒计时总时间,单位是秒
    private val mInterval: Int = 1      //倒计时间期，单位是秒
) {
    /**
     * 倒计时timer
     */
    private var mCountDownTimer: CountDownTimer? = null
    private var mListener: OnCountDownListener? = null

    /**
     * 初始化倒计时器
     */
    private fun initCountDownTimer() {
        // 由于CountDownTimer并不是准确计时，在onTick方法调用的时候，time会有1-10ms左右的误差，这会导致最后一秒不会调用onTick()
        // 因此，设置间隔的时候，默认减去了10ms，从而减去误差。
        // 经过以上的微调，最后一秒的显示时间会由于10ms延迟的积累，导致显示时间比1s长max*10ms的时间，其他时间的显示正常,总时间正常
        if (mCountDownTimer == null) {
            mCountDownTimer =
                object : CountDownTimer(
                    (mCountDownTime * 1000).toLong(),
                    (mInterval * 1000 - 10).toLong()
                ) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(time: Long) {
                        // 第一次调用会有1-10ms的误差，因此需要+15ms，防止第一个数不显示，第二个数显示2s
                        val surplusTime = ((time + 15) / 1000).toLong()
                        if (mListener != null) {
                            mListener?.onCountDown(surplusTime)
                        } else {
                            mButton.text = "$surplusTime s"
                        }
                    }

                    override fun onFinish() {
                        mButton.isEnabled = true
                        if (mListener != null) {
                            mListener?.onFinished()
                        } else {
                            mButton.text = mButton.resources.getString(R.string.ando_count_down_finish)
                        }
                    }
                }
        }
    }

    /**
     * 开始倒计时
     */
    fun start() {
        mButton.isEnabled = false
        mCountDownTimer?.start()
    }

    /**
     * 设置倒计时的监听器
     *
     * @param listener
     */
    fun setOnCountDownListener(listener: OnCountDownListener?): CountDownTimerHelper {
        mListener = listener
        return this
    }

    /**
     * 计时时监听接口
     */
    interface OnCountDownListener {
        /**
         * 正在倒计时
         * @param restTime 剩余的时间
         */
        fun onCountDown(restTime: Long)

        /**
         * 倒计时结束
         */
        fun onFinished()
    }

    /**
     * 取消倒计时
     */
    fun cancel() {
        if (mCountDownTimer != null) {
            mCountDownTimer?.cancel()
        }
    }

    init {
        initCountDownTimer()
    }
}