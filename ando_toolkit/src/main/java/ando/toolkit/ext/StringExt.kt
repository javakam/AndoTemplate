package ando.toolkit.ext

import ando.toolkit.ClipboardUtils
import java.util.*

/**
 * # String Extension
 *
 * @author javakam
 * @date 2020/10/29  10:07
 */

fun String?.noNull(default: String? = ""): String {
    return if (isNullOrBlank()) default ?: "" else this
}

fun CharSequence?.noNullZero(): CharSequence {
    return if (this.isNullOrBlank()) "0" else this
}

fun CharSequence?.noNullZeroInt(): Int {
    return if (this.isNullOrBlank()) 0 else this.toString().toInt()
}

/**
 * 可以在应用内播放的地址
 */
fun String?.isVideoUrl(): Boolean {
    return (this.noNull()).toLowerCase(Locale.getDefault()).run {
        endsWith(".m3u8")
                || endsWith(".mp4")
                || endsWith(".mkv")
                || endsWith(".flv")
                || endsWith(".avi")
                || endsWith(".rm")
                || endsWith(".rmvb")
                || endsWith(".wmv")
    }
}

fun String.copyToClipBoard() = ClipboardUtils.copyText(this)

/**
 * 字符串转换unicode
 */
fun String.stringToUnicode(): String {
    val unicode = StringBuffer()
    for (element in this) {
        unicode.append("\\u" + Integer.toHexString(element.toInt())) // 转换为unicode
    }
    return unicode.toString()
}

/**
 * unicode 转字符串
 */
fun String.unicodeToString(): String {
    val string = StringBuffer()
    val hex = this.split("\\\\u".toRegex()).toTypedArray()
    for (i in 1 until hex.size) {
        val data = hex[i].toInt(16) // 转换出每一个代码点
        string.append(data.toChar()) // 追加成string
    }
    return string.toString()
}