package com.kelin.architecture.core.proxy

/**
 * **描述:** 定义Proxy的拥有者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/9  9:44 PM
 *
 * **版本:** v 1.0.0
 */
interface ProxyOwner {
    fun attachToOwner(proxy: UnBounder)
}