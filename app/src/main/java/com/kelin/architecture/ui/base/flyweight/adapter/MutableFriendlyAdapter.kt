package com.kelin.architecture.ui.base.flyweight.adapter

import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * **描述:** 好用的Adapter。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-10-15  11:36
 *
 * **版本:** v 1.0.0
 */
class MutableFriendlyAdapter<VH: MutableFriendlyAdapter.ViewHolder<Any>>(private val holderGenerator: (item: Any, position: Int, ViewGroup) -> VH) : SimpleListAdapter<Any, VH>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VH {
        return holderGenerator(getItem(p1)!!, p1, p0)
    }

    abstract class ViewHolder<ITEM>(parent: ViewGroup, @LayoutRes layoutRes: Int) : SimpleListAdapter.SimpleViewHolder<ITEM>(parent, layoutRes) {

        override val itemClickable: Boolean
            get() = false

        override val haveItemClickBg: Boolean
            get() = false
    }
}