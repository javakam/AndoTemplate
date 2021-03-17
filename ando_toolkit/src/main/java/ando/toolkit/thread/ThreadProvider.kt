package ando.toolkit.thread

import ando.toolkit.log.L
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

/**
 * # ThreadProvider
 *
 * Description: Modify from [android.os.AsyncTask]
 * <pre>
 * usage:
 *
 * case R.id.btAsyncTaskSerial://串行执行
 * new MyAsyncTask(mActivity, "Task#c1 ").execute("123");
 * new MyAsyncTask(mActivity, "Task#c2 ").execute("123");
 * new MyAsyncTask(mActivity, "Task#c3 ").execute("123");
 * break;
 *
 * case R.id.btAsyncTaskParallel://并行执行 -- 使用 AsyncTask 自带的线程池
 * new MyAsyncTask(mActivity, "Task#b1 ").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "123");
 * new MyAsyncTask(mActivity, "Task#b2 ").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "123");
 * new MyAsyncTask(mActivity, "Task#b3 ").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "123");
 * break;
 *
 * case R.id.btAsyncTaskParallelByUs://并行执行 -- 自定义线程池
 * new MyAsyncTask(mActivity, "Task#bb1 ").executeOnExecutor(MY_THREAD_POOL_EXECUTOR, "123");
 * new MyAsyncTask(mActivity, "Task#bb2 ").executeOnExecutor(MY_THREAD_POOL_EXECUTOR, "123");
 * new MyAsyncTask(mActivity, "Task#bb3 ").executeOnExecutor(MY_THREAD_POOL_EXECUTOR, "123");
 * break;
 *
 * case R.id.btAsyncTaskParallelByUs2://并行执行 -- 自定义线程池 另外一种方式
 * MyAsyncTask.setDefaultExecutor(MY_THREAD_POOL_EXECUTOR);//替换掉默认的  AsyncTask.SERIAL_EXECUTOR
 * new MyAsyncTask(mActivity, "Task#a ").execute("abc");
 * new MyAsyncTask(mActivity, "Task#b ").execute("abc");
 * new MyAsyncTask(mActivity, "Task#c ").execute("abc");
 * break;
 *
 * </pre>
 *
 * @author javakam
 * 2019/11/27  12:57
 */
object ThreadProvider {
    private const val TAG = "ThreadProvider"

    //自定义线程池
    private var CUSTOM_THREAD_POOL_EXECUTOR: Executor? = null

    //Java虚拟机可用的处理器数量 -- 即 CPU核❤数
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()

    //线程池的核心线程数。默认情况下，核心线程会一直存活，即使它们处于闲置状态。
    //【注】设置了超时机制除外，当 allowCoreThreadTimeOut 属性为 true 时,那么闲置的核心线程在等待新任务到来时会有超时策略，
    //当等待时间超过 keepAliveTime 所指定的时长后，核心线程就会被终止！
    private val CORE_POOL_SIZE = max(2, min(CPU_COUNT - 1, 4))

    //线程池所能容纳的最大线程数。当活动线程数达到这个数值后，后续的新任务将会被阻塞。
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1

    //非核心线程闲置时的超时时长，超过这个时长，非核心线程就会被回收。
    //当 allowCoreThreadTimeOut 属性为 true 时，keepAliveTime 同样作用于核心线程。  单位:秒
    private const val KEEP_ALIVE_SECONDS: Long = 20

    //线程池中的任务队列。
    //通过 线程池 的 execute 方法提交的 Runnable 对象会存储在里面。
    private val sPoolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(128)

    //线程工厂，为线程池提供创建线程的功能。
    //ThreadFactory是个接口，只有一个方法：Thread newThread(Runnable r);
    private val threadFactory: ThreadFactory = object : ThreadFactory {
        private val mCount = AtomicInteger(1)
        override fun newThread(runnable: Runnable): Thread {
            return Thread(
                runnable,
                Thread.currentThread().name + " AsyncTask # " + mCount.getAndIncrement()
            )
        }
    }

    fun exec(runnable: Runnable?) = CUSTOM_THREAD_POOL_EXECUTOR?.execute(runnable)

    init {
        //征服手持机 Android6.0: CPU核心数 : 8
        L.d(TAG, "CPU_COUNT : $CPU_COUNT")
        //征服手持机 Android6.0: 自定义线程池的配置：CORE_POOL_SIZE: 4  MAXIMUM_POOL_SIZE: 17  KEEP_ALIVE_SECONDS: 60
        L.d(
            TAG,
            "自定义线程池的配置：" + "CORE_POOL_SIZE: " + CORE_POOL_SIZE
                    + "  MAXIMUM_POOL_SIZE: " + MAXIMUM_POOL_SIZE
                    + "  KEEP_ALIVE_SECONDS: " + KEEP_ALIVE_SECONDS
        )

        val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            sPoolWorkQueue, threadFactory
        )
        threadPoolExecutor.allowCoreThreadTimeOut(true)
        CUSTOM_THREAD_POOL_EXECUTOR = threadPoolExecutor

        //或者简单点的线程池
//      Executor exec = new ThreadPoolExecutor(15, 200, 10,
//          TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
}