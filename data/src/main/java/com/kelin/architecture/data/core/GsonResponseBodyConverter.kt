package com.kelin.architecture.data.core


import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.kelin.architecture.data.apidata.ApiResponseCode
import com.kelin.architecture.data.util.LogHelper
import com.kelin.architecture.domain.croe.exception.ApiException

import java.io.IOException
import java.lang.reflect.Type

import okhttp3.ResponseBody
import retrofit2.Converter


internal class GsonResponseBodyConverter<T>(private val gson: Gson, private val type: Type) : Converter<ResponseBody, T> {

    /**
     * 这里只能处理http httpStatus code为200的情形
     */
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        try {
            val responseCode = gson.fromJson(response, ApiResponseCode::class.java)
            if (statusOk(responseCode)) {
                return gson.fromJson<T>(response, type)
                        ?: throw JsonSyntaxException("数据解析错误:[response: $response]")
            } else {
                throw ApiException(responseCode.code, responseCode.message)
            }
        } catch (e: Exception) {
            LogHelper.system.e("GsonResponseBodyConverter", "================CONVERT Exception START======================")
            LogHelper.system.e("GsonResponseBodyConverter", "response:\n$response")
            LogHelper.system.e("GsonResponseBodyConverter", "Converter Error", e)
            LogHelper.system.e("GsonResponseBodyConverter", "================CONVERT Exception END======================")
            throw if (e is ApiException) {
                e
            } else {
                JsonParseException(e.message, e)
            }
        } finally {
            value.close()
        }
    }


    private fun statusOk(httpResponseCode: ApiResponseCode): Boolean {
        return httpResponseCode.code == ApiResponseCode.API_CODE_OK
    }
}