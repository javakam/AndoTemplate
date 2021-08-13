package ando.toolkit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 基于 API 30 的 CountDownTimer 做了以下调整:
 * <p>
 * 1. 修复计时不准的问题;
 * 2. 增加 暂停/继续
 * </p>
 *
 * @author javakam
 */
public class FixedCountDownTimer {

    public interface Listener {
        /**
         * 每隔 mCountdownInterval 毫秒触发一次
         */
        void onTick(long fixedMillisUntilFinished);

        /**
         * 完成时触发
         */
        void onFinish();
    }

    private static final long SECOND_MILLIS = 1000L;

    /**
     * 要执行的时间(单位毫秒), 此时间并不包含暂停的时间
     * <p>
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * 间隔时间(单位毫秒)
     * <p>
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    /**
     * 结束的时间(单位毫秒), 如果暂停会变化
     */
    private long mStopTimeInFuture;

    /**
     * 剩余时间(单位毫秒)
     */
    private long mMillisUntilFinished;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;

    /**
     * 暂停的状态
     */
    private boolean mIsPaused = false;

    /**
     * 是否正在倒计时
     */
    private boolean mIsRunning = false;

    /**
     * 倒计时开始的时间
     */
    private Date mStartTime;

    private Listener mListener;

    public FixedCountDownTimer(long millisInFuture, long countDownInterval) {
        //注: 由于 sendMessageDelayed 的机制造成的 1000 毫秒的延时
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public void setListener(Listener l) {
        this.mListener = l;
    }

    /**
     * Start the countdown.
     */
    public synchronized final FixedCountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStartTime = new Date();
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mIsRunning = true;
        mIsPaused = false;
        return this;
    }

    public synchronized final void pause() {
        mMillisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mIsRunning = false;
        mIsPaused = true;
    }

    public synchronized long resume() {
        // 结束的时间设置为当前时间加剩余时间
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisUntilFinished;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mIsRunning = true;
        mIsPaused = false;
        return mMillisUntilFinished;
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mHandler.removeMessages(MSG);
        mCancelled = true;
        mIsRunning = false;
        mIsPaused = false;
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    private void onTick(long millisUntilFinished) {
        if (mListener != null) {
            //修复时间不准的问题, 参考 TextClock
            final long fixedMillisUntilFinished = millisUntilFinished + (SECOND_MILLIS - millisUntilFinished % SECOND_MILLIS);
            /*
            millisUntilFinished=1998; fixedMillisUntilFinished=2000
            millisUntilFinished=997; fixedMillisUntilFinished=1000
            ...
             */
            Log.w("123", "millisUntilFinished=" + millisUntilFinished + "; fixedMillisUntilFinished=" + fixedMillisUntilFinished);
            mListener.onTick(fixedMillisUntilFinished);
        }
    }

    private void onFinish() {
        mIsRunning = false;
        if (mListener != null) {
            mListener.onFinish();
        }
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public long getMinutesInFuture() {
        return TimeUnit.MILLISECONDS.toMinutes(mMillisInFuture);
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    private static final int MSG = 1;

    // handles counting down
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (FixedCountDownTimer.this) {
                if (mCancelled) {
                    return;
                }
                if (mIsPaused) {
                    removeMessages(MSG);
                    return;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    onFinish();
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);

                    // take into account user's onTick taking time to execute
                    long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                    long delay;

                    if (millisLeft < mCountdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration;

                        // special case: user's onTick took more than interval to
                        // complete, trigger onFinish without delay
                        if (delay < 0) {
                            delay = 0;
                        }
                    } else {
                        delay = mCountdownInterval - lastTickDuration;

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) {
                            delay += mCountdownInterval;
                        }
                    }

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

}