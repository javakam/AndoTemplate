package ando.toolkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

/**
 * 获取设备唯一识别码
 */
object DeviceIdUtils {

    /**
     * 支持 Android 10 -> Manifest.permission.READ_PHONE_STATE
     * <p>
     * https://juejin.im/post/5cad5b7ce51d456e5a0728b0
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getUUID(): String {
        var serial: String?

        @Suppress("DEPRECATION")
        val szDevIDShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10
        +Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10
        +Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10
        +Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10
        +Build.USER.length % 10 //13 位

        try {
            @Suppress("DEPRECATION")
            serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial()
            else Build.SERIAL
            //API>=9 使用serial号
            return UUID(
                szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()
            ).toString()
        } catch (exception: Exception) {
            //serial需要一个初始化
            serial = "serial" // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return UUID(szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    /**
     * 获得设备硬件标识
     * <pre>
     * 注: 需要权限  Manifest.permission.READ_PHONE_STATE
    </pre> *
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    fun getDeviceId(context: Context): String? {
        val sbDeviceId = StringBuilder()
        //获得AndroidId（无需权限）
        val androidId = getAndroidId(context)
        //获得设备序列号（无需权限）
        val serial = getSERIAL()
        //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
        val uuid = getDeviceUUID().replace("-", "")

        //追加 IMEI
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //获得设备默认 IMEI（>=6.0 需要ReadPhoneState权限）
            @SuppressLint("MissingPermission") val imei = getIMEI(context)
            if (imei.isNotEmpty()) {
                sbDeviceId.append(imei)
                sbDeviceId.append("|")
            }
        }
        //追加 androidId
        if (androidId.isNotEmpty()) {
            sbDeviceId.append(androidId)
            sbDeviceId.append("|")
        }
        //追加serial
        if (serial.isNotEmpty()) {
            sbDeviceId.append(serial)
            sbDeviceId.append("|")
        }
        //追加硬件uuid
        if (uuid.isNotEmpty()) {
            sbDeviceId.append(uuid)
        }

        //生成SHA1，统一DeviceId长度
        if (sbDeviceId.isNotEmpty()) {
            try {
                val hash = getHashByString(sbDeviceId.toString())
                val sha1 = bytesToHex(hash)
                //返回最终的 DeviceId
                if (sha1.isNotEmpty()) return sha1
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        //如果以上硬件标识数据均无法获得，
        //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
        return UUID.randomUUID().toString().replace("-", "")
    }

    //需要获得READ_PHONE_STATE权限，>=6.0，默认返回null
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getIMEI(context: Context): String =
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            @Suppress("DEPRECATION")
            tm.deviceId
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidId(context: Context): String =
        try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    @SuppressLint("HardwareIds")
    private fun getSERIAL(): String =
        try {
            @Suppress("DEPRECATION")
            Build.SERIAL
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    @SuppressLint("HardwareIds")
    private fun getDeviceUUID(): String = try {
        val dev =
            "3883756" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.DEVICE.length % 10
        +Build.HARDWARE.length % 10 + Build.ID.length % 10 + Build.MODEL.length % 10
        @Suppress("DEPRECATION")
        +Build.PRODUCT.length % 10 + Build.SERIAL.length % 10
        @Suppress("DEPRECATION")
        UUID(dev.hashCode().toLong(), Build.SERIAL.hashCode().toLong()).toString()
    } catch (ex: Exception) {
        ex.printStackTrace()
        ""
    }

    /**
     * 取SHA1
     *
     * @param data 数据
     * @return 对应的hash值
     */
    private fun getHashByString(data: String): ByteArray {
        return try {
            val messageDigest = MessageDigest.getInstance("SHA1")
            messageDigest.reset()
            messageDigest.update(data.toByteArray(StandardCharsets.UTF_8))
            messageDigest.digest()
        } catch (e: Exception) {
            "".toByteArray()
        }
    }

    /**
     * 转16进制字符串
     *
     * @param data 数据
     * @return 16进制字符串
     */
    private fun bytesToHex(data: ByteArray): String {
        val sb = java.lang.StringBuilder()
        var stmp: String
        for (n in data.indices) {
            stmp = Integer.toHexString((data[n] and 0xFF.toByte()).toInt())
            if (stmp.length == 1) sb.append("0")
            sb.append(stmp)
        }
        return sb.toString().toUpperCase(Locale.CHINA)
    }
}