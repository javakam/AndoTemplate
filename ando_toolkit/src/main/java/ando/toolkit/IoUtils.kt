package ando.toolkit

import ando.toolkit.log.L
import java.io.*

/**
 * From https://www.codercrunch.com/question/1863527609/copy-bytes-inputstream-outputstream-android
 */
object IoUtils {
    private const val BUFFER_SIZE = 1024 * 2

    @Throws(Exception::class, IOException::class)
    fun copy(input: InputStream?, output: OutputStream?): Int {
        val buffer = ByteArray(BUFFER_SIZE)
        val ins = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n: Int
        try {
            while (ins.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            close(ins, out)
        }
        return count
    }

    fun close(vararg c: Closeable?) {
        if (c.isNullOrEmpty()) return
        try {
            c.forEach { it?.close() }
        } catch (e: IOException) {
            L.i(e.message)
        }
    }
}