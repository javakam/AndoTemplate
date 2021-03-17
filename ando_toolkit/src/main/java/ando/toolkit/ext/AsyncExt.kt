package ando.toolkit.ext

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * @author javakam
 * @date 2020/11/9  16:20
 */

/**
 * Execute [f] on the application UI thread.
 */
fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (ContextHelper.mainThread == Thread.currentThread()) f() else ContextHelper.handler.post { f() }
}

/**
 * Execute [f] on the application UI thread.
 */
inline fun Fragment.runOnUiThread(crossinline f: () -> Unit) {
    activity?.runOnUiThread { f() }
}

fun Context.postDelay(task: () -> Unit, delayMillis: Long) {
    if (ContextHelper.mainThread == Thread.currentThread()) {
        ContextHelper.mainHandler.postDelayed({
            task.invoke()
        }, delayMillis)
    }
}

inline fun Fragment.postDelay(crossinline f: () -> Unit, delayMillis: Long) {
    activity?.postDelay({ f() }, delayMillis)
}

/**
 * Execute [task] asynchronously.
 *
 * @param exceptionHandler optional exception handler.
 *  If defined, any exceptions thrown inside [task] will be passed to it. If not, exceptions will be ignored.
 * @param task the code to execute asynchronously.
 */
fun <T> T.doAsync(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    task: () -> Unit,
): Future<Unit> {
    return BackgroundExecutor.submit {
        return@submit try {
            task.invoke()
        } catch (thr: Throwable) {
            val result = exceptionHandler?.invoke(thr)
            if (result != null) {
                result
            } else Unit
        }
    }
}

fun <T> T.doAsyncDelay(
    task: () -> Unit,
    delay: Long,
): Future<Unit> {
    return BackgroundExecutor.submitDelay({
        return@submitDelay try {
            task.invoke()
        } catch (thr: Throwable) {
            crashLogger.invoke(thr)
        }
    }, delay)
}


fun <T> T.doAsync(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    executorService: ExecutorService,
    task: () -> Unit,
): Future<Unit> {
    return executorService.submit<Unit> {
        try {
            task.invoke()
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr)
        }
    }
}

fun <T, R> T.doAsyncResult(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    task: (T) -> R,
): Future<R> {
    return BackgroundExecutor.submit {
        try {
            task.invoke(this)
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr)
            throw thr
        }
    }
}

fun <T, R> T.doAsyncResult(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    executorService: ExecutorService,
    task: (T) -> R,
): Future<R> {
    return executorService.submit<R> {
        try {
            task.invoke(this)
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr)
            throw thr
        }
    }
}

internal object BackgroundExecutor {
    var executor: ExecutorService =
        Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())

    fun <T> submit(task: () -> T): Future<T> = executor.submit(task)

    fun <T> submitDelay(task: () -> T, delay: Long): Future<T> =
        Executors.newSingleThreadScheduledExecutor().schedule(task, delay, TimeUnit.MILLISECONDS)
}

private val crashLogger = { throwable: Throwable -> throwable.printStackTrace() }

private object ContextHelper {
    val handler = Handler(Looper.myLooper())
    val mainHandler = Handler(Looper.getMainLooper())
    val mainThread: Thread = Looper.getMainLooper().thread
}