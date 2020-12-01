package com.kelin.architecture.domain.cache

/**
 * **描述:** 缓存
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 9:47 AM
 *
 * **版本:** v 1.0.0
 */
interface Cache<D> {

    /**
     * 获取当前的缓存。
     */
    fun get():D?
}