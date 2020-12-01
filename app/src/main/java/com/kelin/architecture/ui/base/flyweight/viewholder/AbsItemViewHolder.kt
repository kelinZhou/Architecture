package com.kelin.architecture.ui.base.flyweight.viewholder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelin.architecture.ui.base.flyweight.adapter.SuperRecyclerViewAdapter
import com.kelin.architecture.ui.base.flyweight.callback.OnItemEventListener


/**
 * **描述: ** ViewHolder的基类，所有的RecyclerView中的ViewHolder都应该继承该类。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2020/3/10  下午3:00
 *
 * **版本: ** v 1.0.0
 */
abstract class AbsItemViewHolder<T>(view: View, private var itemEventListener: OnItemEventListener<T>?, clickableIds: IntArray? = null, initEnable: Boolean = true) : BaseViewHolder(view, false),
    SuperRecyclerViewAdapter.ItemViewHolderInterface<T> {

    protected constructor(parent: ViewGroup, layoutRes: Int, itemEventListener: OnItemEventListener<T>?, clickableIds: IntArray? = null, initEnable: Boolean = true) : this(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false), itemEventListener, clickableIds, initEnable)

    init {
        if (initEnable) {
            initViewHolder(clickableIds)
        }
    }

    protected fun setItemEventListener(l: OnItemEventListener<T>) {
        itemEventListener = l
    }

    @Suppress("UNCHECKED_CAST")
    protected fun initViewHolder(clickableIds: IntArray?) {
        onCreateView()
        super.initViewHolder()
        if (itemClickable) {
            val clickableView =
                itemView.findViewById(itemClickableViewId) ?: itemView
            clickableView.setOnClickListener {
                if (adapterPosition >= 0) {
                    val data = itemView.getTag(KEY_ITEM_MODEL) as T
                    if (it === itemView || it.id == itemClickableViewId) {
                        itemEventListener?.onItemClick(itemView, adapterPosition, data)
                    } else {
                        itemEventListener?.onItemChildClick(itemView, it, adapterPosition, data)
                    }
                }
            }
            if (itemLongClickable) {
                clickableView.setOnLongClickListener {
                    val data = itemView.getTag(KEY_ITEM_MODEL) as T
                    itemEventListener?.onItemLongClick(itemView, adapterPosition, data)
                    true
                }
            }
        }
        bindEvent(clickableIds)
    }

    final override fun bindView(item: T) {
        itemView.setTag(KEY_ITEM_MODEL, item)
        onBindView(item)
    }

    override fun bindPartView(position: Int, item: T, payload: Bundle) {
        itemView.tag = item
        onBindPartView(position, item, payload)
    }

    protected open val itemClickable: Boolean
        get() = true

    protected open val itemLongClickable: Boolean
        get() = false

    @Suppress("UNCHECKED_CAST")
    private fun bindEvent(clickableIds: IntArray?) {
        clickableIds?.forEach { clickableId ->
            val v = itemView.findViewById<View>(clickableId)
            v?.setOnClickListener {
                if (adapterPosition >= 0) {
                    val data = itemView.getTag(KEY_ITEM_MODEL) as T
                    if (it === itemView || it.id == itemClickableViewId) {
                        itemEventListener?.onItemClick(itemView, adapterPosition, data)
                    } else {
                        itemEventListener?.onItemChildClick(itemView, it, adapterPosition, data)
                    }
                }
            }
        }
    }

    protected open fun onCreateView() {}


    protected abstract fun onBindView(item: T)

    protected open fun onBindPartView(position: Int, item: T, payload: Bundle) {
        onBindView(item)
    }

    override fun onViewAttachedToWindow() {}

    override fun onViewDetachedFromWindow() {}

    companion object {
        private const val KEY_ITEM_MODEL = 0x0f00_1001
    }
}
