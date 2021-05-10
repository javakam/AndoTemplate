package ando.toolkit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import ando.toolkit.AppUtils.getContext
import ando.toolkit.ShellUtils.execCmd
import ando.toolkit.log.L
import java.io.IOException
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.net.*
import java.util.*

@Suppress("DEPRECATION")
object NetworkUtils {
    enum class NetworkType {
        /**
         * NETWORK_ETHERNET 以太网网络
         */
        NETWORK_ETHERNET, NETWORK_WIFI, NETWORK_5G, NETWORK_4G, NETWORK_3G, NETWORK_2G, NETWORK_UNKNOWN, NETWORK_NO
    }

    /**
     * Open the settings of wireless.
     */
    fun openWirelessSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    /**
     * Return whether network is connected.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: connected<br></br>`false`: disconnected
     */
    val isConnected: Boolean
        get() {
            val info = activeNetworkInfo
            return info != null && info.isConnected
        }

    /**
     * Return whether network is available.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isAvailable: Boolean
        get() = isAvailableByDns || isAvailableByPing("")

    /**
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * The default ping ip: 223.5.5.5
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isAvailableByPing: Boolean
        get() = isAvailableByPing("")

    /**
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param ip The ip address.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAvailableByPing(ip: String): Boolean {
        val realIp = if (TextUtils.isEmpty(ip)) "223.5.5.5" else ip
        val result = execCmd(String.format("ping -c 1 %s", realIp), false)
        return result.result == 0
    }

    /**
     * Return whether network is available using domain.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isAvailableByDns: Boolean
        get() = isAvailableByDns("")

    /**
     * Return whether network is available using domain.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain The name of domain.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isAvailableByDns(domain: String?): Boolean {
        val realDomain = if (domain.isNullOrBlank()) "www.baidu.com" else domain
        val inetAddress: InetAddress?
        try {
            inetAddress = InetAddress.getByName(realDomain)
            return inetAddress != null
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Return whether mobile data is enabled.
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    @get:SuppressLint("MissingPermission")
    val isMobileEnabled: Boolean
        get() {
            try {
                val tm =
                    getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.isDataEnabled
                }
                @SuppressLint("PrivateApi") val getMobileDataEnabledMethod =
                    tm.javaClass.getDeclaredMethod("getDataEnabled")
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            } catch (e: Exception) {
                Log.e("NetworkUtils", "getMobileDataEnabled: ", e)
            }
            return false
        }

    /**
     * Return whether using mobile data.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isMobile: Boolean
        get() {
            val info = activeNetworkInfo
            return (null != info && info.isAvailable
                    && info.type == ConnectivityManager.TYPE_MOBILE)
        }

    /**
     * Return whether using 4G.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun is4G(): Boolean {
        val info = activeNetworkInfo
        return (info != null && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
    }

    /**
     * Return whether using 4G.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun is5G(): Boolean {
        val info = activeNetworkInfo
        return (info != null && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_NR)
    }
    /**
     * Return whether wifi is enabled.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    /**
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @get:RequiresPermission(allOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE])
    var wifiEnabled: Boolean
        get() {
            @SuppressLint("WifiManagerLeak") val manager =
                getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.isWifiEnabled
        }
        set(enabled) {
            @SuppressLint("WifiManagerLeak") val manager =
                getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (enabled == manager.isWifiEnabled) {
                return
            }
            manager.isWifiEnabled = enabled
        }

    /**
     * Return whether wifi is connected.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: connected<br></br>`false`: disconnected
     */
    val isWifiConnected: Boolean
        @SuppressLint("MissingPermission")
        get() {
            val cm =
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
        }

    /**
     * Return whether wifi is available.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: available<br></br>`false`: unavailable
     */
    fun isWifiAvailable(): Boolean = wifiEnabled && isAvailable

