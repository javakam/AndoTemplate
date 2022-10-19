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

//相较于 noNull , 处理了返回值为字符串"null"的问题
fun String?.noNullStrict(default: String? = ""): String {
    return if (isNullOrBlank()) default ?: "" else (if (trim().equals("null", ignoreCase = true)) "" else this)
}

fun CharSequence?.noNullZero(): CharSequence {
    return if (this.isNullOrBlank()) "0" else this
}

fun CharSequence?.noNullZeroInt(): Int {
    return if (this.isNullOrBlank()) 0 else this.toString().toIntOrNull() ?: 0
}

fun CharSequence?.noNullZeroLong(): Long {
    return if (this.isNullOrBlank()) 0L else this.toString().toLongOrNull() ?: 0L
}

/**
 * 分隔符
 * @param delimiter 分隔符, 默认是英文逗号
 * @param place 集合为空时, 返回的默认字符串
 */
fun Collection<Any>?.split(delimiter: String = ",", place: String = ""): String {
    if (isNullOrEmpty()) return place
    val sb = StringBuilder()
    forEach {
        sb.append(it).append(delimiter)
    }
    return if (sb.isNotBlank()) sb.substring(0, sb.length - 1) else ""
}

/**
 * 字符串加入空白
 *
 * 中文空格(\u3000 和 &#12288; 效果相同)
 * 1. \u3000 用于代码中动态添加中文空格
 * 2. &#12288; 用于 string.xml
 * 3. https://juejin.cn/post/7128302603431051278/
 *
 * @param text 字符串
 * @param appendIndex 拼接空白的起始位置, 尾部追加时为 text.length-1
 * @param spaceCount 需要拼接的空白数量
 * @param isBeforeHead 仅在头部前面追加空白时为 true , 此时 appendIndex 参数没用到
 */
fun appendSpace(text: String?, appendIndex: Int = 0, spaceCount: Int = 0, isBeforeHead: Boolean = false): String {
    var s: String? = text
    if (s == null) s = ""
    val space = String.format(Locale.getDefault(), format = "%s", "\u3000")
    var isCenterMode = false
    var isCenterSpace = ""
    var i = 0
    while (i < spaceCount) {
        if (isBeforeHead || text.isNullOrBlank()) {//Head
            s = (space + s)
        } else if (appendIndex == (text.length - 1)) {//End
            s += space
        } else {//Center
            if (appendIndex < 0 || appendIndex > text.length - 1) {
                //注: 当索引位置越界,按照尾部追加处理,没有避免抛出异常,可以根据需求改动
                s += space
            } else {
                isCenterMode = true
                isCenterSpace += space
            }
        }
        i += 1
    }
    //字符串中间加入空白
    if (isCenterMode) {
        val realIndex = (appendIndex + 1)
        val head = s?.substring(0, realIndex)
        val end = s?.substring(realIndex, s.length)
        s = "$head$isCenterSpace$end"
    }
    return s ?: ""
}

//默认尾部追加, i 为需要拼接空白的数量
fun String?.space(i: Int = 0, appendIndex: Int = (this?.length ?: 0), isBeforeHead: Boolean = false) =
    appendSpace(text = this, appendIndex = appendIndex, spaceCount = i, isBeforeHead = isBeforeHead)

fun String?.spaceHead(i: Int = 0) = appendSpace(text = this, appendIndex = 0, spaceCount = i, isBeforeHead = true)

fun String?.spaceCenter(i: Int = 0, appendIndex: Int) = appendSpace(text = this, appendIndex = appendIndex, spaceCount = i, isBeforeHead = false)

fun String?.spaceEnd(i: Int = 0) = appendSpace(text = this, appendIndex = (this?.length ?: 0), spaceCount = i, isBeforeHead = false)

/**
 * 可以在应用内播放的地址
 */
fun String?.isVideoUrl(): Boolean {
    return (this.noNull()).lowercase(Locale.getDefault()).run {
        endsWith(".m3u8") || endsWith(".mp4") || endsWith(".mov") || endsWith(".mkv") || endsWith(".flv") || endsWith(".avi") || endsWith(".rm") || endsWith(
            ".rmvb"
        ) || endsWith(".wmv")
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