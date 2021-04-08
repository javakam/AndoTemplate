package ando.library.utils.task;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

public class TinyTaskExecutor {

    private volatile static TinyTaskExecutor sTinyTaskExecutor;

    private final ExecutorService mExecutor;
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private static final HashMap<Runnable, Runnable> S_DELAY_TASKS = new HashMap<>();

    public static TinyTaskExecutor getInstance() {
        if (sTinyTaskExecutor == null) {
            synchronized (TinyTaskExecutor.class) {
                sTinyTaskExecutor = new TinyTaskExecutor();
            }
        }
        return sTinyTaskExecutor;
    }

    public TinyTaskExecutor() {
        mExecutor = new TaskThreadPoolExecutor(true);
    }

    private static ExecutorService getExecutor() {
        return getInstance().mExecutor;
    }

    public static Handler getMainThreadHandler() {
        return getInstance().mMainThreadHandler;
    }

    public static void execute(Runnable runnable) {
        execute(runnable, 0);
    }

    public static void execute(final Runnable runnable, long delayMillisecond) {
        if (runnable == null) {
            return;
        }
        if (delayMillisecond < 0) {
            return;
        }

        if (!getExecutor().isShutdown()) {
            if (delayMillisecond > 0) {
                Runnable delayRunnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (S_DELAY_TASKS) {
                            S_DELAY_TASKS.remove(runnable);
                        }
                        realExecute(runnable);
                    }
                };

                synchronized (S_DELAY_TASKS) {
                    S_DELAY_TASKS.put(runnable, delayRunnable);
                }

                getMainThreadHandler().postDelayed(delayRunnable, delayMillisecond);
            } else {
                realExecute(runnable);
            }
        }
    }

    /**
     * real executor
     */
    private static void realExecute(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    /**
     * remove task
     */
    public static void removeTask(final Runnable runnable) {
        if (runnable == null) {
            return;
        }

        Runnable delayRunnable;
        synchronized (S_DELAY_TASKS) {
            delayRunnable = S_DELAY_TASKS.remove(runnable);
        }

        if (delayRunnable != null) {
            getMainThreadHandler().removeCallbacks(delayRunnable);
        }
    }

    /**
     * post to main thread, do not handle time-consuming job
     */
    public static void postToMainThread(final Runnable task) {
        postToMainThread(task, 0);
    }

    /**
     * post to main thread, do not handle time-consuming job
     */
    public static void postToMainThread(final Runnable task, long delayMillis) {
        if (task == null) {
            return;
        }

        getMainThreadHandler().postDelayed(task, delayMillis);
    }

    /**
     * remove delay task from main thread
     */
    public static void removeMainThreadRunnable(Runnable task) {
        if (task == null) {
            return;
        }

        getMainThreadHandler().removeCallbacks(task);
    }

    /**
     * check current thread is main thread or not
     */
    public static boolean isMainThread() {
        return Thread.currentThread() == getInstance().mMainThreadHandler.getLooper().getThread();
    }
}
