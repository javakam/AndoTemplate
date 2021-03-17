package ando.toolkit

import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

/**
 * # PrintUtils 打印工具类
 *
 * http://blog.csdn.net/haovip123/article/details/50739670
 */
open class PrintUtils(ip: String?, port: Int, encoding: String?) {
    //定义编码方式
    private lateinit var encoding: String
    private var sock: Socket? = null

    // 通过socket流进行读写
    private var socketOut: OutputStream? = null
    private var writer: OutputStreamWriter? = null

    /**
     * 关闭IO流和Socket
     */
    @Throws(IOException::class)
    fun closeIOAndSocket() {
        writer?.close()
        socketOut?.close()
        sock?.close()
    }

    /**
     * 打印二维码
     *
     * @param qrData 二维码的内容
     * @throws IOException
     */
    @Throws(IOException::class)
    fun qrCode(qrData: String) {
        val moduleSize = 8
        val length = qrData.toByteArray(charset(encoding)).size

        //打印二维码矩阵
        writer?.write(0x1D) // init
        writer?.write("(k") // adjust height of barcode
        writer?.write((length + 3).toString()) // pl
        writer?.write(0) // ph
        writer?.write(49) // cn
        writer?.write(80) // fn
        writer?.write(48) //
        writer?.write(qrData)
        writer?.write(0x1D)
        writer?.write("(k")
        writer?.write(3)
        writer?.write(0)
        writer?.write(49)
        writer?.write(69)
        writer?.write(48)
        writer?.write(0x1D)
        writer?.write("(k")
        writer?.write(3)
        writer?.write(0)
        writer?.write(49)
        writer?.write(67)
        writer?.write(moduleSize)
        writer?.write(0x1D)
        writer?.write("(k")
        writer?.write(3) // pl
        writer?.write(0) // ph
        writer?.write(49) // cn
        writer?.write(81) // fn
        writer?.write(48) // m
        writer?.flush()
    }

    /**
     * 进纸并全部切割
     */
    @Throws(IOException::class)
    protected fun feedAndCut() {
        writer?.write(0x1D)
        writer?.write(86)
        writer?.write(65)
        //writer.write(0);
        //切纸前走纸多少
        writer?.write(100)
        writer?.flush()

        //另外一种切纸的方式
        //byte[] bytes = {29, 86, 0};
        //socketOut.write(bytes);
    }

    /**
     * 打印换行
     *
     *
     * length 需要打印的空行数
     */
    @Throws(IOException::class)
    fun printLine(lineNum: Int) {
        for (i in 0 until lineNum) {
            writer?.write("\n")
        }
        writer?.flush()
    }

    /**
     * 打印换行(只换一行)
     */
    @Throws(IOException::class)
    fun printLine() {
        writer?.write("\n")
        writer?.flush()
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度
     */
    @Throws(IOException::class)
    fun printTabSpace(length: Int) {
        for (i in 0 until length) {
            writer?.write("\t")
        }
        writer?.flush()
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度
     */
    @Throws(IOException::class)
    fun printWordSpace(length: Int) {
        for (i in 0 until length) {
            writer?.write("  ")
        }
        writer?.flush()
    }

    /**
     * 打印位置调整
     *
     * @param position 打印位置  0：居左(默认) 1：居中 2：居右
     */
    @Throws(IOException::class)
    fun printLocation(position: Int) {
        writer?.write(0x1B)
        writer?.write(97)
        writer?.write(position)
        writer?.flush()
    }

    /**
     * 绝对打印位置
     */
    @Throws(IOException::class)
    fun printLocation(light: Int, weight: Int) {
        writer?.write(0x1B)
        writer?.write(0x24)
        writer?.write(light)
        writer?.write(weight)
        writer?.flush()
    }

    /**
     * 打印文字
     */
    @Throws(IOException::class)
    fun printText(text: String) {
        val content = text.toByteArray(charset("gbk"))
        socketOut?.write(content)
        socketOut?.flush()
    }

    /**
     * 新起一行，打印文字
     */
    @Throws(IOException::class)
    fun printTextNewLine(text: String) {
        //换行
        writer?.write("\n")
        writer?.flush()
        val content = text.toByteArray(charset("gbk"))
        socketOut?.write(content)
        socketOut?.flush()
    }

    /**
     * 初始化打印机
     */
    @Throws(IOException::class)
    fun initPos() {
        writer?.write(0x1B)
        writer?.write(0x40)
        writer?.flush()
    }

    /**
     * 加粗
     *
     * @param flag false为不加粗
     */
    @Throws(IOException::class)
    fun bold(flag: Boolean) {
        if (flag) {
            //常规粗细
            writer?.write(0x1B)
            writer?.write(69)
            writer?.write(0xF)
            writer?.flush()
        } else {
            //加粗
            writer?.write(0x1B)
            writer?.write(69)
            writer?.write(0)
            writer?.flush()
        }
    }

    /**
     * 初始化Pos实例
     *
     * @param ip       打印机IP
     * @param port     打印机端口号
     * @param encoding 编码
     */
    init {
        sock = Socket(ip, port)
        socketOut = DataOutputStream(sock?.getOutputStream())
        this.encoding = encoding ?: StandardCharsets.UTF_8.name()
        writer = OutputStreamWriter(socketOut, encoding)
    }
}