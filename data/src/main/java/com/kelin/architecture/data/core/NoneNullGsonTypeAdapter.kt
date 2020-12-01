package com.kelin.architecture.data.core

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * **描述:** 用来处理后台返回null的反序列化Gson适配器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/5/13 1:12 PM
 *
 * **版本:** v 1.0.0
 */
class NoneNullGsonTypeAdapter : TypeAdapterFactory {

    @Suppress("unchecked_cast")
    override fun <Any> create(gson: Gson?, type: TypeToken<Any>?): TypeAdapter<Any>? {
        return if (type?.rawType == String::class.java) {
            StringAdapter() as TypeAdapter<Any>
        } else {
            null
        }
    }

    class StringAdapter : TypeAdapter<String>() {
        override fun read(reader: JsonReader?): String {
            return if (reader?.peek() == JsonToken.NULL) {
                reader.nextNull()
                ""
            } else {
                reader?.nextString() ?: ""
            }
        }

        override fun write(writer: JsonWriter?, value: String?) {
            writer?.value(value)
        }
    }
}