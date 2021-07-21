package ando.toolkit.http

import ando.toolkit.AppUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.IntDef

/**
 * https://blog.csdn.net/weixin_42824294/article/details/87919949
 */

const val NET_STATE_NONE: Int = -1   // 没有连网
const val NET_STATE_MOBILE: Int = 1  // 移动网络
const val NET_STATE_WIFI: Int = 2    // 无线网络

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [NET_STATE_NONE, NET_STATE_MOBILE, NET_STATE_WIFI])
annotation class NetState

interface NetStateEvent {
    fun onNetChange(@NetState netMobile: Int)
}

class NetStateReceiver : BroadcastReceiver() {
    private var netStateEvent: NetStateEvent? = null
    override fun onReceive(context: Context, intent: Intent) {
        //onReceive这里写上相关的处理代码，一般来说，不要此添加过多的逻辑或者是进行任何的耗时操作
        //因为广播接收器中是不允许开启多线程的，过久的操作就会出现报错
        //广播接收器更多的是扮演一种打开程序其他组件的角色，比如创建一条状态栏通知，或者启动某个服务

        //判断广播的类型为网络action后
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            //获取当前网络状态，并将结果发送给广播接受者
            netStateEvent?.onNetChange(getNetWorkState())
        }
    }

    fun setNetStateEvent(netStateEvent: NetStateEvent?) {
        this.netStateEvent = netStateEvent
    }

    /**
     * 获取网络状态
     *
     * WiFi不可用或未连接，返回2
     * WiFi需要认证，返回1
     * WiFi可用，返回0
     */
    private fun getNetWorkState(): Int {
        val info = NetworkUtils.getActiveNetworkInfo()
        //判断网络处于连接状态
        if (info != null && info.isConnected) {
            //获取网络的类型
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                if (AppUtils.isDebug()) Log.i("通知", "当前网络处于WiFi状态")
                //是否可上网
                return if (NetworkUtils.isNetworkOnline()) {
                    NET_STATE_WIFI
                } else {
                    NET_STATE_NONE
                }
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                return NET_STATE_MOBILE
            }
        } else {
            return NET_STATE_NONE
        }
        return NET_STATE_NONE
    }

}