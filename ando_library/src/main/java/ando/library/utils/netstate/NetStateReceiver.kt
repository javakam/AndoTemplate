package ando.library.utils.netstate

import ando.toolkit.NetworkUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission

/**
 * 类描述：网络状态监听广播
 * < uses-permission android:name="android.permission.INTERNET" />
 * < uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * < uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * @author Created by luckyAF on 2019-02-18
 *
 */
class NetStateReceiver private constructor() : BroadcastReceiver() {

    companion object {
        val instance = SingletonHolder.holder
        const val CUSTOM_ANDROID_NET_CHANGE_ACTION = "ando.library.CONNECTIVITY_CHANGE"
        const val ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    private object SingletonHolder {
        val holder = NetStateReceiver()
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onReceive(context: Context?, intent: Intent?) {
        context?:return
        intent?:return
        if (intent.action.equals(ANDROID_NET_CHANGE_ACTION,true)
                || intent.action.equals(CUSTOM_ANDROID_NET_CHANGE_ACTION,true)) {
            NetStateManager.notifyNetworkState(NetworkUtils.getNetworkType())
        }
    }

}