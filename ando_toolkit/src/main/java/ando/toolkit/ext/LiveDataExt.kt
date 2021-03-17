package ando.toolkit.ext

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


/**
 * usage :
 *      _viewStateLiveData.postNext { last ->
 *          last.copy(isLoading = false, throwable = null)
 *      }
 */
@AnyThread
inline fun <reified T> MutableLiveData<T>.postNext(map: (T) -> T) {
    postValue(map(verifyLiveDataNotEmpty()))
}

/**
 * usage:
 *      _viewStateLiveData.setNext { last ->
 *          last.copy(isLoading = true, throwable = null, pagedList = null, nextPageData = null, sort = sort)
 *      }
 */
@MainThread
inline fun <reified T> MutableLiveData<T>.setNext(map: (T) -> T) {
    value = map(verifyLiveDataNotEmpty())
}

@AnyThread
inline fun <reified T> LiveData<T>.verifyLiveDataNotEmpty(): T {
    return value
            ?: throw NullPointerException("MutableLiveData<${T::class.java}> not contain value.")
}