package com.kelin.architecture.data.core

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer


/**
 * **描述:** 响应体转换器工厂。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 17:54
 *
 * **版本:** v 1.0.0
 */
class ResponseConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson = Gson()): Converter.Factory {
            return ResponseConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        return GsonResponseBodyConverter<Any>(gson, type)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody> {
        return GsonRequestBodyConverter(gson, gson.getAdapter(TypeToken.get(type)))
    }
}