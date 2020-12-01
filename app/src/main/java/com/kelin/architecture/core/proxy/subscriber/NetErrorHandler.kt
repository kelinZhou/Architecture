package com.kelin.architecture.core.proxy.subscriber

import android.net.ParseException
import com.kelin.architecture.core.AppModule
import com.kelin.architecture.domain.croe.exception.ApiException
import com.kelin.architecture.tools.AppLayerErrorCatcher
import org.json.JSONException
import java.io.IOException
import java.net.UnknownHostException

/**
 * **描述:** 网络错误的处理器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/9  11:01 AM
 *
 * **版本:** v 1.0.0
 */
abstract class NetErrorHandler<T> : ErrorHandlerSubscriber<T>() {

    override fun dealError(e: Throwable) {
        if (e is ApiException) {
            if (e.isHttpPermissionError()) {
                handlePermissionError(e)
            } else {
                handleCustomError(e)
            }
        } else if (e is UnknownHostException || "GaiException" == e.javaClass.simpleName) {
            handleNetworkError(ApiException(ApiException.Error.NETWORK_UNAVAILABLE))
        } else if (e.javaClass.simpleName == "JsonParseException" || e is JSONException || e is ParseException) {
            handleDataError(ApiException(ApiException.Error.RESULT_ERROR, e))
            printError(e, "ParseException Exception")
        } else if ("ConnectException" == e.javaClass.simpleName) {
            handleDataError(ApiException(ApiException.Error.DEADLINE_EXCEEDED_OR_NETWORK_UNAVAILABLE, e))
            printError(e, "ConnectException Exception")
        } else if (e is IOException) {
            // 均视为网络错误
            handleNetworkError(ApiException(ApiException.Error.RESULT_ERROR, e))
            printError(e, "IOException Exception")
        } else {
            // 未知错误
            handleUnknownError(ApiException(ApiException.Error.UNKNOWN_ERROR.code, e.message, e))
            printError(e, "UNKNOWN Exception")
        }
        onFinished(false)
    }

    private fun handleCustomError(ex: ApiException) {
        try {
            onError(ex)
        } catch (e: Exception) {
            printError(e, "handleCustomError error")
            AppLayerErrorCatcher.throwException(e)
        }
    }

    /**
     * 服务器返回数据的错误(无法解析)
     */
    protected open fun handleDataError(ex: ApiException) {
        try {
            onError(ex)
        } catch (e: Exception) {
            printError(e, "handleDataError error")
            AppLayerErrorCatcher.throwException(e)
        }
    }


    /**
     * http权限错误，需要实现重新登录操作
     */
    protected open fun handlePermissionError(ex: ApiException) {
        try {
//            onError(ex)
            AppModule.logOut()
        } catch (e: Exception) {
            printError(e, "handlePermissionError")
            AppLayerErrorCatcher.throwException(e)
        }
    }

    protected open fun handleArgumentError(ex: ApiException) {
        try {
            onError(ex)
        } catch (e: Exception) {
            AppLayerErrorCatcher.throwException(e)
        }
    }


    protected open fun handleNetworkError(ex: ApiException) {
        try {
            onError(ex)
        } catch (e: Exception) {
            printError(e, "handleNetworkError error")
            AppLayerErrorCatcher.throwException(e)
        }
    }

    protected open fun handleUnknownError(ex: ApiException) {
        try {
            onError(ex)
        } catch (e: Exception) {
            printError(e, "handleUnknownError error")
            AppLayerErrorCatcher.throwException(e)
        }
    }
}
