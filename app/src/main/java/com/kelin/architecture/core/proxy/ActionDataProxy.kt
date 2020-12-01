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
abstract class ActionDataProxy<D> : IdActionDataProxy<Any, D>() {

    private val defaultRequestId = Any()

    override fun setNotToast(): ActionDataProxy<D> {
        super.setNotToast()
        return this
    }

    final override fun createUseCase(id: Any, action: ActionParameter): UseCase<D> {
        return createUseCase(action)
    }

    abstract fun createUseCase(action: ActionParameter): UseCase<D>

    final override fun checkNetworkEnable(id: Any, action: ActionParameter): Boolean {
        return checkNetworkEnable(action)
    }

    protected open fun checkNetworkEnable(action: ActionParameter): Boolean = true

    fun request(action: ActionParameter): Disposable? {
        return super.request(action, defaultRequestId)
    }

    fun bind(owner: ProxyOwner, callBack: ActionDataCallback<ActionParameter, D>): ActionDataProxy<D> {
        super.bind(owner, callBack)
        return this
    }

    fun bind(owner: ProxyOwner): ActionDataProxy<D> {
        bind(owner, InnerCallback())
        return this
    }

    fun onSuccess(onSuccess: (data: D) -> Unit): ActionDataProxy<D> {
        if (mGlobalCallback != null && mGlobalCallback is InnerCallback) {
            (mGlobalCallback as InnerCallback).success = onSuccess
        } else {
            mGlobalCallback = SingleCallback()
            (mGlobalCallback as SingleCallback).success = onSuccess
        }
        return this
    }

    fun onFailed(onFailed: (e: ApiException) -> Unit): ActionDataProxy<D> {
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

    abstract class ActionDataCallback<ACTION : ActionParameter, D> : IdActionDataCallback<Any, ACTION, D> {
        final override fun onSuccess(id: Any, action: ACTION, data: D) {
            onSuccess(action, data)
        }

        abstract fun onSuccess(action: ACTION, data: D)

        final override fun onFailed(id: Any, action: ACTION, e: ApiException) {
            onFailed(action, e)
        }

        abstract fun onFailed(action: ACTION, e: ApiException)
    }
}