package com.kelin.architecture.data.apidata

import com.google.gson.annotations.SerializedName

/**
 * **描述:** 服务端返回数据的Code的处理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 17:17
 *
 * **版本:** v 1.0.0
 */
open class ApiResponseCode {

    companion object{
        const val API_CODE_OK = 0
    }

    @SerializedName("code")
    var code: Int = 0
    @SerializedName("message")
    var message: String = ""
}