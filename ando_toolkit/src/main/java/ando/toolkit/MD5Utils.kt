package ando.toolkit

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

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
            md5Decode32(String.format(Locale.getDefault(), format, args))
        }

    /**
     * 32位MD5加密
     *
     * @param content 待加密内容
     */
    fun md5Decode32(content: String): String {
        val hash: ByteArray = try {
            MessageDigest.getInstance("MD5").digest(content.toByteArray(StandardCharsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("NoSuchAlgorithmException", e)
        }
        //对生成的16字节数组进行补零操作
        val hex = StringBuilder(hash.size * 2)
        for (b: Byte in hash) {
            if (b and 0xFF.toByte() < 0x10) {
                hex.append("0")
            }
            hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
        }
        return hex.toString()
    }

    /**
     * 16位MD5加密
     * 实际是截取的32位加密结果的中间部分(8-24位)
     */
    fun md5Decode16(content: String): String = md5Decode32(content).substring(8, 24)
}