package ando.toolkit.log

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * # 日志打印
 *
 * @author javakam
 * @date 2020/9/30  10:41
 */
object L {

    var logEnabled = true
    var logGlobalTag = "123"

    private const val TOP_LEFT_CORNER = '╔'
    private const val BOTTOM_LEFT_CORNER = '╚'
    private const val MIDDLE_CORNER = '╟'
    private const val VERTICAL_DOUBLE_LINE = '║'
    private const val HORIZONTAL_DOUBLE_LINE = "═════════════════════════════════════════════════"
    private const val SINGLE_LINE = "─────────────────────────────────────────────────"
    private val TOP_BORDER = TOP_LEFT_CORNER + HORIZONTAL_DOUBLE_LINE + HORIZONTAL_DOUBLE_LINE
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + HORIZONTAL_DOUBLE_LINE + HORIZONTAL_DOUBLE_LINE
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_LINE + SINGLE_LINE

    private var timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    private val logMethod = listOf(
        "verbose", "debug",
        "info", "warn",
        "error", "assert", "v", "d", "i", "w", "e", "a"
    )

    fun v(msg: String?) = v(logGlobalTag, msg)
    fun d(msg: String?) = d(logGlobalTag, msg)
    fun i(msg: String?) = i(logGlobalTag, msg)
    fun w(msg: String?) = w(logGlobalTag, msg)
    fun e(msg: String?) = e(logGlobalTag, msg)

    fun v(tag: String? = logGlobalTag, msg: String?) = logEnabled.debugLog(tag, msg, Log.VERBOSE)
    fun d(tag: String? = logGlobalTag, msg: String?) = logEnabled.debugLog(tag, msg, Log.DEBUG)
    fun i(tag: String? = logGlobalTag, msg: String?) = logEnabled.debugLog(tag, msg, Log.INFO)
    fun w(tag: String? = logGlobalTag, msg: String?) = logEnabled.debugLog(tag, msg, Log.WARN)
    fun e(tag: String? = logGlobalTag, msg: String?) = logEnabled.debugLog(tag, msg, Log.ERROR)

    private fun targetStackTraceMSg(): String {
        var targetStackTraceElement: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val stackTraceClassName = stackTraceElement.className
            val isLogClass = stackTraceClassName == L::class.java.name
            val isLogMethod = logMethod.contains(stackTraceElement.methodName.toLowerCase(Locale.getDefault()))
            val isLog = isLogClass || isLogMethod
            if (shouldTrace && !isLog) {
                targetStackTraceElement = stackTraceElement
                break
            }
            shouldTrace = isLog
        }
        return if (targetStackTraceElement != null) {
            "${targetStackTraceElement.className}.${targetStackTraceElement.methodName}(${targetStackTraceElement.fileName}:${targetStackTraceElement.lineNumber})"
        } else ""
    }

    private fun Boolean.debugLog(tag: String?, msg: String?, priority: Int) {
        if (msg.isNullOrBlank()) return
        if (this) Log.println(priority, tag, msgFormat(msg))
    }

    private fun msgFormat(msg: String): String {
        var showMsg = formatJson(msg)
        if (showMsg.contains("\n")) {
            showMsg = showMsg.replace("\n".toRegex(), "\n ")
        }
        return StringBuilder()
            .append("  \n")
            .append(TOP_BORDER)
            .appendLine()
            .append(VERTICAL_DOUBLE_LINE)
            .append("Thread: ${Thread.currentThread().name} at ${timeFormat.format(Date())}")
            .appendLine()
            .append(MIDDLE_BORDER)
            .appendLine()
            .append(VERTICAL_DOUBLE_LINE)
            .append(targetStackTraceMSg())
            .appendLine()
            .append(MIDDLE_BORDER)
            .appendLine()
            .append(VERTICAL_DOUBLE_LINE)
            .append(showMsg)
            .appendLine()
            .append(BOTTOM_BORDER)
            .toString()
    }

    /**
     * 格式化json
     */
    private fun formatJson(json: String): String {
        return try {
            val trimJson = json.trim()
            when {
                trimJson.startsWith("{") -> JSONObject(trimJson).toString(4)
                trimJson.startsWith("[") -> JSONArray(trimJson).toString(4)
                else -> trimJson
            }
        } catch (e: JSONException) {
            json
        }
    }

}