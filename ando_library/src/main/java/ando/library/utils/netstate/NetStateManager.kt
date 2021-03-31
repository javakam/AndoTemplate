package ando.library.utils.netstate

import ando.library.utils.netstate.NetStateReceiver.Companion.ANDROID_NET_CHANGE_ACTION
import ando.library.utils.netstate.NetStateReceiver.Companion.CUSTOM_ANDROID_NET_CHANGE_ACTION
import ando.toolkit.NetworkUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.Intent

/**
 * https://github.com/luckyAF/KommonMaster/blob/master/Kommon/src/main/java/com/luckyaf/kommon/manager/netstate/NetStateManager.kt
 */
object NetStateManager {

    private var mNetChangeObservers = ArrayList<NetChangeObserver>()
    private var mBroadcastReceiver:BroadcastReceiver ?=null

    interface NetChangeObserver {
        fun onNetChanged(state: NetworkUtils.NetworkType)
    }

    /**
     * 注册
     */
    fun registerNetworkStateReceiver(context: Context) {
        if (null == mBroadcastReceiver) {
            mBroadcastReceiver = NetStateReceiver.instance
        }
        val filter = IntentFilter()
        filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION)
        filter.addAction(ANDROID_NET_CHANGE_ACTION)
        context.applicationContext.registerReceiver(mBroadcastReceiver, filter)
    }

    /**
     * 注销
     */
    fun unRegisterNetworkStateReceiver(context: Context) {
        mBroadcastReceiver?.apply {
            try {
                context.applicationContext.unregisterReceiver(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mNetChangeObservers.clear()
    }

    /**
     * 检查当前网络
     * @param mContext
     */
    fun checkNetworkState(mContext: Context) {
        val intent = Intent()
        intent.action = CUSTOM_ANDROID_NET_CHANGE_ACTION
        mContext.sendBroadcast(intent)
    }

    fun notifyNetworkState(state: NetworkUtils.NetworkType) {
        mNetChangeObservers.map {
            it.onNetChanged(state)
        }
    }

    /**
     * 添加网络监听
     * 在activity 或fragment中调用
     * @param observer
     */
    fun registerObserver(observer: NetChangeObserver?) {
        observer?:return
        mNetChangeObservers.add(observer)
    }

    /**
     * 移除网络监听
     *  在activity 或fragment中调用
     * @param observer
     */
    fun removeRegisterObserver(observer: NetChangeObserver?) {
        observer?:return
        if (mNetChangeObservers.contains(observer)) {
            mNetChangeObservers.remove(observer)
        }
    }

}