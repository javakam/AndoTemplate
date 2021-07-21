package ando.repo.ui.toolkit

import ando.repo.R
import ando.toolkit.http.NetworkUtils
import ando.toolkit.ThreadUtils
import ando.toolkit.http.*
import ando.toolkit.thread.ThreadTask
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * @author javakam
 * @date 2021-07-21  13:57
 */
class ToolKitActivity : AppCompatActivity(), NetStateEvent {

    private val tvNetState: TextView by lazy { findViewById(R.id.tvNetState) }
    private val tvNetInfo: TextView by lazy { findViewById(R.id.tvNetInfo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolkit)

        testNetworkUtils()
    }

    @SuppressLint("SetTextI18n")
    private fun testNetworkUtils() {
        tvNetInfo.text = "请稍候..."
        val nextLine = "\n"
        ThreadUtils.executeByCpu(ThreadTask({
            //异步处理
            //Caused by: android.os.NetworkOnMainThreadException
            "isAvailable=${NetworkUtils.isAvailable()} " + nextLine +
                    "isAvailableByDns=${NetworkUtils.isAvailableByDns()} " + nextLine +
                    "isAvailableByPing=${NetworkUtils.isAvailableByPing()} " + nextLine +
                    "isWifiAvailable=${NetworkUtils.isWifiAvailable()}" + nextLine +
                    "getDomainAddress=${NetworkUtils.getDomainAddress("www.baidu.com")}" + nextLine +
                    "getMacAddress=${NetworkUtils.getMacAddress()}" + nextLine
        }, {
            tvNetInfo.postDelayed({
                tvNetInfo.text = "Available Info:$nextLine${it}" + nextLine +
                        "getNetworkType=${NetworkUtils.getNetworkType()}" + nextLine +
                        "isNetworkOnline=${NetworkUtils.isNetworkOnline()}" + nextLine +
                        "isConnected=${NetworkUtils.isConnected()}" + nextLine +
                        "isMobileEnabled=${NetworkUtils.isMobileEnabled()}" + nextLine +
                        "isMobile=${NetworkUtils.isMobile()}" + nextLine +
                        "isWifiConnected=${NetworkUtils.isWifiConnected()}" + nextLine +
                        "broadcastIpAddress=${NetworkUtils.broadcastIpAddress()}" + nextLine +
                        "gatewayByWifi=${NetworkUtils.gatewayByWifi()}" + nextLine +
                        "getIPAddress v4=${NetworkUtils.getIPAddress(true)}" + nextLine +
                        "getIPAddress v6=${NetworkUtils.getIPAddress(false)}" + nextLine +
                        "getMac=${NetworkUtils.getMac(this)}" + nextLine +
                        "getMacDefault=${NetworkUtils.getMacDefault(this)}" + nextLine +
                        "getMacFromHardware=${NetworkUtils.getMacFromHardware()}" + nextLine +
                        "getWifiRSSI=${NetworkUtils.getWifiRSSI()}" + nextLine +
                        "getWifiSSID=${NetworkUtils.getWifiSSID(this)}" + nextLine +
                        "netMaskByWifi=${NetworkUtils.netMaskByWifi()}" + nextLine +
                        "ipAddressByWifi=${NetworkUtils.ipAddressByWifi()}" + nextLine +
                        "networkOperatorName=${NetworkUtils.networkOperatorName()}" + nextLine +
                        "serverAddressByWifi=${NetworkUtils.serverAddressByWifi()}" + nextLine +

                        "isWifiEnabled=${NetworkUtils.isWifiEnabled()}" + nextLine +
                        "isWifi=${NetworkUtils.isWifi()}" + nextLine +
                        "is4G=${NetworkUtils.is4G()}" + nextLine +
                        "is5G=${NetworkUtils.is5G()}"
            }, 0)
        }))
    }

    @SuppressLint("SetTextI18n")
    override fun onNetChange(@NetState netMobile: Int) {
        when (netMobile) {
            NET_STATE_MOBILE -> {
                tvNetState.text = "移动网络 NET_STATE_MOBILE"
            }
            NET_STATE_WIFI   -> {
                tvNetState.text = "WIFI网络 NET_STATE_WIFI"
            }
            NET_STATE_NONE   -> {
                tvNetState.text = "无网络 NET_STATE_NONE"
            }
            else             -> {
            }
        }
    }

}