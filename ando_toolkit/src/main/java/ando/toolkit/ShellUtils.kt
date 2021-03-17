package ando.toolkit

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * # ShellUtils
 *
 * @author javakam
 * @date 2020/9/29  15:33
 */
object ShellUtils {
    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * The result of command.
     */
    class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {
        override fun toString(): String {
            return """
               result: $result
               successMsg: $successMsg
               errorMsg: $errorMsg
               """.trimIndent()
        }
    }

    fun execCmd(command: String?, isRooted: Boolean): CommandResult {
        return execCmd(
                if (command.isNullOrEmpty()) null else arrayOf<String>(command),
                isRooted,
                true
        )
    }

    fun execCmd(commands: List<String>?, isRooted: Boolean): CommandResult {
        return execCmd(
                if (commands.isNullOrEmpty()) null else commands,
                isRooted,
                true
        )
    }

    fun execCmd(commands: Array<String>?, isRooted: Boolean): CommandResult {
        return execCmd(commands, isRooted, true)
    }

    fun execCmd(
            command: String?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(
                if (command.isNullOrEmpty()) null else arrayOf<String>(command),
                isRooted,
                isNeedResultMsg
        )
    }

    fun execCmd(
            commands: List<String>?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(
                if (commands.isNullOrEmpty()) null else commands.toTypedArray(),
                isRooted,
                isNeedResultMsg
        )
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
            commands: Array<String>?,
            isRooted: Boolean,
            isNeedResultMsg: Boolean
    ): CommandResult {
        var result = -1
        if (commands.isNullOrEmpty()) {
            return CommandResult(result, "", "")
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh")
            os = DataOutputStream(process.outputStream)
            for (command: String? in commands) {
                if (command.isNullOrBlank()) continue
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
                os.flush()
            }
            os.writeBytes("exit$LINE_SEP")
            os.flush()
            result = process.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(
                        InputStreamReader(process.inputStream, "UTF-8")
                )
                errorResult = BufferedReader(
                        InputStreamReader(process.errorStream, "UTF-8")
                )
                var line: String?
                if (successResult.readLine().also { line = it } != null) {
                    successMsg.append(line)
                    while (successResult.readLine().also { line = it } != null) {
                        successMsg.append(LINE_SEP).append(line)
                    }
                }
                if (errorResult.readLine().also { line = it } != null) {
                    errorMsg.append(line)
                    while (errorResult.readLine().also { line = it } != null) {
                        errorMsg.append(LINE_SEP).append(line)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                successResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            process?.destroy()
        }
        return CommandResult(
                result,
                successMsg?.toString() ?: "",
                errorMsg?.toString() ?: ""
        )
    }

    /**
     * 执行shell命令, 命令中不必带`adb shell`
     *
     * @param cmd
     * @return Sting  命令执行在控制台输出的结果
     */
    fun execCmd(cmd: String): String? {
        var process: Process? = null
        var bufferedReader: BufferedReader? = null
        var inputStreamReader: InputStreamReader? = null
        return try {
            process = Runtime.getRuntime().exec(cmd)
            inputStreamReader = InputStreamReader(process.inputStream)
            bufferedReader = BufferedReader(inputStreamReader)
            var read: Int
            val buffer = CharArray(8 * 1024)
            val output = java.lang.StringBuilder()
            while (bufferedReader.read(buffer).also { read = it } > 0) {
                output.append(buffer, 0, read)
            }
            output.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                inputStreamReader?.close()
                bufferedReader?.close()
                process?.destroy()
            } catch (t: Throwable) {
            }
        }
    }
}