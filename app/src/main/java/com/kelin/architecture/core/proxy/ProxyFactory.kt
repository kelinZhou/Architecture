package com.kelin.architecture.core.proxy

import com.kelin.architecture.core.proxy.usecase.*
import io.reactivex.Observable
import java.lang.RuntimeException

/**
 * **描述:** Proxy的工厂类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/9  9:44 PM
 *
 * **版本:** v 1.0.0
 */
object ProxyFactory {

    /**
     * 创建一个请求代理（不支持分页）。
     *
     * @param caller api调用器，具体要调用那个api由调用器决定。
     */
    fun <ID, DATA> createIdActionProxy(caller: (id: ID) -> Observable<DATA>): IdActionDataProxy<ID, DATA> {
        return object : IdActionDataProxy<ID, DATA>() {
            override fun createUseCase(id: ID, action: ActionParameter): UseCase<DATA> {
                return ApiIdRequestUseCase(id, caller)
            }
        }
    }

    /**
     * 创建一个请求代理（不支持分页）。
     *
     * @param caller api调用器，具体要调用那个api由调用器决定。
     */
    fun <DATA> createActionProxy(caller: () -> Observable<DATA>): ActionDataProxy<DATA> {
        return object : ActionDataProxy<DATA>() {
            override fun createUseCase(action: ActionParameter): UseCase<DATA> {
                return ApiRequestUseCase(caller)
            }
        }
    }

    /**
     * 创建一个请求代理（不支持分页）。
     *
     * @param caller api调用器，具体要调用那个api由调用器决定。
     */
    fun <ID, DATA> createIdProxy(caller: (id: ID) -> Observable<DATA>): IdDataProxy<ID, DATA> {
        return object : IdDataProxy<ID, DATA>() {
            override fun createUseCase(id: ID): UseCase<DATA> {
                return ApiIdRequestUseCase(id, caller)
            }
        }
    }

    /**
     * 创建一个请求代理（不支持分页）。
     *
     * @param caller api调用器，具体要调用那个api由调用器决定。
     */
    fun <DATA> createProxy(caller: () -> Observable<DATA>): DataProxy<DATA> {
        return object : DataProxy<DATA>() {
            override fun createUseCase(): UseCase<DATA> {
                return ApiRequestUseCase(caller)
            }
        }
    }

    /**
     * 创建一个请求代理（支持分页）。
     *
     * @param caller api调用器，具体要调用那个api由调用器决定。
     */
    fun <ID, DATA> createPageIdActionProxy(caller: (id: ID, pages: PageActionParameter.Pages) -> Observable<DATA>): IdActionDataProxy<ID, DATA> {
        return object : IdActionDataProxy<ID, DATA>() {
            override fun createUseCase(id: ID, action: ActionParameter): UseCase<DATA> {
                //这里改用子类集成IdActionDataProxy
                return if (action is PageActionParameter && action.pages != null) {
                    PageApiIdRequestUseCase(id, action.pages, caller)
                } else throw RuntimeException("Action Parameter Error!")
            }
        }
    }

    /**
     * 创建一个请求代理（支持分页）。
     *
     * @param caller api调用器，具体要调用那个api由调用器决定。
     */
    fun <DATA> createPageActionProxy(caller: (pages: PageActionParameter.Pages) -> Observable<DATA>): ActionDataProxy<DATA> {
        return object : ActionDataProxy<DATA>() {
            override fun createUseCase(action: ActionParameter): UseCase<DATA> {
                //这里改用子类集成ActionDataProxy
                return if (action is PageActionParameter && action.pages != null) {
                    PageApiRequestUseCase(action.pages, caller)
                } else throw RuntimeException("Action Parameter Error!")
            }
        }
    }
}