package ando.toolkit

import ando.toolkit.ext.noNull
import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.annotation.StringDef
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

/**
 * # TimeUtils 时间工具类
 *
 * @author javakam
 * @date 2019/12/11 10:09
 */
object TimeUtils {

    const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT1 = "yyyy-MM-dd HH:mm"
    const val DATE_FORMAT2 = "yyyy-MM-dd"
    const val DATE_FORMAT3 = "yyyy.MM.dd"
    const val DATE_FORMAT4 = "yyyy年MM月dd日 HH:mm:ss"
    const val DATE_FORMAT5 = "yyyy年MM月dd日 HH:mm"
    const val DATE_FORMAT6 = "yyyy年M月dd日"
    const val DATE_FORMAT7 = "yyyyMMddHHmmss"

    private fun sdf(@Format format: String): SimpleDateFormat {
        var time: String? = format
        if (time.isNullOrBlank()) time = DATE_FORMAT
        return SimpleDateFormat(time, Locale.getDefault())
    }

    private fun dateToString(date: Date?, @Format format: String = DATE_FORMAT): String =
        sdf(format).format(date ?: Date())

    /**
     * 获取今天的时间
     *
     * @return int[] 三个元素: 年,月,日
     */
    fun getCurrentDateTime(): IntArray {
        val now = Calendar.getInstance()
        return intArrayOf(
            now[Calendar.YEAR],
            now[Calendar.MONTH] + 1,
            now[Calendar.DAY_OF_MONTH]
        )
    }

    /**
     * 获取现在时间 yyyy-MM-dd HH:mm:ss
     */
    fun getCurrentDateTimeDate(): Date? {
        val formatter = sdf(DATE_FORMAT)
        return formatter.parse(formatter.format(Date()), ParsePosition(8))
    }

    /**
     * 获取现在星期几
     */
    fun getCurrentWeek(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return calendar[Calendar.DAY_OF_WEEK] - 1
    }

    /**
     * 把一个毫秒数转化成时间字符串。格式为小时/分/秒/毫秒（如：24903600 --> 06小时55分03秒600毫秒）。
     *
     * @param millis   要转化的毫秒数。
     * @param isWhole  是否强制全部显示小时/分/秒/毫秒。
     * @param isFormat 时间数字是否要格式化，如果true：少位数前面补全；如果false：少位数前面不补全。
     * @return 返回时间字符串：小时/分/秒/毫秒的格式（如：24903600 --> 06小时55分03秒600毫秒）。
     */
    fun millisToString(millis: Long, isWhole: Boolean, isFormat: Boolean): String {
        var h = ""
        var m = ""
        var s = ""
        var mi = ""
        if (isWhole) {
            h = if (isFormat) "00小时" else "0小时"
            m = if (isFormat) "00分" else "0分"
            s = if (isFormat) "00秒" else "0秒"
            mi = if (isFormat) "00毫秒" else "0毫秒"
        }
        var temp: Long = millis
        val hper: Long = 60 * 60 * 1000.toLong()
        val mper: Long = 60 * 1000.toLong()
        val sper: Long = 1000
        if (temp / hper > 0) {
            h = if (isFormat) {
                if (temp / hper < 10) "0" + temp / hper else "${temp / hper}"
            } else {
                "${temp / hper}"
            }
            h += "小时"
        }
        temp %= hper
        if (temp / mper > 0) {
            m = if (isFormat) {
                if (temp / mper < 10) "0" + temp / mper else "${temp / mper}"
            } else {
                "${temp / mper}"
            }
            m += "分"
        }
        temp %= mper
        if (temp / sper > 0) {
            s = if (isFormat) {
                if (temp / sper < 10) "0" + temp / sper else "${temp / sper}"
            } else {
                "${temp / sper}"
            }
            s += "秒"
        }
        temp %= sper
        if (!isWhole) {
            mi = temp.toString() + ""
        }
        if (isFormat) {
            if (temp in 10..99) {
                mi = "0$temp"
            }
            if (temp < 10) {
                mi = "00$temp"
            }
        }
        mi += "毫秒"
        return h + m + s + mi
    }

