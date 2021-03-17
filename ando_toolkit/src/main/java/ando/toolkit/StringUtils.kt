package ando.toolkit

/**
 * # StringUtils
 *
 * @author javakam
 * @date 2021/3/3 9:26
 */
object StringUtils {

    /**
     * 字符串转换unicode
     */
    fun stringToUnicode(string: String): String {
        val unicode = StringBuffer()
        for (element in string) {
            unicode.append("\\u" + Integer.toHexString(element.toInt())) // 转换为unicode
        }
        return unicode.toString()
    }

    /**
     * unicode 转字符串
     */
    fun unicodeToString(unicode: String): String {
        val string = StringBuffer()
        val hex = unicode.split("\\\\u".toRegex()).toTypedArray()
        for (i in 1 until hex.size) {
            val data = hex[i].toInt(16) // 转换出每一个代码点
            string.append(data.toChar()) // 追加成string
        }
        return string.toString()
    }

}