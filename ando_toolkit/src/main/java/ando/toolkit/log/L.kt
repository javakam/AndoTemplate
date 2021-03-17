package ando.toolkit.log

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Title: Log
 * <p>
 * Description:扩展函数 - 日志打印
 * </p>
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

    private const val CALL_INDEX = 5

    //ToolKit.isDebug()
    fun init(globalTag: String, enable: Boolean) {
        logGlobalTag = globalTag
        logEnabled = enable
    }

    fun v(msg: String?) {
        v(msg, logGlobalTag)
    }

    fun v(tag: String?, msg: String?) {
        log(Log.VERBOSE, tag = tag, msg = msg)
    }

    fun d(msg: String?) {
        d(msg, logGlobalTag)
    }

    fun d(tag: String?, msg: String?) {
        log(Log.DEBUG, tag = tag, msg = msg)
    }

    fun i(msg: String?) {
        i(msg, logGlobalTag)
    }

    fun i(tag: String?, msg: String?) {
        log(Log.INFO, tag = tag, msg = msg)
    }

    fun w(msg: String?) {
        w(msg, logGlobalTag)
    }

    fun w(tag: String?, msg: String?) {
        log(Log.WARN, tag = tag, msg = msg)
    }

    fun e(msg: String?) {
        e(msg, logGlobalTag)
    }

    fun e(tag: String?, msg: String?) {
        log(Log.ERROR, tag = tag, msg = msg)
    }

    fun json(msg: String?) {
        json(msg, logGlobalTag)
    }

    fun json(tag: String?, msg: String?) {
        log(Log.ERROR, tag = tag, msg = formatJson(msg))
    }

    /**
     * 格式化json
     */
    private fun formatJson(json: String?): String {
        return try {
            val trimJson = json?.trim() ?: ""
            when {
                trimJson.startsWith("{") -> JSONObject(trimJson).toString(4)
                trimJson.startsWith("[") -> JSONArray(trimJson).toString(4)
                else -> trimJson
            }
        } catch (e: JSONException) {
            e.printStackTrace().toString()
        }
    }

    /**
     * 输出日志
     * @param priority 日志级别
     * @param tag
     * @param msg
     */
    private fun log(priority: Int, tag: String?, msg: String?) {
        if (!logEnabled) return

        val elements = Thread.currentThread().stackTrace
        val index = findIndex(elements)
        val element = elements[index]
        val tagHandled = handleTag(element, tag ?: "")
        var message = msg ?: ""
        if (msg?.contains("\n") == true) {
            message = msg.replace("\n".toRegex(), "\n$VERTICAL_DOUBLE_LINE ")
        }
        Log.println(priority, tagHandled, handleFormat(element, message))
    }

    /**
     * 格式化log
     * @param element
     */
    private fun handleFormat(element: StackTraceElement, msg: String): String =
        StringBuilder().apply {
            append(TOP_BORDER)
            appendLine()
            // 添加当前线程名
            append("║ " + "Thread: " + Thread.currentThread().name)
            appendLine()
            append(MIDDLE_BORDER)
            appendLine()
            // 添加类名、方法名、行数
            append("║ ").append(element.className)
                .append(".")
                .append(element.methodName).append(" (")
                .append(element.fileName)
                .append(":")
                .append(element.lineNumber)
                .append(")")
            appendLine()
            append(MIDDLE_BORDER)
            appendLine()
            // 添加打印的日志信息
            append("$VERTICAL_DOUBLE_LINE ")
            append(msg)
            appendLine()
            append(BOTTOM_BORDER)
            appendLine()
        }.toString()

    /**
     * 处理tag逻辑
     * @param element
     * @param tag
     */
    private fun handleTag(element: StackTraceElement, tag: String): String =
        when {
            tag.isNotBlank() -> tag
            logGlobalTag.isNotBlank() -> logGlobalTag
            else -> element.className.substringAfterLast(".")
        }

    /**
     * 寻找当前调用类在[elements]中的下标
     * @param elements
     */
    private fun findIndex(elements: Array<StackTraceElement>): Int {
        var index = CALL_INDEX
        while (index < elements.size) {
            val className = elements[index].className
            if (className != L::class.java.name && !elements[index].methodName.startsWith("log")) {
                return index
            }
            index++
        }
        return -1
    }

}