    /**
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    fun networkOperatorName(): String =
        (getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName

    /**
     * Return type of network.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return type of network
     *
     *  * [NetworkUtils.NetworkType.NETWORK_ETHERNET]
     *  * [NetworkUtils.NetworkType.NETWORK_WIFI]
     *  * [NetworkUtils.NetworkType.NETWORK_4G]
     *  * [NetworkUtils.NetworkType.NETWORK_3G]
     *  * [NetworkUtils.NetworkType.NETWORK_2G]
     *  * [NetworkUtils.NetworkType.NETWORK_UNKNOWN]
     *  * [NetworkUtils.NetworkType.NETWORK_NO]
     *
     */
    fun getNetworkType(): NetworkType {
        if (isEthernet) {
            return NetworkType.NETWORK_ETHERNET // 以太网网络
        }
        val cm = getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @SuppressLint("MissingPermission")
        val info = cm.activeNetworkInfo ?: return NetworkType.NETWORK_NO // 没有任何网络
        if (!info.isConnected) {
            return NetworkType.NETWORK_NO // 网络断开或关闭
        }
        return if (info.isAvailable) {
            when (info.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    NetworkType.NETWORK_WIFI
                }
                ConnectivityManager.TYPE_MOBILE -> {
                    when (info.subtype) {
                        TelephonyManager.NETWORK_TYPE_GSM,
                        TelephonyManager.NETWORK_TYPE_GPRS,
                        TelephonyManager.NETWORK_TYPE_CDMA,
                        TelephonyManager.NETWORK_TYPE_EDGE,
                        TelephonyManager.NETWORK_TYPE_1xRTT,
                        TelephonyManager.NETWORK_TYPE_IDEN -> {
                            NetworkType.NETWORK_2G
                        }
                        TelephonyManager.NETWORK_TYPE_TD_SCDMA,
                        TelephonyManager.NETWORK_TYPE_EVDO_A,
                        TelephonyManager.NETWORK_TYPE_UMTS,
                        TelephonyManager.NETWORK_TYPE_EVDO_0,
                        TelephonyManager.NETWORK_TYPE_HSDPA,
                        TelephonyManager.NETWORK_TYPE_HSUPA,
                        TelephonyManager.NETWORK_TYPE_HSPA,
                        TelephonyManager.NETWORK_TYPE_EVDO_B,
                        TelephonyManager.NETWORK_TYPE_EHRPD,
                        TelephonyManager.NETWORK_TYPE_HSPAP -> {
                            NetworkType.NETWORK_3G
                        }
                        TelephonyManager.NETWORK_TYPE_IWLAN,
                        TelephonyManager.NETWORK_TYPE_LTE -> {
                            NetworkType.NETWORK_4G
                        }
                        TelephonyManager.NETWORK_TYPE_NR -> NetworkType.NETWORK_5G
                        else -> {
                            val subtypeName = info.subtypeName
                            if ("TD-SCDMA".equals(subtypeName, ignoreCase = true)
                                || "WCDMA".equals(subtypeName, ignoreCase = true)
                                || "CDMA2000".equals(subtypeName, ignoreCase = true)
                            ) {
                                NetworkType.NETWORK_3G
                            } else {
                                NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                }
                else -> {
                    NetworkType.NETWORK_UNKNOWN
                }
            }
        } else NetworkType.NETWORK_NO
    }

    /**
     * Return whether using ethernet.
     *
     * Must hold
     * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    private val isEthernet: Boolean
        @SuppressLint("MissingPermission")
        get() {
            val cm =
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
            val state = info.state ?: return false
            return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
        }

    private val activeNetworkInfo: NetworkInfo?
        @SuppressLint("MissingPermission")
        get() {
            val cm =
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

    private fun intToIp(ipAddress: Int): String {
        return (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
    }

    /**
     * @param useIPv4 true ipV4 ; false ipV6
     * @return ip address ; null no network
     */
    fun getIPAddress(useIPv4: Boolean): String? = try {
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val enumIpAddress = en.nextElement().inetAddresses
            while (enumIpAddress.hasMoreElements()) {
                val inetAddress = enumIpAddress.nextElement()
                if (!inetAddress.isLoopbackAddress) {
                    if (useIPv4) {
                        if (!inetAddress.isLinkLocalAddress) {
                            inetAddress.hostAddress
                        }
                    } else {
                        if (inetAddress is Inet6Address) {
                            inetAddress.getHostAddress()
                        }
                    }
                }
            }
        }
        null
    } catch (ex: Exception) {
        L.i("IP Address Ex ", ex.toString())
        null
    }

    //todo 2020年9月30日 14:23:59 测试该方法
    /**
     * Return the ip address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4 true to use ipv4, false otherwise.
     * @return the ip address , null 无网络连接
     */
    fun getIPAddress2(useIPv4: Boolean): String? {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) {
                    continue
                }
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress
                        }
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0) hostAddress.toUpperCase(Locale.getDefault()) else
                                hostAddress.substring(0, index).toUpperCase(Locale.getDefault())
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    val broadcastIpAddress: String
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                //val adds = LinkedList<InetAddress>()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (!ni.isUp || ni.isLoopback) {
                        continue
                    }
                    val ias = ni.interfaceAddresses
                    var i = 0
                    val size = ias.size
                    while (i < size) {
                        val ia = ias[i]
                        val broadcast = ia.broadcast
                        if (broadcast != null) {
                            return broadcast.hostAddress
                        }
                        i++
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ""
        }

    /**
     * Return the domain address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    fun getDomainAddress(domain: String?): String {
        val inetAddress: InetAddress
        return try {
            inetAddress = InetAddress.getByName(domain)
            inetAddress.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    val ipAddressByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress) ?: ""
        }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    val gatewayByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.gateway) ?: ""
        }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    val netMaskByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.netmask) ?: ""
        }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    val serverAddressByWifi: String
        get() {
            @SuppressLint("WifiManagerLeak") val wm =
                getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress) ?: ""
        }


    /**
     * 获取 WIFI 信号强度 RSSI
     */
    @SuppressLint("MissingPermission")
    fun getWifiRSSI(context: Context): Int {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.rssi
    }

    /**
     * 获取 WIFI 的 SSID
     */
    fun getWifiSSID(context: Context): String {
        val ssid = "unknown id"
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            val wifiManager =
                (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            @SuppressLint("MissingPermission") val info = wifiManager.connectionInfo
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                info.ssid
            } else {
                info.ssid.replace("\"", "")
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
            val connManager =
                (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            @SuppressLint("MissingPermission") val networkInfo = connManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                if (networkInfo.extraInfo != null) {
                    return networkInfo.extraInfo.replace("\"", "")
                }
            }
        }
        return ssid
    }

    fun isWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val info = connectivityManager.activeNetworkInfo
        return info != null && info.type == ConnectivityManager.TYPE_WIFI
    }

    /////////////////////////////////获取MAC地址//////////////////////////////////
    /**
     * 获取mac地址（适配所有Android版本）
     */
    fun getMac(context: Context?): String? {
        var mac: String? = ""
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware()
        }
        return mac
    }

    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     *
     * @param context * @return
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getMacDefault(context: Context?): String? {
        var mac = ""
        if (context == null) {
            return mac
        }
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var info: WifiInfo? = null
        try {
            info = wifi.connectionInfo
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (info == null) {
            return null
        }
        mac = info.macAddress
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH)
        }
        return mac
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     */
    fun getMacAddress(): String? {
        var macSerial: String? = null
        var str = ""
        try {
            val pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address")
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)
            while (str.isBlank()) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' } //去空格
                    break
                }
            }
        } catch (ex: IOException) {
            // 赋予默认值
            ex.printStackTrace()
        }
        return macSerial
    }

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     */
    fun getMacFromHardware(): String {
        try {
            val all = NetworkInterface.getNetworkInterfaces()
            while (all.hasMoreElements()) {
                val nif = all.nextElement()
                if (!nif.name.equals("wlan0", ignoreCase = true)) {
                    continue
                }
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (!TextUtils.isEmpty(res1)) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}