package ando.toolkit

import ando.toolkit.AppUtils.getContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * # AssetsUtils
 *
 * 读取 main -> assets 目录下的文件
 *
 * eg: AssetsUtils.getBeanByClass(Application.get(), "video.json", VideoBean.class);
 *
 * @author javakam
 * @date 2019/11/7  10:12
 */
object AssetsUtils {

    /**
     * 读取 json 文件并转为 String
     */
    fun readAssetsData(fileName: String): String {
        val sb = StringBuilder()
        var bf: BufferedReader? = null
        try {
            bf = BufferedReader(InputStreamReader(getContext().assets.open(fileName)))
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bf?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

}