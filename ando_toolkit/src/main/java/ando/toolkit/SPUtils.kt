package ando.toolkit

import android.content.Context
import android.content.SharedPreferences
import android.util.ArrayMap
import ando.toolkit.AppUtils.getContext

/**
 * # SPUtils
 *
 * @author javakam
 * @date 2019/3/17 14:44
 */
class SPUtils private constructor(spName: String) {
    private val sp: SharedPreferences

    /**
     * SP中写入String
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String, value: String?) {
        sp.edit().putString(key, value).apply()
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`""`
     */
    fun getString(key: String?): String? {
        return getString(key, "")
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getString(key: String?, defaultValue: String): String? {
        return sp.getString(key, defaultValue)
    }

    /**
     * SP中写入int
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Int?) {
        sp.edit().putInt(key, value ?: return).apply()
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getInt(key: String?): Int? {
        return getInt(key, -1)
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getInt(key: String?, defaultValue: Int): Int? {
        return sp.getInt(key, defaultValue)
    }

    /**
     * SP中写入long
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Long?) {
        sp.edit().putLong(key, value ?: return).apply()
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getLong(key: String?): Long? {
        return getLong(key, -1L)
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getLong(key: String?, defaultValue: Long): Long? {
        return sp.getLong(key, defaultValue)
    }

    /**
     * SP中写入float
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Float?) {
        sp.edit().putFloat(key, value ?: return).apply()
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    fun getFloat(key: String?): Float? {
        return getFloat(key, -1f)
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getFloat(key: String?, defaultValue: Float): Float? {
        return sp.getFloat(key, defaultValue)
    }

    /**
     * SP中写入boolean
     *
     * @param key   键
     * @param value 值
     */
    fun put(key: String?, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`false`
     */
    fun getBoolean(key: String?): Boolean {
        return getBoolean(key, false)
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    /**
     * SP中写入String集合
     *
     * @param key    键
     * @param values 值
     */
    fun put(key: String?, values: Set<String?>) {
        sp.edit().putStringSet(key, values).apply()
    }

    /**
     * SP中读取StringSet
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值`Collections.<String>emptySet()`
     */
    fun getStringSet(key: String?): Set<String>? {
        return getStringSet(key, emptySet<String>())
    }

    /**
     * SP中读取StringSet
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getStringSet(key: String?, defaultValue: Set<String?>): Set<String>? {
        return sp.getStringSet(key, defaultValue)
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    val all: Map<String, *>
        get() = sp.all

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    operator fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    fun remove(key: String) {
        sp.edit().remove(key).apply()
    }

    /**
     * SP中清除所有数据
     */
    fun clear() {
        sp.edit().clear().apply()
    }

    companion object {
        private const val DEFAULT_NAME = "ShareData"
        private val sSPMap = ArrayMap<String, SPUtils>()

        /**
         * 获取SP实例
         *
         * @return [SPUtils]
         */
        fun get(): SPUtils = get(DEFAULT_NAME)

        /**
         * 获取SP实例
         *
         * @param spName sp名
         * @return [SPUtils]
         */
        fun get(spName: String?): SPUtils {
            var name = spName
            if (name.isNullOrBlank()) {
                name = DEFAULT_NAME
            }
            var sp = sSPMap[name]
            if (sp == null) {
                sp = SPUtils(name)
                sSPMap[name] = sp
            }
            return sp
        }
    }

    init {
        sp = getContext().getSharedPreferences(spName, Context.MODE_PRIVATE)
    }
}