    /**
     * 把一个毫秒数转化成时间字符串。格式为小时/分/秒/毫秒（如：24903600 --> 06小时55分03秒）。
     *
     * @param millis   要转化的毫秒数。
     * @param isWhole  是否强制全部显示小时/分/秒/毫秒。
     * @param isFormat 时间数字是否要格式化，如果true：少位数前面补全；如果false：少位数前面不补全。
     * @return 返回时间字符串：小时/分/秒/毫秒的格式（如：24903600 --> 06小时55分03秒）。
     */
    fun millisToStringMiddle(
        millis: Long,
        isWhole: Boolean,
        isFormat: Boolean,
        hUnit: String = "小时",
        mUnit: String = "分钟",
        sUnit: String = "秒"
    ): String {
        var h = ""
        var m = ""
        var s = ""
        if (isWhole) {
            h = if (isFormat) "00$hUnit" else "0$hUnit"
            m = if (isFormat) "00$mUnit" else "0$mUnit"
            s = if (isFormat) "00$sUnit" else "0$sUnit"
        }
        var temp = millis
        val hper = 60 * 60 * 1000.toLong()
        val mper = 60 * 1000.toLong()
        val sper: Long = 1000
        if (temp / hper > 0) {
            h = if (isFormat) {
                if (temp / hper < 10) "0" + temp / hper else "${temp / hper}"
            } else {
                "${temp / hper}"
            }
            h += hUnit
        }
        temp %= hper
        if (temp / mper > 0) {
            m = if (isFormat) {
                if (temp / mper < 10) "0" + temp / mper else "${temp / mper}"
            } else {
                "${temp / mper}"
            }
            m += mUnit
        }
        temp %= mper
        if (temp / sper > 0) {
            s = if (isFormat) {
                if (temp / sper < 10) "0" + temp / sper else "${temp / sper}"
            } else {
                "${temp / sper}"
            }
            s += sUnit
        }
        return h + m + s
    }

    /**
     * 把一个毫秒数转化成时间字符串。格式为小时/分/秒/毫秒（如：24903600 --> 06小时55分钟）。
     *
     * @param millis   要转化的毫秒数。
     * @param isWhole  是否强制全部显示小时/分。
     * @param isFormat 时间数字是否要格式化，如果true：少位数前面补全；如果false：少位数前面不补全。
     * @return 返回时间字符串：小时/分/秒/毫秒的格式（如：24903600 --> 06小时55分钟）。
     */
    fun millisToStringShort(millis: Long, isWhole: Boolean, isFormat: Boolean): String {
        var h = ""
        var m = ""
        if (isWhole) {
            h = if (isFormat) "00小时" else "0小时"
            m = if (isFormat) "00分钟" else "0分钟"
        }
        var temp = millis
        val hper = 60 * 60 * 1000.toLong()
        val mper = 60 * 1000.toLong()
        if (temp / hper > 0) {
            h = if (isFormat) {
                if (temp / hper < 10) "0" + temp / hper else "${temp / hper}"
            } else {
                "${temp / hper}"
            }
            h += "小时"
        }
        temp %= hper
        if (temp / mper > 0) {
            m = if (isFormat) {
                if (temp / mper < 10) "0" + temp / mper else "${temp / mper}"
            } else {
                "${temp / mper}"
            }
            m += "分钟"
        }
        return h + m
    }

    /**
     * 把日期毫秒转化为字符串。默认格式：yyyy-MM-dd HH:mm:ss
     *
     * @param millis 要转化的日期毫秒数。
     * @return 返回日期字符串（如："2013-02-19 11:48:31"）。
     */
    fun millisToStringDate(millis: Long): String = millisToStringDate(millis, DATE_FORMAT)

    /**
     * 把日期毫秒转化为字符串。
     *
     * @param millis  要转化的日期毫秒数。
     * @param pattern 要转化为的字符串格式（如：yyyy-MM-dd HH:mm:ss）。
     * @return 返回日期字符串。
     */
    @SuppressLint("SimpleDateFormat")
    fun millisToStringDate(millis: Long, pattern: String?): String =
        SimpleDateFormat(pattern).format(Date(millis))

    /**
     * 把日期毫秒转化为字符串（文件名）。
     *
     * @param millis  要转化的日期毫秒数。
     * @param pattern 要转化为的字符串格式（如：yyyy-MM-dd HH:mm:ss）。
     * @return 返回日期字符串（yyyy_MM_dd_HH_mm_ss）。
     */
    fun millisToStringFilename(millis: Long, pattern: String?): String {
        val dateStr = millisToStringDate(millis, pattern)
        return dateStr.replace("[- :]".toRegex(), "_")
    }

