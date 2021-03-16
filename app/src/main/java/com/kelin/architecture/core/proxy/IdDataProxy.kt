package com.kelin.architecture.core.proxy

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.kelin.architecture.domain.croe.exception.ApiException
import com.kelin.architecture.core.proxy.usecase.UseCase
import com.kelin.architecture.util.ToastUtil
import io.reactivex.disposables.Disposable

/**
 * **描述:** 有请求参数的网络请求代理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/9  9:44 PM
 *
 * **版本:** v 1.0.0
 */
abstract class IdDataProxy<ID, D> : IdActionDataProxy<ID, D>() {

    private val defaultAction = ActionParameter.createInstance()

    override fun setNotToast(): IdDataProxy<ID, D> {
        super.setNotToast()
        return this
    }

    final override fun createUseCase(id: ID, action: ActionParameter): UseCase<D> {
        return createUseCase(id)
    }

    abstract fun createUseCase(id: ID): UseCase<D>

    final override fun checkNetworkEnable(id: ID, action: ActionParameter): Boolean {
        return checkNetworkEnable(action)
    }

    protected open fun checkNetworkEnable(action: ActionParameter): Boolean = true

    fun request(id: ID) {
        super.request(defaultAction, id)
    }

    /**
     * 将Proxy于声明周期绑定，由于绑定后将会减少垃圾的产生，所以通常情况下建议绑定。
     * @param owner 声明周期拥有者，通常是Activity或Fragment。
     * @param callBack 异步回调。
     */
    fun bind(owner: LifecycleOwner, callBack: IdDataCallback<ID, D>): IdDataProxy<ID, D> {
        super.bind(owner, callBack)
        return this
    }

    /**
     * 将Proxy于声明周期绑定，由于绑定后将会减少垃圾的产生，所以通常情况下建议绑定。
     * @param owner 声明周期拥有者，通常是Activity或Fragment。
     */
    fun bind(owner: LifecycleOwner): IdDataProxy<ID, D> {
        bind(owner, InnerCallback())
        return this
    }

    /**
     * 显示加载进度弹窗(loading弹窗)。
     * @param context 可以显示Dialog的Context。
     */
    override fun progress(context: Context): IdDataProxy<ID, D> {
        super.progress(context)
        return this
    }

    /**
     * 设置请求成功的回调。
     * @param onSuccess 如果在这之前调用过bind方法则会一直有效，直到页面销毁，否则该回调需要每次请求时都设置。
     */
    fun onSuccess(onSuccess: (id: ID, data: D) -> Unit): IdDataProxy<ID, D> {
        if (mGlobalCallback != null && mGlobalCallback is InnerCallback) {
            (mGlobalCallback as InnerCallback).success = onSuccess
        } else {
            mGlobalCallback = SingleCallback()
            (mGlobalCallback as SingleCallback).success = onSuccess
        }
        return this
    }

    /**
     * 设置请求失败的回调。
     * @param onFailed 如果在这之前调用过bind方法则会一直有效，直到页面销毁，否则该回调需要每次请求时都设置。
     */
    fun onFailed(onFailed: (id: ID, e: ApiException) -> Unit): IdDataProxy<ID, D> {
        if (mGlobalCallback != null && mGlobalCallback is InnerCallback) {
            (mGlobalCallback as InnerCallback).failed = onFailed
        } else {
            mGlobalCallback = SingleCallback()
            (mGlobalCallback as SingleCallback).failed = onFailed
        }
        return this
    }

    private open inner class InnerCallback : IdActionDataCallback<ID, ActionParameter, D> {

        var success: ((id: ID, data: D) -> Unit)? = null

        var failed: ((id: ID, e: ApiException) -> Unit)? = null

        override fun onSuccess(id: ID, action: ActionParameter, data: D) {
            success?.invoke(id, data)
        }

        override fun onFailed(id: ID, action: ActionParameter, e: ApiException) {
            if (failed != null) {
                failed?.invoke(id, e)
            } else {
                ToastUtil.showShortToast(e.displayMessage)
            }
        }
    }

    private inner class SingleCallback : InnerCallback() {
        override fun onSuccess(id: ID, action: ActionParameter, data: D) {
            super.onSuccess(id, action, data)
            unbind()
        }

        override fun onFailed(id: ID, action: ActionParameter, e: ApiException) {
            super.onFailed(id, action, e)
            unbind()
        }
    }

    abstract class IdDataCallback<ID, D> : IdActionDataCallback<ID, ActionParameter, D> {
        final override fun onSuccess(id: ID, action: ActionParameter, data: D) {
            onSuccess(id, data)
        }

        abstract fun onSuccess(id: ID, data: D)

        final override fun onFailed(id: ID, action: ActionParameter, e: ApiException) {
            onFailed(id, e)
        }

        abstract fun onFailed(id: ID, e: ApiException)
    }
}