package com.kelin.architecture.ui.base.flyweight.callback

import android.view.ViewGroup

/**
 * **描述:** ViewHolder的构建接口。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/4/8  10:06 AM
 *
 * **版本:** v 1.0.0
 */
interface ItemHolderGenerator<VH, T> {
    fun createHolder(parent: ViewGroup, viewType: Int, l: OnItemEventListener<T>?): VH
}