package com.kelin.architecture.data.core

import com.kelin.architecture.data.util.LogHelper.Companion.system
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.*
import java.util.concurrent.TimeUnit

class HttpLogInterceptor : Interceptor {

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        private fun isPlaintext(buffer: Buffer): Boolean {
            return try {
                val prefix = Buffer()
                val byteCount = if (buffer.size() < 64) buffer.size() else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                true
            } catch (e: EOFException) {
                false
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val logList = ArrayList<String>()
        val request = chain.request()
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        var requestStartMessage = String.format("【%s】 【%s】", request.method(), request.url())
        if (hasRequestBody) {
            requestStartMessage += String.format(" ( %d -byte body", requestBody!!.contentLength())
        }
        logList.add(requestStartMessage)
        if (hasRequestBody) {
            val sb = StringBuilder()
            if (requestBody!!.contentType() != null) {
                sb.append("【Content-Type】：" + requestBody.contentType())
            }
            if (requestBody.contentLength() != -1L) {
                sb.append(" 【Content-Length】：" + requestBody.contentLength())
            }
            logList.add(sb.toString())
        }
        val headers = request.headers()
        initHeaders(logList, headers)
        if (hasRequestBody && !bodyEncoded(request.headers())) {
            if (requestBody is MultipartBody) {
                val sb = StringBuilder()
                for (part in requestBody.parts()) {
                    val requestCount = part.headers()!!.size()
                    if (requestCount > 0) {
                        for (i in 0 until requestCount) {
                            val name = part.headers()!!.name(i)
                            sb.append(String.format("【%s = %s】", name, part.headers()!!.value(i)))
                            sb.append("\n")
                        }
                    }
                    if (part.body().contentType() != null && (part.body().contentType().toString() == "video/mp4" || part.body().contentType().toString() == "image/jpeg")) {
                        sb.append("*******************************************************************\n")
                        sb.append("***************这里是文件的数据，省略输出**************************\n")
                        sb.append("*******************************************************************\n\n")
                    } else {
                        sb.append(String.format("【body = %s】", formatBodyToString(part.body())))
                    }
                    sb.append("\n\n")
                }
                logList.add("【Request Body】:$sb")
            } else {
                logList.add("【Request Body】:" + formatBodyToString(requestBody))
            }
        }
        logList.add(String.format("***************** END 【%s】Request ************************************************\n\n", request.method()))
        logList.add(" ")
        val startNs = System.nanoTime()
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            logList.add("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        logList.add(String.format(Locale.CHINA, "【code = %d】,【url = %s】,【%dms】", response.code(), response.request().url(), tookMs))
        val responseHeaders = response.headers()
        val responseHeaderSize = responseHeaders.size()
        if (responseHeaderSize > 0) {
            val sb = StringBuilder()
            sb.append("ResponseHeaders: ")
            for (i in 0 until responseHeaderSize) {
                logList.add(String.format("【%s = %s】,", responseHeaders.name(i), responseHeaders.value(i)))
            }
        }
        if (!HttpHeaders.hasBody(response)) {
            logList.add("<-- END HTTP Without Body")
        } else if (bodyEncoded(response.headers())) {
            logList.add("<-- END HTTP (encoded body omitted)")
        } else {
            if (contentLength > 1024 * 1024 * 10) {
                throw IOException("文件过大")
            }
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = try {
                    contentType.charset(UTF8)
                } catch (e: UnsupportedCharsetException) {
                    logList.add("Couldn't decode the response body; charset is likely malformed.")
                    logList.add("<-- END HTTP")
                    return response
                }
            }
            if (!isPlaintext(buffer)) {
                logList.add("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)")
                return response
            }
            if (contentLength != 0L) {
                logList.add("↓↓↓↓↓↓【Response Data】↓↓↓↓↓")
                logList.add(buffer.clone().readString(charset))
            }
            logList.add("<-- END HTTP (" + buffer.size() + " -byte body)")
        }
        system.i(logList.toString())
        return response
    }

    private fun formatBodyToString(requestBody: RequestBody?): String {
        var result = ""
        try {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)
            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            if (isPlaintext(buffer)) {
                result = buffer.readString(charset)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            result = "解析错误"
        }
        return result
    }

    private fun initHeaders(logList: ArrayList<String>, headers: Headers) {
        val requestCount = headers.size()
        if (requestCount > 0) {
            for (i in 0 until requestCount) {
                val name = headers.name(i)
                logList.add(String.format("【%s = %s】", name, headers.value(i)))
            }
        }
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }
}