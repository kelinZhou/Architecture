package com.kelin.architecture.ui.base.flyweight.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * **描述:** 好用的Adapter。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-10-15  11:36
 *
 * **版本:** v 1.0.0
 */
class FriendlyAdapter<ITEM>(@LayoutRes private val layoutRes: Int, private val viewBinder: (iv: View, item: ITEM, vh: RecyclerView.ViewHolder) -> Unit) : SimpleListAdapter<ITEM, FriendlyAdapter<ITEM>.FriendlyViewHolder<ITEM>>() {

    private var onItemClickListener: ((lastPosition: Int, position: Int, lastItem: ITEM, item: ITEM) -> Unit)? = null
    private var isSingleSelected = false
    var lastSelectedPosition = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FriendlyViewHolder<ITEM> {
        return FriendlyViewHolder(p0, layoutRes, viewBinder) { lastPosition, position, item ->
            onItemClickListener?.invoke(lastPosition, position, dataList[lastPosition], item)
        }
    }

    fun setOnItemClickListener(onItemClickListener: (lastPosition: Int, position: Int, lastItem: ITEM, item: ITEM) -> Unit): FriendlyAdapter<ITEM> {
        this.onItemClickListener = onItemClickListener
        return this
    }

    fun setSingleSelectMode(singleSelect: Boolean): FriendlyAdapter<ITEM> {
        isSingleSelected = singleSelect
        return this
    }

    inner class FriendlyViewHolder<ITEM>(parent: ViewGroup, @LayoutRes layoutRes: Int, private val viewBinder: (iv: View, item: ITEM, vh: RecyclerView.ViewHolder) -> Unit, val onItemClick: ((lastPosition: Int, position: Int, item: ITEM) -> Unit)? = null) : SimpleListAdapter.SimpleViewHolder<ITEM>(parent, layoutRes) {

        override val itemClickable: Boolean
            get() = onItemClickListener != null

        override val haveItemClickBg: Boolean
            get() = false

        override fun onBindView(item: ITEM) {
            viewBinder(itemView, item, this)
        }

        override fun onItemClick(itemView:View, position: Int, item: ITEM) {
            onItemClick?.invoke(lastSelectedPosition, position, item)
            if (lastSelectedPosition != -1) {
                notifyItemChanged(lastSelectedPosition)
            }
            lastSelectedPosition = position
            notifyItemChanged(position)
        }
    }
}