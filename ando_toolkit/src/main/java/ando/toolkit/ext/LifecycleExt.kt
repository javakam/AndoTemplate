package ando.toolkit.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * usage:
 *  In Fragment: observe(mViewModel.viewStateLiveData, this::onNewState)
 */
fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (t: T) -> Unit) {
    liveData.observe(this, { it?.let { t -> observer(t) } })
}
