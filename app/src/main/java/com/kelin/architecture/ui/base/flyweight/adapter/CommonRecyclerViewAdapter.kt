package com.kelin.architecture.ui.base.flyweight.adapter

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.kelin.architecture.ui.base.flyweight.callback.ItemHolderGenerator
import com.kelin.architecture.ui.base.flyweight.callback.OnItemEventListener

/**
 * **创建人:** 通用的支持DiffUtil的RecyclerView适配器。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/29  1:23 PM
 *
 * **版本:** v 1.0.0
 */
class CommonRecyclerViewAdapter<T, VH>(private val typeTranslator: ItemTypeTranslator<T>, holderGenerator: ItemHolderGenerator<out VH, T>, itemEventListener: OnItemEventListener<T>) : SuperRecyclerViewAdapter<T, VH>(itemEventListener) where VH : RecyclerView.ViewHolder, VH : SuperRecyclerViewAdapter.ItemViewHolderInterface<T> {

    init {
        super.holderGenerator = holderGenerator
    }

    override fun getItemType(position: Int): Int {
        return typeTranslator.getUiItemType(position, getItem(position))
    }

    /**
     * 创建ViewHolder的事情交给了[ItemHolderGenerator]去办，所以这里不会被至执行。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, itemEventListener: OnItemEventListener<T>?): VH {
        throw RuntimeException("you must set the holderGenerator or overwrite \"onCreateViewHolder\" method!")
    }

    interface ItemTypeTranslator<T> {
        @LayoutRes
        fun getUiItemType(position: Int, item: T): Int
    }
}
