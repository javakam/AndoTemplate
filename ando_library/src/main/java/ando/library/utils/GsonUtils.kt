package ando.library.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*

/**
 * # GsonUtils
 *
 * Description: com.google.gson.Gson
 *
 * @author javakam
 * @date 2019/12/17 10:13
 */
object GsonUtils {

    private val sGson: Gson by lazy { GsonBuilder().serializeNulls().create() }

    private fun createGson(): Gson {
        synchronized(Gson::class.java) {
            return sGson
        }
    }

    /**
     * 将对象转成jsonString
     */
    fun <T> toJsonString(t: T): String = createGson().toJson(t).toString()

    @Throws(JSONException::class)
    fun toJson(obj: Any?): JSONObject = JSONObject(createGson().toJson(obj) ?: "")

    // 在 kotlin 中扩展 Java 类的 Gson.fromJson 方法
    // 不在传入 T 的class，inline 的作用就是将函数插入到被调用处，配合 reified 就可以获取到 T 真正的类型
    inline fun <reified T : Any> fromJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

    /**
     * 将json字符串转成对象
     */
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(json: String?, cla: Class<T>?): T = createGson().fromJson(json, cla)

    /**
     * new TypeToken<List></List><Music>>(){}.getType()
     */
    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(jsonStr: String?, type: Type?): List<T> {
        return createGson().fromJson(jsonStr, type) ?: emptyList()
    }

    fun <T> fromJsonByLinkedTreeMap(message: String?, jsonHead: String?, type: Type?): List<T> {
        val jsonObject = JsonParser.parseString(message).asJsonObject
        val jsonArray = jsonObject.getAsJsonArray(jsonHead)
        val list: MutableList<T> = ArrayList()
        for (jsonElement in jsonArray) {
            list.add(createGson().fromJson(jsonElement, type) ?: continue)  //type
        }
        return list
    }

    fun <T> fromJsonByLinkedTreeMap(
        message: String?,
        jsonHead: String?,
        cls: Class<T>?
    ): List<T> {
        val jsonObject = JsonParser.parseString(message).asJsonObject
        val jsonArray = jsonObject.getAsJsonArray(jsonHead)
        val list: MutableList<T> = ArrayList()
        for (jsonElement in jsonArray) {
            list.add(createGson().fromJson(jsonElement, cls) ?: continue) //cls
        }
        return list
    }
}