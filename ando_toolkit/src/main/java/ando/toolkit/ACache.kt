package ando.toolkit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import android.text.TextUtils
import android.util.LruCache
import ando.toolkit.AppUtils.getContext
import ando.toolkit.log.L.e
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * # ACache
 *
 * 磁盘缓存+内存缓存
 *
 * @author javakam
 * @date 2020-04-20
 */
class ACache private constructor(cacheDir: File, max_size: Long, max_count: Int) {
    private val mStringCache = LruCache<String, String>(MAX_MEMORY)
    private val mByteCache = LruCache<String, ByteArray>(MAX_MEMORY)
    private var mCache: ACache.ACacheManager

    init {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw RuntimeException("can't make dirs in ${cacheDir.absolutePath}")
        }
        mCache = ACacheManager(cacheDir, max_size, max_count)
    }

    // =======================================
    // ============    String   ==============
    // =======================================

    /**
     * 保存String到缓存中
     */
    fun put(key: String?, value: String?) {
        if (key != null && value != null) {
            mStringCache.put(key, value)
        }
        val file = mCache.newFile(key)
        var out: BufferedWriter? = null
        try {
            out = BufferedWriter(FileWriter(file), 1024)
            out.write(value)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mCache.put(file)
        }
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: String, saveTime: Int) {
        put(key,
            Utils.newStringWithDateInfo(saveTime, value)
        )
    }

    /**
     * 读取 String数据
     */
    fun getAsString(key: String?): String? {
        if (key.isNullOrBlank()) return null
        val result = mStringCache[key]
        if (result != null) return result
        val file = mCache[key]
        if (!file.exists()) return null
        var removeFile = false
        var reader: BufferedReader? = null
        return try {
            reader = BufferedReader(FileReader(file))
            var readString = ""
            var currentLine: String
            while (reader.readLine().also { currentLine = it } != null) {
                readString += currentLine
            }
            if (!Utils.isDue(readString)) {
                Utils.clearDateInfo(readString)
            } else {
                removeFile = true
                null
            }
        } catch (e: IOException) {
            e(TAG, e.message ?: "")
            null
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (removeFile) {
                remove(key)
            }
        }
    }

    /**
     * 保存long到缓存中
     */
    fun put(key: String?, value: Long) {
        put(key, value.toString())
    }

    /**
     * 保存long到缓存中
     */
    fun put(key: String?, value: Long, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 获取long类型数据
     */
    fun getAsLong(key: String?): Long {
        val result = getAsString(key)
        var num = Long.MIN_VALUE
        if (!TextUtils.isEmpty(result)) {
            try {
                num = result?.toLong() ?: 0L
            } catch (e: Exception) {
                e(TAG, e.message)
            }
        }
        return num
    }

    /**
     * 保存int类型数据
     */
    fun put(key: String?, value: Int) {
        put(key, value.toString())
    }

    /**
     * 保存int类型数据
     */
    fun put(key: String?, value: Int, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 获取int类型数据
     */
    fun getAsInt(key: String?): Int {
        val result = getAsString(key)
        var num = Int.MIN_VALUE
        if (!TextUtils.isEmpty(result)) {
            try {
                num = result?.toInt() ?: 0
            } catch (e: Exception) {
                e(TAG, e.message)
            }
        }
        return num
    }

    /**
     * 保存float类型数据
     */
    fun put(key: String?, value: Float) {
        put(key, value.toString())
    }

    /**
     * 保存float类型数据
     */
    fun put(key: String?, value: Float, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 获取float类型数据
     */
    fun getAsFloat(key: String?): Float {
        val result = getAsString(key)
        var num = Float.MIN_VALUE
        if (!TextUtils.isEmpty(result)) {
            try {
                num = result?.toFloat() ?: 0F
            } catch (e: Exception) {
                e(TAG, e.message)
            }
        }
        return num
    }

    /**
     * 保存double类型数据
     */
    fun put(key: String?, value: Double) {
        put(key, value.toString())
    }

    /**
     * 保存double类型数据
     */
    fun put(key: String?, value: Double, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 获取double类型数据
     */
    fun getAsDouble(key: String?): Double {
        val result = getAsString(key)
        var num = Double.MIN_VALUE
        if (!TextUtils.isEmpty(result)) {
            try {
                num = result?.toDouble() ?: 0.00
            } catch (e: Exception) {
                e(TAG, e.message)
            }
        }
        return num
    }

    /**
     * 保存boolean类型数据
     */
    fun put(key: String?, value: Boolean) {
        put(key, value.toString())
    }

    /**
     * 保存boolean类型数据
     */
    fun put(key: String?, value: Boolean, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 获取boolean类型数据
     */
    fun getAsBoolean(key: String?): Boolean {
        val result = getAsString(key)
        var num = false
        if (!TextUtils.isEmpty(result)) {
            try {
                num = java.lang.Boolean.parseBoolean(result)
            } catch (e: Exception) {
                e(TAG, e.message)
            }
        }
        return num
    }

    // =======================================
    // ============= JSONObject ==============
    // =======================================

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSON数据
     */
    fun put(key: String?, value: JSONObject) {
        put(key, value.toString())
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONObject数据
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: JSONObject, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 读取JSONObject数据
     */
    fun getAsJSONObject(key: String?): JSONObject? {
        val json = getAsString(key)
        return try {
            JSONObject(json ?: "")
        } catch (e: Exception) {
            e(TAG, e.message)
            null
        }
    }

    // =======================================
    // ============  JSONArray   =============
    // =======================================

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSONArray数据
     */
    fun put(key: String?, value: JSONArray) {
        put(key, value.toString())
    }

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONArray数据
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: JSONArray, saveTime: Int) {
        put(key, value.toString(), saveTime)
    }

    /**
     * 读取JSONArray数据
     *
     * @param key KEY
     * @return 数据
     */
    fun getAsJSONArray(key: String?): JSONArray? {
        val json = getAsString(key)
        return try {
            JSONArray(json ?: "")
        } catch (e: Exception) {
            e(TAG, e.message)
            null
        }
    }

    // =======================================
    // ==============    byte    =============
    // =======================================

    /**
     * 保存 byte 数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    fun put(key: String?, value: ByteArray?) {
        if (key != null && value != null) {
            mByteCache.put(key, value)
        }
        val file = mCache.newFile(key)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            out.write(value)
        } catch (e: Exception) {
            e(TAG, e.message)
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mCache.put(file)
        }
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: ByteArray?, saveTime: Int) {
        put(key,
            Utils.newByteArrayWithDateInfo(saveTime, value)
        )
    }

    /**
     * 获取 byte 数据
     *
     * @param key KEY
     * @return byte    数据
     */
    fun getAsBinary(key: String?): ByteArray? {
        var bytes: ByteArray? = null
        if (key != null) {
            bytes = mByteCache[key]
        }
        if (bytes != null && bytes.isNotEmpty()) {
            return bytes
        }
        var raFile: RandomAccessFile? = null
        var removeFile = false
        return try {
            val file = mCache[key]
            if (!file.exists()) {
                return null
            }
            raFile = RandomAccessFile(file, "r")
            val byteArray = ByteArray(raFile.length().toInt())
            raFile.read(byteArray)
            if (!Utils.isDue(byteArray)) {
                Utils.clearDateInfo(byteArray)
            } else {
                removeFile = true
                null
            }
        } catch (e: Exception) {
            e(TAG, e.message)
            null
        } finally {
            if (raFile != null) {
                try {
                    raFile.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (removeFile) {
                remove(key)
            }
        }
    }

    // =======================================
    // =============   序列化   ===============
    // =======================================

    /**
     * 保存 Serializable数据到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的value
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: Serializable?, saveTime: Int = -1) {
        var oos: ObjectOutputStream? = null
        try {
            val baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(value)
            val data = baos.toByteArray()
            if (saveTime != -1) {
                put(key, data, saveTime)
            } else {
                put(key, data)
            }
        } catch (e: Exception) {
            e(TAG, e.message)
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun <T> getAsObject(key: String?, clazz: Class<T>): T? {
        var t1: T? = null
        try {
            @Suppress("UNCHECKED_CAST") val t2 = getAsObject(key) as? T?
            if (t2 != null) {
                return t2
            }
        } catch (e: Exception) {
            e(TAG, e.message)
        }
        try {
            t1 = clazz.newInstance()
        } catch (e: Exception) {
            e(TAG, e.message)
        }
        return t1
    }

    /**
     * 读取 Serializable 数据
     *
     * @param key KEY
     * @return Serializable 数据
     */
    fun getAsObject(key: String?): Any? {
        val data = getAsBinary(key)
        if (data != null) {
            var bais: ByteArrayInputStream? = null
            var ois: ObjectInputStream? = null
            try {
                bais = ByteArrayInputStream(data)
                ois = ObjectInputStream(bais)
                return ois.readObject()
            } catch (e: Exception) {
                e(TAG, e.message)
            } finally {
                try {
                    bais?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    // =======================================
    // ==============   bitmap   =============
    // =======================================

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的bitmap数据
     */
    fun put(key: String?, value: Bitmap?) {
        put(key, Utils.bitmap2Bytes(value))
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 bitmap 数据
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: Bitmap?, saveTime: Int) {
        put(key, Utils.bitmap2Bytes(value), saveTime)
    }

    /**
     * 读取 bitmap 数据
     *
     * @param key KEY
     * @return bitmap    数据
     */
    fun getAsBitmap(key: String?): Bitmap? {
        return if (getAsBinary(key) == null) {
            null
        } else Utils.bytes2Bitmap(getAsBinary(key))
    }
    // =======================================
    // ============= drawable 数据 读写 =============
    // =======================================
    /**
     * 保存 drawable 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的drawable数据
     */
    fun put(key: String?, value: Drawable?) {
        put(key, Utils.drawable2Bitmap(value))
    }

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 drawable 数据
     * @param saveTime 保存的时间，单位：秒
     */
    fun put(key: String?, value: Drawable?, saveTime: Int) {
        put(key, Utils.drawable2Bitmap(value), saveTime)
    }

    /**
     * 读取 Drawable 数据
     *
     * @param key KEY
     * @return Drawable 数据
     */
    fun getAsDrawable(key: String?): Drawable? {
        return if (getAsBinary(key) == null) null else Utils.bitmap2Drawable(
            Utils.bytes2Bitmap(getAsBinary(key))
        )
    }

    /**
     * 获取缓存文件
     *
     * @param key KEY
     * @return values    缓存的文件
     */
    fun file(key: String?): File? {
        val f = mCache.newFile(key)
        return if (f.exists()) f else null
    }

    /**
     * 移除某个key
     *
     * @param key KEY
     * @return 是否移除成功
     */
    fun remove(key: String?): Boolean {
        if (key != null) {
            mStringCache.remove(key)
        }
        return mCache.remove(key)
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        mStringCache.evictAll()
        mCache.clear()
    }

    /**
     * 缓存管理器
     */
    private inner class ACacheManager(
        private val cacheDir: File,
        private val sizeLimit: Long,
        private val countLimit: Int
    ) {
        private val threads: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
        private val cacheSize: AtomicLong = AtomicLong()
        private val cacheCount: AtomicInteger = AtomicInteger()
        private val lastUsageDates = Collections
            .synchronizedMap(HashMap<File, Long>())

        /**
         * 计算 cacheSize和cacheCount
         */
        private fun calculateCacheSizeAndCacheCount() {
            threads.submit {
                var size = 0
                var count = 0
                val cachedFiles = cacheDir.listFiles()
                if (cachedFiles != null) {
                    for (cachedFile in cachedFiles) {
                        size += calculateSize(cachedFile).toInt()
                        count += 1
                        lastUsageDates[cachedFile] = cachedFile.lastModified()
                    }
                    cacheSize.set(size.toLong())
                    cacheCount.set(count)
                }
            }
        }

        fun put(file: File) {
            var curCacheCount = cacheCount.get()
            while (curCacheCount + 1 > countLimit) {
                val freedSize = removeNext()
                cacheSize.addAndGet(-freedSize)
                curCacheCount = cacheCount.addAndGet(-1)
            }
            cacheCount.addAndGet(1)
            val valueSize = calculateSize(file)
            var curCacheSize = cacheSize.get()
            while (curCacheSize + valueSize > sizeLimit) {
                val freedSize = removeNext()
                curCacheSize = cacheSize.addAndGet(-freedSize)
            }
            cacheSize.addAndGet(valueSize)
            val currentTime = System.currentTimeMillis()
            file.setLastModified(currentTime)
            lastUsageDates[file] = currentTime
        }

        operator fun get(key: String?): File {
            val file = newFile(key)
            val currentTime = System.currentTimeMillis()
            file.setLastModified(currentTime)
            lastUsageDates[file] = currentTime
            return file
        }

        fun newFile(key: String?): File {
            return File(cacheDir, key.hashCode().toString() + "")
        }

        fun remove(key: String?): Boolean {
            val image = get(key)
            return image.delete()
        }

        fun clear() {
            lastUsageDates.clear()
            cacheSize.set(0)
            val files = cacheDir.listFiles()
            if (files != null) {
                for (f in files) {
                    f.delete()
                }
            }
        }

        /**
         * 移除旧的文件
         */
        private fun removeNext(): Long {
            if (lastUsageDates.isEmpty()) {
                return 0
            }
            var oldestUsage: Long? = null
            var mostLongUsedFile: File? = null
            val entries: Set<Map.Entry<File, Long>> = lastUsageDates.entries
            synchronized(lastUsageDates) {
                for ((key, lastValueUsage) in entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = key
                        oldestUsage = lastValueUsage
                    } else {
                        if (lastValueUsage < oldestUsage ?: 0L) {
                            oldestUsage = lastValueUsage
                            mostLongUsedFile = key
                        }
                    }
                }
            }
            val fileSize = calculateSize(mostLongUsedFile)
            if (mostLongUsedFile?.delete() == true) {
                lastUsageDates.remove(mostLongUsedFile)
            }
            return fileSize
        }

        private fun calculateSize(file: File?): Long {
            return file?.length() ?: 0L
        }

        init {
            calculateCacheSizeAndCacheCount()
        }
    }

    /**
     * 时间计算工具类
     */
    private object Utils {
        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true：到期了 false：还没有到期
         */
        fun isDue(str: String): Boolean {
            return isDue(str.toByteArray())
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data
         * @return true：到期了 false：还没有到期
         */
        fun isDue(data: ByteArray): Boolean {
            val strs = ACache.Utils.getDateInfoFromDate(data)
            if (strs != null && strs.size == 2) {
                var saveTimeStr = strs[0]
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr
                        .substring(1, saveTimeStr.length)
                }
                val saveTime = java.lang.Long.valueOf(saveTimeStr)
                val deleteAfter = java.lang.Long.valueOf(strs[1])
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true
                }
            }
            return false
        }

        fun newStringWithDateInfo(second: Int, strInfo: String): String {
            return createDateInfo(second) + strInfo
        }

        fun newByteArrayWithDateInfo(second: Int, data2: ByteArray?): ByteArray {
            val data1 = createDateInfo(second).toByteArray()
            val retData = ByteArray(data1.size + (data2?.size ?: 0))
            System.arraycopy(data1, 0, retData, 0, data1.size)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            System.arraycopy(data2, 0, retData, data1.size, (data2?.size ?: 0))
            return retData
        }

        fun clearDateInfo(strInfo: String): String? {
            var info: String? = strInfo
            if (info != null && hasDateInfo(info.toByteArray())) {
                info = info.substring(
                    info.indexOf(mSeparator) + 1,
                    info.length
                )
            }
            return info
        }

        fun clearDateInfo(data: ByteArray): ByteArray {
            return if (hasDateInfo(data)) {
                copyOfRange(
                    data,
                    indexOf(
                        data,
                        mSeparator
                    ) + 1,
                    data.size
                )
            } else data
        }

        private fun hasDateInfo(data: ByteArray?): Boolean {
            return data != null && data.size > 15 && data[13] == '-'.toByte() && indexOf(
                data,
                mSeparator
            ) > 14
        }

        private fun getDateInfoFromDate(data: ByteArray): Array<String>? {
            if (hasDateInfo(data)) {
                val saveDate = String(
                    copyOfRange(
                        data,
                        0,
                        13
                    )
                )
                val deleteAfter = String(
                    copyOfRange(
                        data, 14,
                        indexOf(
                            data,
                            mSeparator
                        )
                    )
                )
                return arrayOf(saveDate, deleteAfter)
            }
            return null
        }

        private fun indexOf(data: ByteArray, c: Char?): Int {
            for (i in data.indices) {
                if (data[i] == c?.toByte()) {
                    return i
                }
            }
            return -1
        }

        private fun copyOfRange(original: ByteArray, from: Int, to: Int): ByteArray {
            val newLength = to - from
            require(newLength >= 0) { "$from > $to" }
            val copy = ByteArray(newLength)
            System.arraycopy(
                original, from, copy, 0,
                Math.min(original.size - from, newLength)
            )
            return copy
        }

        private const val mSeparator = ' '
        private fun createDateInfo(second: Int): String {
            var currentTime = System.currentTimeMillis().toString() + ""
            while (currentTime.length < 13) {
                currentTime = "0$currentTime"
            }
            return "$currentTime-$second$mSeparator"
        }

        /*
         * Bitmap → byte[]
         */
        fun bitmap2Bytes(bm: Bitmap?): ByteArray? {
            if (bm == null) {
                return null
            }
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }

        /*
         * byte[] → Bitmap
         */
        fun bytes2Bitmap(b: ByteArray?): Bitmap? {
            return if (b?.size == 0) null else BitmapFactory.decodeByteArray(b, 0, b?.size ?: 0)
        }

        /*
         * Drawable → Bitmap
         */
        fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
            if (drawable == null) {
                return null
            }
            // 取 drawable 的长宽
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight
            // 取 drawable 的颜色格式
            @Suppress("DEPRECATION") val config =
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            // 建立对应 bitmap
            val bitmap = Bitmap.createBitmap(w, h, config)
            // 建立对应 bitmap 的画布
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, w, h)
            // 把 drawable 内容画到画布中
            drawable.draw(canvas)
            return bitmap
        }

        /*
         * Bitmap → Drawable
         */
        fun bitmap2Drawable(bm: Bitmap?): Drawable? {
            return bm?.let { BitmapDrawable(getContext().resources, it) }
        }
    }

    companion object {
        private const val TAG = "ACache"

        private val MAX_MEMORY = Runtime.getRuntime().maxMemory().toInt() / 8
        private const val MAX_SIZE = 1000 * 1000 * 300 // 300 MB
        private const val MAX_COUNT = Int.MAX_VALUE // 不限制存放数据的数量
        private const val CACHE = "ACache"
        private val mInstanceMap: MutableMap<String, ACache> = HashMap()

        /**
         * 获取文件存储路径(Get file storage path)
         *
         * /SDCard/Android/data/包名/files/
         *
         * 设置：对应清除数据(Settings: corresponding to clear data)
         */
        private fun getFilesPath(dirName: String): String? {
            val filesDir = if (AppUtils.isDebug()) getContext().getExternalFilesDir(null)
            else getContext().filesDir
            val root = filesDir?.absolutePath

            return if (root != null && root.isNotBlank() && !TextUtils.isEmpty(dirName)) {
                val path = root + File.separator + dirName + File.separator
                val file = File(path)
                if (!file.exists() && !file.mkdirs()) {
                    throw  RuntimeException("can't make dirs in " + file.absolutePath);
                }
                path
            } else root
        }

        fun build(cacheName: String?): ACache? {
            val filePath = getFilesPath(
                cacheName ?: CACHE
            )
            return if (!filePath.isNullOrBlank()) {
                build(
                    File(filePath),
                    MAX_SIZE.toLong(),
                    MAX_COUNT
                )
            } else null
        }

        fun build(max_size: Long, max_count: Int): ACache? {
            val filePath =
                getFilesPath(CACHE)
            if (filePath != null) {
                val file = File(filePath)
                return build(
                    file,
                    max_size,
                    max_count
                )
            }
            return null
        }

        fun build(cacheDir: File, max_size: Long, max_count: Int): ACache {
            var manager = mInstanceMap[cacheDir.absoluteFile.toString() + myPid()]
            if (manager == null) {
                manager = ACache(cacheDir, max_size, max_count)
                mInstanceMap[cacheDir.absolutePath + myPid()] = manager
            }
            return manager
        }

        private fun myPid(): String {
            return "_" + Process.myPid()
        }
    }
}