    /**
     * 把日期毫秒转化为字符串（文件名）。
     *
     * @param millis 要转化的日期毫秒数。
     * @return 返回日期字符串（yyyy-MM-dd HH:mm:ss）。
     */
    fun millisToStringFilename(millis: Long): String {
        val dateStr = millisToStringDate(millis, DATE_FORMAT)
        return dateStr.replace("[- :]".toRegex(), "_")
    }

    private const val oneHourMillis = 60 * 60 * 1000 // 一小时的毫秒数
    private const val oneDayMillis = 24 * oneHourMillis // 一天的毫秒数
    private const val oneYearMillis = 365 * oneDayMillis // 一年的毫秒数

    fun millisToLifeStringPHP2(millis: String?): String {
        val millisLong = if (millis.isNullOrBlank()) 0 else millis.toLong()
        return if (millisLong <= 0) "" else millisToLifeStringPHP2(millisLong)
    }

    fun millisToLifeStringPHP2(millisLong: Long): String {
        var millis = millisLong
        if (millis <= 0) {
            return ""
        }
        millis *= 1000 //PHP时间要 * 1000
        val now = System.currentTimeMillis()
        val todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd")
        if (now - millis in 1..oneHourMillis) { // 一小时内
            val m = millisToStringShort(now - millis, isWhole = false, isFormat = false)
            return if ("" == m) "1分钟内" else m + "前"
        }
        if (millis >= todayStart && millis <= oneDayMillis + todayStart) { // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
            return "今天 " + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart - oneDayMillis) { // 大于（今天开始值减一天，即昨天开始值）
            return "昨天"
        }
        if (millis > todayStart - oneDayMillis * 2) {
            return "前天"
        }
        val diff = (todayStart - millis).toDouble() / oneDayMillis
        val diffDay = ceil(diff).toInt()
        //L.i("TimeUtils diffDay= " + diff + "   " + diffDay);
        if (diffDay <= 10) { // 大于 10 天显示具体时间
            //return diffDay + "天前 "+ millisToStringDate(millisLong, "yyyy年MM月dd日 HH:mm");//用于调试
            return diffDay.toString() + "天前 "
        }
        //?不准?
//        if (millisLong >= todayStart - oneDayMillis * 4 && millisLong <= todayStart - oneDayMillis * 10) { // 大于 10 天显示具体时间
//            return diffDay + "天前";
//        }
        val thisYearStart = string2Millis(millisToStringDate(now, "yyyy"), "yyyy")
        return if (millis > thisYearStart) { // 大于今天小于今年
            millisToStringDate(millis, "yyyy年MM月dd日")
        } else millisToStringDate(millis, "yyyy年MM月dd日")
    }

    /**
     * millis "1578455259"
     */
    fun millisToLifeStringPHP(millis: String?): String {
        val millisLong = if (millis.isNullOrBlank()) 0 else millis.toLong()
        return millisToLifeStringPHP(millisLong)
    }

    /**
     * Fixed : PHP 返回的时间戳和Java的不一致导致时间为 1970 的问题
     * <pre>
     * 1563423720L    System.currentTimeMillis() -> 1574392167822
    </pre> *
     * 时间格式：
     * 1小时内用，多少分钟前；
     * 超过1小时，显示时间而无日期；
     * 如果是昨天，则显示昨天
     * 超过昨天再显示日期；
     * 超过1年再显示年。
     */
    fun millisToLifeStringPHP(millis: Long): String =
        if (millis <= 0) "" else millisToLifeString(millis * 1000)

    /**
     * 时间格式：
     * 1小时内用，多少分钟前；
     * 超过1小时，显示时间而无日期；
     * 如果是昨天，则显示昨天
     * 超过昨天再显示日期；
     * 超过1年再显示年。
     */
    fun millisToLifeString(millis: Long): String {
        val now = System.currentTimeMillis()
        val todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd")
        if (now - millis in 1..oneHourMillis) { // 一小时内
            val m = millisToStringShort(now - millis, isWhole = false, isFormat = false)
            return if ("" == m) "1分钟内" else m + "前"
        }
        if (millis >= todayStart && millis <= oneDayMillis + todayStart) { // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
            return "今天 " + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart - oneDayMillis) { // 大于（今天开始值减一天，即昨天开始值）
            return "昨天 " + millisToStringDate(millis, "HH:mm")
        }
        val thisYearStart = string2Millis(millisToStringDate(now, "yyyy"), "yyyy")
        return if (millis > thisYearStart) { // 大于今天小于今年
            millisToStringDate(millis, "MM月dd日 HH:mm")
        } else millisToStringDate(
            millis,
            "yyyy年MM月dd日 HH:mm"
        )
    }

