package com.kelin.architecture.data.core

/**
 * **描述:** 用来描述Http请求的返回结果。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 17:15
 *
 * **版本:** v 1.0.0
 */
class HttpResult<T> : ApiResponseCode() {
    val data: T? = null
}