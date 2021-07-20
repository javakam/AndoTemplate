package ando.toolkit.thread

import ando.toolkit.ThreadUtils

/**
 * Task，对回调做catch，防止崩溃
 *
 * ThreadUtils.executeByCpu(ThreadTask({
 *      //Time-consuming operation
 *  }, {
 *      callback?.invoke(it)
 *  }))
 */
class ThreadTask<T>(private val doInBackground: (() -> (T?))?, private val callback: ((T?) -> Unit)?) : ThreadUtils.Task<T>() {

    override fun doInBackground(): T? {
        try {
            return doInBackground?.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onSuccess(result: T?) {
        try {
            callback?.invoke(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFail(t: Throwable?) {
        try {
            callback?.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCancel() {
        try {
            callback?.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}