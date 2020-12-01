package com.kelin.architecture.domain.croe.exception

/**
 * Created by neuifo on 16/9/9.
 * 用于处理Http的错误
 */
class ApiException(val errCode: Int, private val msg: String? = null, throwable: Throwable? = null) : Exception(msg
        ?: throwable?.message) {

    companion object{
        val createFileException = ApiException(999, "创建文件失败")
        val ioException = ApiException(999, "保存失败")
    }


    constructor(errCode: Int, throwable: Throwable) : this(errCode, throwable.message, throwable)

    constructor(errCode: Int, msg: String) : this(errCode, msg, null)

    constructor(error: Error) : this(error.code, error.msg)

    constructor(error: Error, throwable: Throwable) : this(error.code, error.msg, throwable)

    val displayMessage: String
        get() = toString()

    override fun toString() = msg ?: "未知错误"

    fun isHttpPermissionError(): Boolean {
        return errCode == Error.PERMISSION_ERROR.code
    }

    fun isServerError(): Boolean {
        return errCode == Error.SERVICE_ERROR.code || errCode == Error.DEADLINE_EXCEEDED.code
    }

    fun isHttpRequestError(httpCode: Int): Boolean {
        return httpCode != 0
    }

    fun isNetworkError(): Boolean {
        return errCode == Error.NETWORK_ERROR.code || errCode == Error.NETWORK_UNAVAILABLE.code
    }
    /**
     * 1001 比较特殊，是专门用来捕获没有预料的异常的，由于没有预料到所以这里叫未知异常。
     *
     * 所有7开头的错误都是在检测到用户有一些不正常的操作时给出的友好提示。
     *
     * 所有8开头的异常都属于自定义异常，报错后端定义的自定义异常。
     *
     * 所有9开头的异常都属于服务器端的异常，通常情况下这些异常都是因为后端的代码错误才导致的。
     *
     * 所有6开头的错误都属于代码错误，是可以避免的。这些错误都是提醒给程序员看的。
     */
    enum class Error(val code: Int, val msg: String) {
        /**
         * 所有未知的错误。
         */
        UNKNOWN_ERROR(1001, "未知错误"),
        /**
         * 在本来需要体统默认值的地方没有提默认值时就会抛出改异常。
         */
        NO_DEFAULT_VALUE_ERROR(6001, "无默认返回值错误"),
        /**
         * 当用户进行某项操作失败次数过多是抛出改异常。
         */
        FAIL_TOO_MUCH(7001, "错误次数太多，请稍后再试"),
        /**
         * 当用户进行某项操作过于频繁时或者服务器压力过大时抛出该异常。
         */
        TOO_BUSY(7002, "您的操作太频繁了，请稍后再试"),
        /**
         * 网络不可用。
         */
        NETWORK_UNAVAILABLE(8001, "网络不可用"),
        /**
         * 网络错误。
         */
        NETWORK_ERROR(8002, "网络不可用"),
        /**
         * 服务器异常
         */
        SERVICE_ERROR(9001, "服务器异常"),
        /**
         * 服务器无响应
         */
        DEADLINE_EXCEEDED(9002, "服务器无响应"),
        /**
         * 服务器无响应or网络不可用
         */
        DEADLINE_EXCEEDED_OR_NETWORK_UNAVAILABLE(9003, "服务器无响应或网络不可用"),
        /**
         * 数据解析异常。解析数据时没有获取到data或者result信息,获取解析JSON数据错误时都会抛出改异常。
         */
        RESULT_ERROR(9003, "数据错误"),
        /**
         * Token无效。
         */
        TOKEN_INVALID(9004, "您还未登录"),
        /**
         * 当用户没有权限调用某个api时抛出该异常。
         */
        PERMISSION_ERROR(403, "权限错误"),
        /**
         * 当调用后台API缺少必要参数是抛出该错误。
         */
        ARGUMENT_ERROR(405, "参数错误");
    }
}
