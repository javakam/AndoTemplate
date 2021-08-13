package ando.toolkit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

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
        void onTick(long fixedMillisUntilFinished);

        void onFinish();
    }

    private static final long SECOND_MILLIS = 1000L;

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

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
    private boolean mPaused = false;

    /**
     * 是否正在倒计时
     */
    private boolean mIsRunning = false;

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
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mIsRunning = true;
        mPaused = false;
        return this;
    }

    public synchronized final void pause() {
        mMillisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mIsRunning = false;
        mPaused = true;
    }

    public synchronized long resume() {
        // 结束的时间设置为当前时间加剩余时间
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisUntilFinished;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mIsRunning = true;
        mPaused = false;
        return mMillisUntilFinished;
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mHandler.removeMessages(MSG);
        mCancelled = true;
        mIsRunning = false;
        mPaused = false;
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

    public boolean isPaused() {
        return mPaused;
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
                if (mPaused) {
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