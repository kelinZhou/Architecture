package com.kelin.architecture.core.proxy


/**
 * **描述:** 声明可以被解绑的特性。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/9  9:44 PM
 *
 * **版本:** v 1.0.0
 */
interface UnBounder {

    /**
     * 与拥有者解除绑定关系。
     * @param thorough 是否是彻底解除绑定，如果为true这表示永远跟拥有者断绝关系，将会把所有曾经绑定过的通通销毁。
     */
    fun unbind(thorough: Boolean = true)
}