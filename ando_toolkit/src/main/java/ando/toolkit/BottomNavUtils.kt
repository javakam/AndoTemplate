package ando.toolkit

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * # BottomNavUtils
 *
 * com.google.android.material.bottomnavigation.BottomNavigationView
 *
 * @author javakam
 * @date 2020/12/9  14:13
 */
class BottomNavUtils(private val containerId: Int, size: Int = 5) {

    private val fragmentArray = SparseArray<Fragment>(size)
    private lateinit var navView: BottomNavigationView
    private lateinit var fragmentManager: FragmentManager

    fun attach(navView: BottomNavigationView?, fragmentManager: FragmentManager?) {
        if (navView == null) {
            throw RuntimeException("BottomNavigationView can`t be null")
        }
        if (fragmentManager == null) {
            throw RuntimeException("FragmentManager can`t be null")
        }
        this.navView = navView
        this.fragmentManager = fragmentManager
    }

    fun putFragments(vararg arrayOfPairs: Pair<Int, Fragment>?) {
        if (arrayOfPairs.isNullOrEmpty()) return
        arrayOfPairs.forEach { p ->
            p?.apply {
                fragmentArray.put(first, second)
            }
        }
    }

    fun switchDefaultPage(pageId: Int) {
        fragmentManager
            .beginTransaction()
            .apply {
                fragmentArray.forEach { key, value ->
                    add(containerId, value, key.toString())
                }
            }
            .commitAllowingStateLoss()
        switchPage(pageId)
    }

    fun switchPage(id: Int) {
        fragmentManager
            .beginTransaction()
            .apply {
                fragmentArray.forEach { key, value ->
                    if (key == id) show(value) else hide(value)
                }
            }.commitAllowingStateLoss()
    }

    //Copy from androidx.core.util.SparseArrayKt.forEach
    private inline fun <T> SparseArray<T>.forEach(action: (key: Int, value: T) -> Unit) {
        for (index in 0 until size()) {
            action(keyAt(index), valueAt(index))
        }
    }

}