    /**
     * 时间格式：
     * 今天，显示时间而无日期；
     * 如果是昨天，则显示昨天
     * 超过昨天再显示日期；
     * 超过1年再显示年。
     */
    fun millisToLifeString2(millis: Long): String {
        val now = System.currentTimeMillis()
        val todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd")
        if (millis > todayStart + oneDayMillis && millis < todayStart + 2 * oneDayMillis) { // 明天
            return "明天" + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart + 2 * oneDayMillis && millis < todayStart + 3 * oneDayMillis) { // 后天
            return "后天" + millisToStringDate(millis, "HH:mm")
        }
        if (millis >= todayStart && millis <= oneDayMillis + todayStart) { // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
            return "今天 " + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart - oneDayMillis && millis < todayStart) { // 大于（今天开始值减一天，即昨天开始值）
            return "昨天 " + millisToStringDate(millis, "HH:mm")
        }
        val thisYearStart = string2Millis(millisToStringDate(now, "yyyy"), "yyyy")
        return if (millis > thisYearStart) { // 大于今天小于今年
            millisToStringDate(millis, "MM月dd日 HH:mm")
        } else millisToStringDate(
            millis,
            "yyyy年MM月dd日 HH:mm"
        )
    }

    /**
     * 时间格式：
     * 今天，显示时间而无日期；
     * 如果是昨天，则显示昨天
     * 超过昨天再显示日期；
     * 超过1年再显示年。
     */
    fun millisToLifeString3(millis: Long): String {
        val now = System.currentTimeMillis()
        val todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd")
        if (millis > todayStart + oneDayMillis && millis < todayStart + 2 * oneDayMillis) { // 明天
            return "明天"
        }
        if (millis > todayStart + 2 * oneDayMillis && millis < todayStart + 3 * oneDayMillis) { // 后天
            return "后天"
        }
        if (millis >= todayStart && millis <= oneDayMillis + todayStart) { // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
            return millisToStringDate(millis, "HH:mm")
        }
        return if (millis > todayStart - oneDayMillis && millis < todayStart) { // 大于（今天开始值减一天，即昨天开始值）
            "昨天 "
        } else millisToStringDate(
            millis,
            "MM月dd日"
        )
    }

    /**
     * 时间格式：
     * 今天，显示时间而无日期；
     * 如果是昨天，则显示昨天
     * 超过昨天再显示日期；
     * 超过1年再显示年。
     */
    fun millisToLifeString4(millis: Long): String {
        val now = System.currentTimeMillis()
        val todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd")
        if (millis > todayStart + oneDayMillis && millis < todayStart + 2 * oneDayMillis) { // 明天
            return "明天" + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart + 2 * oneDayMillis && millis < todayStart + 3 * oneDayMillis) { // 后天
            return "后天" + millisToStringDate(millis, "HH:mm")
        }
        if (millis >= todayStart && millis <= oneDayMillis + todayStart) { // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
            return "今天 " + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart - oneDayMillis && millis < todayStart) { // 大于（今天开始值减一天，即昨天开始值）
            return "昨天 " + millisToStringDate(millis, "HH:mm")
        }
        val thisYearStart = string2Millis(millisToStringDate(now, "yyyy"), "yyyy")
        return if (millis > thisYearStart) { // 大于今天小于今年
            millisToStringDate(millis, "MM月dd日 HH:mm")
        } else millisToStringDate(millis, "yyyy-MM-dd HH:mm")
    }

