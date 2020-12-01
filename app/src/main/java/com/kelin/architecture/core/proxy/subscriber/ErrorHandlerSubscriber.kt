package com.kelin.architecture.core.proxy.subscriber

import com.kelin.architecture.EnvConfig
import com.kelin.architecture.domain.croe.exception.ApiException

/**
 * **描述:** 错误处理的的观察者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/9  11:01 AM
 *
 * **版本:** v 1.0.0
 */
abstract class ErrorHandlerSubscriber<T> : UseCaseSubscriber<T>() {//api错误处理的观察者

    protected open fun onFinished(successful: Boolean){
        if (isDisposed) {
            dispose()
        }
    }

    protected abstract fun onError(ex: ApiException)

    protected abstract fun onSuccess(t: T)


    final override fun onError(e: Throwable) {
        onError(e, true)
    }

    private fun onError(e: Throwable, printError: Boolean) {
        try {
            if (printError) {
                printError(e, "ErrorHandlerSubscribe")
            }
            dealError(e)
        } catch (e: Exception) {
            try {
                if (printError) {
                    printError(e, "onError error")
                }
                onFinished(false)
            } catch (finishEx: Exception) {
                if (printError) {
                    printError(e, "onFinished")
                }
            }
        }
    }

    protected open fun dealError(e: Throwable) {
        onError(ApiException(ApiException.Error.UNKNOWN_ERROR, e))
    }

    protected fun printError(e: Throwable, tip: String) {
        if (EnvConfig.IS_DEBUG) {
            e.printStackTrace()
            print("ErrorHandlerSubscriber===============$tip===============")
            print("ErrorHandlerSubscriber ${e.localizedMessage}")
        }
    }

    override fun onNext(t: T) {
        try {
            onSuccess(t)
        } catch (e: Exception) {
            printError(e, "onNext error")
            onError(e, false)
        }
    }

    override fun onComplete() {
        try {
            onFinished(true)
        } catch (e: Exception) {
            printError(e, "onFinished error")
            onError(e, false)
        }
    }
}