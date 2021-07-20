package ando.toolkit

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * # MD5Utils
 *
 * @author javakam
 * @date 2019/11/13  9:23
 */
object MD5Utils {

    fun generateKey(vararg args: String?): String =
        args.let {
            val count: Int = args.size
            val format = ""
            for (i in 0..count) {
                format.plus("%s")
            }
            encrypt32(String.format(Locale.getDefault(), format, args))
        }

    /**
     * 32位
     *
     * @param isUpperCase 默认小写(false)
     */
    fun encrypt32(text: String, isUpperCase: Boolean = false): String {
        var reMd5 = String()
        try {
            val md: MessageDigest = MessageDigest.getInstance("MD5")
            md.update(text.toByteArray())
            val b: ByteArray = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in b.indices) {
                i = b[offset].toInt()
                if (i < 0) i += 256
                if (i < 16) buf.append("0")
                buf.append(Integer.toHexString(i))
            }
            reMd5 = buf.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return if (isUpperCase) reMd5.uppercase(Locale.getDefault()) else reMd5.lowercase(Locale.getDefault())
    }

    /**
     * 16位
     *
     * @param isUpperCase 默认小写(false)
     */
    fun encrypt16(text: String, isUpperCase: Boolean = false): String {
        return encrypt32(text, isUpperCase).substring(8, 24)
    }
}