    /**
     * 字符串解析成毫秒数
     */
    fun string2Millis(str: String?, pattern: String?): Long {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        var millis: Long = 0
        try {
            @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            millis = format.parse(str.noNull()).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return millis
    }

    /**
     * 获得今天开始的毫秒值
     */
    fun getTodayStartMillis(): Long = getOneDayStartMillis(System.currentTimeMillis())

    private fun getOneDayStartMillis(millis: Long): Long {
        val dateStr = millisToStringDate(millis, "yyyy-MM-dd")
        return string2Millis(dateStr, "yyyy-MM-dd")
    }

    /**
     * 字符串转日期
     *
     * yyyy-MM-dd HH:mm:ss  -> Date 对象
     */
    fun strToDate(str: String?): Date? =
        str?.let {
            try {
                val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                dateFormat.parse(str)
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
        }

    /**
     * 获取现在时间
     *
     * @return返回短时间格式 yyyy-MM-dd
     */
    fun getNowDateyyyyMMdd(): Date? {
        val currentTime = Date()
        val formatter = SimpleDateFormat(DATE_FORMAT2, Locale.getDefault())
        val dateString = formatter.format(currentTime)
        return formatter.parse(dateString, ParsePosition(8))
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    fun getDateSecondStr(): String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())

    /**
     * 获取当前时间
     *
     * @return 返回格式 yyyy-MM-dd HH:mm
     */
    fun getDateMinuteStr(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

    /**
     * 获取当前时间 -- 今天的年月日
     *
     * @return 返回格式 yyyy-MM-dd
     */
    fun getStringDateyyyy_MM_dd(): String {
        val currentTime = Date()
        val formatter = SimpleDateFormat(DATE_FORMAT2, Locale.getDefault())
        return formatter.format(currentTime)
    }

    /**
     * 获取当前时间
     *
     * @return 返回格式 yyyy.MM.dd
     */
    fun getStringDateyyyyMMdd(): String {
        val formatter = sdf(DATE_FORMAT3)
        return formatter.format(Date())
    }

    /**
     * 获取前月的第一天
     *
     * @return yyyy.MM.dd
     */
    fun getFirstDayOfThisMonth(): String {
        val cale = Calendar.getInstance()
        cale.add(Calendar.MONTH, 0)
        cale[Calendar.DAY_OF_MONTH] = 1
        return sdf(DATE_FORMAT3).format(cale.time)
    }

    /**
     * 获取前月的最后一天
     *
     * @return yyyy.MM.dd
     */
    fun getLastDayOfThisMonth(): String {
        val cale = Calendar.getInstance()
        cale.add(Calendar.MONTH, 1)
        cale[Calendar.DAY_OF_MONTH] = 0
        return sdf(DATE_FORMAT3).format(cale.time)
    }

    /**
     * 获取今年第一天日期
     *
     * @return String 日期格式：今年第一天 2018-01-01
     */
    fun getCurrYearFirst(): String {
        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, 0)
        c[Calendar.DAY_OF_YEAR] = 1 //设置为1号,当前日期既为本年第一天
        return dateToString(c.time)
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份  2018
     * @return Date
     */
    fun getCurrYearFirst(year: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar[Calendar.YEAR] = year
        return calendar.time
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份  2018
     * @return String
     */
    fun getCurrYearFirstStr(year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar[Calendar.YEAR] = year
        return dateToString(calendar.time)
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份  2018
     * @return Date
     */
    fun getCurrYearLast(year: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar[Calendar.YEAR] = year
        calendar.roll(Calendar.DAY_OF_YEAR, -1)
        return calendar.time
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份  2018
     * @return String
     */
    fun getCurrYearLastStr(year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar[Calendar.YEAR] = year
        calendar.roll(Calendar.DAY_OF_YEAR, -1)
        return dateToString(calendar.time)
    }

    /**
     * 两时间比较大小
     *
     * @param setDate 日期参数格式 yyyy-MM-dd
     * @param nowDate 日期参数格式 yyyy-MM-dd
     * @return true setDate <= nowDate
     */
    fun compared(setDate: String, nowDate: String): Boolean {
        val dateFormat = sdf(DATE_FORMAT2)
        try {
            val date1 = dateFormat.parse(setDate)
            val date2 = dateFormat.parse(nowDate)
            if (date1 == null || date2 == null) return false
            return date1.time <= date2.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 两时间比较大小
     *
     * @param setDate 日期参数格式 yyyy-MM-dd HH:mm
     * @param nowDate 日期参数格式 yyyy-MM-dd HH:mm
     * @return true setDate <= nowDate and default
     */
    fun compared2(setDate: String, nowDate: String): Boolean {
        val dateFormat = sdf(DATE_FORMAT1)
        try {
            val date1 = dateFormat.parse(setDate)
            val date2 = dateFormat.parse(nowDate)
            if (date1 == null || date2 == null) {
                return false
            }
            return date1.time <= date2.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 两时间比较大小
     *
     * @param setDate 日期参数格式 Date
     * @param nowDate 日期参数格式 Date
     * @return true setDate <= nowDate and default
     */
    fun compared2(setDate: Date, nowDate: Date): Boolean {
        return setDate.time <= nowDate.time
    }

    /**
     * 将日期格式转为毫秒数
     *
     * @param in 格式为 2014-09-30 09:50
     * @return 返回格式为 1345185923140
     */
    fun dateToLong(@Format format: String): Long {
        try {
            val date = sdf(format).parse(format) ?: return 0
            val cal = Calendar.getInstance()
            cal.time = date
            return cal.timeInMillis
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 将毫秒数转为日期
     *
     * @param millis 格式为1345185923140L
     * @return 返回格式为 年-月-日 时：分：秒
     */
    fun longToDate(millis: Long, @Format template: String = DATE_FORMAT): String {
        val gc = Calendar.getInstance()
        gc.time = Date(millis)
        return sdf(template).format(gc.time)
    }

    /**
     * 将2015-10-18-16-47-30格式时间转换为 2015年10月18日 16:47
     *
     * @param dateTime
     * @return
     */
    fun transTime1(dateTime: String): String {
        return if (!TextUtils.isEmpty(dateTime)) {
            val str = dateTime.split("-").toTypedArray()
            val sb = StringBuilder()
            sb.append(str[0])
                .append("年")
                .append(str[1])
                .append("月")
                .append(str[2])
                .append("日  ")
                .append(str[3])
                .append(":")
                .append(str[4])
            sb.toString()
        } else ""
    }

    /**
     * 将2015-10-18-16-47-30格式时间转换为 2015-10-18 16:47
     *
     * @param dateTime
     * @return
     */
    fun transTime2(dateTime: String): String =
        if (!TextUtils.isEmpty(dateTime)) {
            val str = dateTime.split("-").toTypedArray()
            val sb = StringBuilder()
            sb.append(str[0])
                .append("-")
                .append(str[1])
                .append("-")
                .append(str[2])
                .append("   ")
                .append(str[3])
                .append(":")
                .append(str[4])
            sb.toString()
        } else ""

    /**
     * 将2015-10-18-16-47-30格式时间转换为 2015-10-18
     *
     * @param dateTime
     * @return
     */
    fun transTime3(dateTime: String): String {
        return if (!TextUtils.isEmpty(dateTime)) {
            val str = dateTime.split("-").toTypedArray()
            val sb = StringBuilder()
            sb.append(str[0])
                .append("-")
                .append(str[1])
                .append("-")
                .append(str[2])
            sb.toString()
        } else ""
    }

    /**
     * 将 yyyy-MM-dd 格式时间转换为 yyyy.MM.dd
     *
     * @param dateTime
     * @return
     */
    fun transTime4(dateTime: String): String {
        return if (!TextUtils.isEmpty(dateTime)) {
            dateTime.replace("-", ".")
        } else ""
    }

    /**
     * 将 yyyy-MM-dd HH:mm:ss 格式时间转换为 yyyy.MM.dd
     *
     * @param dateTime
     * @return
     */
    fun transTime5(dateTime: String): String {
        return if (!TextUtils.isEmpty(dateTime)) {
            val newTime = dateTime.substring(0, 10)
            newTime.replace("-", ".")
        } else ""
    }

    /**
     * 服务器返回时间格式：2015/10/9 0:00:00
     *
     * @param time
     * @return 2015-10-09
     */
    fun convertTime(time: String): String? {
        if (!TextUtils.isEmpty(time)) {
            val str = time.split(" ").toTypedArray()
            if (str.size > 1) {
                str[0] = str[0].replace("/".toRegex(), "-")
                val ss = str[0].split("-").toTypedArray()
                var res: String? = null
                if (ss.size == 3) {
                    res = ss[0]
                    res += if (ss[1].length == 1) {
                        "-0" + ss[1]
                    } else {
                        "-" + ss[1]
                    }
                    res += if (ss[2].length == 1) {
                        "-0" + ss[2]
                    } else {
                        "-" + ss[2]
                    }
                }
                return res
            }
        }
        return null
    }

    @StringDef(
        DATE_FORMAT, DATE_FORMAT1, DATE_FORMAT2,
        DATE_FORMAT3, DATE_FORMAT4, DATE_FORMAT5, DATE_FORMAT6
    )

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    internal annotation class Format
}