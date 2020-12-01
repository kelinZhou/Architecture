package com.kelin.architecture.core.proxy

import com.kelin.architecture.core.proxy.usecase.UseCase
import com.kelin.architecture.domain.croe.exception.ApiException
import io.reactivex.disposables.Disposable

/**
 * **描述:** 有请求参数的网络请求代理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/4/1  1:42 PM
 *
 * **版本:** v 1.0.0
 */
abstract class DataProxy<D> : IdActionDataProxy<Any, D>() {

    private val defaultAction = ActionParameter.createInstance()
    private val defaultId = Any()

    override fun setNotToast(): DataProxy<D> {
        super.setNotToast()
        return this
    }

    final override fun createUseCase(id: Any, action: ActionParameter): UseCase<D> {
        return createUseCase()
    }

    abstract fun createUseCase(): UseCase<D>

    final override fun checkNetworkEnable(id: Any, action: ActionParameter): Boolean {
        return checkNetworkEnable()
    }

    protected open fun checkNetworkEnable(): Boolean = true

    fun request(): Disposable? {
        return super.request(defaultAction, defaultId)
    }

    fun bind(owner: ProxyOwner, callBack: DataCallback<D>): DataProxy<D> {
        super.bind(owner, callBack)
        return this
    }

    fun bind(owner: ProxyOwner): DataProxy<D> {
        bind(owner, InnerCallback())
        return this
    }

    fun onSuccess(onSuccess: (data: D) -> Unit): DataProxy<D> {
        if (mGlobalCallback != null && mGlobalCallback is InnerCallback) {
            (mGlobalCallback as InnerCallback).success = onSuccess
        } else {
            mGlobalCallback = SingleCallback()
            (mGlobalCallback as SingleCallback).success = onSuccess
        }
        return this
    }

    fun onFailed(onFailed: (e: ApiException) -> Unit): DataProxy<D> {
        if (mGlobalCallback != null && mGlobalCallback is InnerCallback) {
            (mGlobalCallback as InnerCallback).failed = onFailed
        } else {
            mGlobalCallback = SingleCallback()
            (mGlobalCallback as SingleCallback).failed = onFailed
        }
        return this
    }

    private open inner class InnerCallback : IdActionDataCallback<Any, ActionParameter, D> {

        var success: ((data: D) -> Unit)? = null

        var failed: ((e: ApiException) -> Unit)? = null

        override fun onSuccess(id: Any, action: ActionParameter, data: D) {
            success?.invoke(data)
        }

        override fun onFailed(id: Any, action: ActionParameter, e: ApiException) {
            failed?.invoke(e)
        }
    }

    private inner class SingleCallback : InnerCallback() {
        override fun onSuccess(id: Any, action: ActionParameter, data: D) {
            super.onSuccess(id, action, data)
            unbind()
        }

        override fun onFailed(id: Any, action: ActionParameter, e: ApiException) {
            super.onFailed(id, action, e)
            unbind()
        }
    }

    abstract class DataCallback<D> : IdActionDataCallback<Any, ActionParameter, D> {
        final override fun onSuccess(id: Any, action: ActionParameter, data: D) {
            onSuccess(data)
        }

        abstract fun onSuccess(data: D)

        final override fun onFailed(id: Any, action: ActionParameter, e: ApiException) {
            onFailed(e)
        }

        abstract fun onFailed(e: ApiException)
    }
}