package ando.library.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

/**
 * 基于 API 30 的 CountDownTimer 加入了
 */
public class FixedCountDownTimer {
    public interface Listener {
        void onTick(long fixedMillisUntilFinished);

        void onFinish();
    }

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
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;

    //
    private Listener mListener;
    /**
     * 剩余时间(单位毫秒)
     */
    private long mMillisUntilFinished;
    /**
     * 暂停的状态
     */
    private boolean mPaused = false;

    /**
     * 是否正在倒计时
     */
    private boolean mIsRunning = false;

    public FixedCountDownTimer(long millisInFuture, long countDownInterval) {
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
        mIsRunning = true;
        mPaused = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    public final void pause() {
        mMillisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mIsRunning = false;
        mPaused = true;
    }

    public long resume() {
        // 结束的时间设置为当前时间加剩余时间
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisUntilFinished;
        mIsRunning = true;
        mPaused = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return mMillisUntilFinished;
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void stop() {
        mCancelled = true;
        mIsRunning = false;
        mPaused = false;
        mHandler.removeMessages(MSG);
    }

    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    private void onTick(long millisUntilFinished) {
        if (mListener != null) {
            //修复时间不准的问题
            final long fixedMillisUntilFinished = millisUntilFinished + (1000 - millisUntilFinished % 1000);
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