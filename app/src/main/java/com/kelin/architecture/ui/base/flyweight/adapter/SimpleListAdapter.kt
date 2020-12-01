package com.kelin.architecture.ui.base.flyweight.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kelin.architecture.ui.base.flyweight.callback.OnItemEventListener
import com.kelin.architecture.ui.base.flyweight.viewholder.AbsItemViewHolder
import com.kelin.architecture.util.LogHelper

import java.util.ArrayList

/**
 * **描述: ** 简单的RecyclerView的适配器。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2017/12/14  上午10:56
 *
 * **版本: ** v 1.0.0
 */

abstract class SimpleListAdapter<ITEM, VH : SimpleListAdapter.SimpleViewHolder<ITEM>> constructor(private var mDataList: List<ITEM>? = null) :
        RecyclerView.Adapter<VH>() {

    var dataList: List<ITEM>
        get() {
            if (mDataList == null) {
                mDataList = ArrayList()
            }
            return mDataList!!
        }
        set(dataList) {
            this.mDataList = dataList
        }

    val isMutableList: Boolean
        get() = dataList is MutableList

    @JvmOverloads
    fun addItem(item: ITEM?, refresh: Boolean = true) {
        val list = dataList
        if (item != null && list is MutableList && list.add(item) && refresh) {
            notifyItemInserted(dataList.size)
        }
    }

    fun getItem(position: Int): ITEM? {
        return if (dataList.isEmpty() || position < 0 || position > dataList.lastIndex) null else dataList[position]
    }

    @JvmOverloads
    fun removeItem(position: Int, refresh: Boolean = true): ITEM? {
        val list = dataList
        return if (list is MutableList) {
            val item = if (position < 0 || position > list.lastIndex) null else list.removeAt(position)
            if (item != null && refresh) {
                notifyItemRemoved(position)
            }
            item
        } else {
            printDataListError()
            null
        }
    }

    private fun printDataListError() {
        LogHelper.system.i("---------->modify dataList failed, because the dataList is not MutableList.")
    }

    @JvmOverloads
    fun addAll(list: Collection<ITEM>?, refresh: Boolean = true) {
        if (list != null) {
            if (dataList is MutableList) {
                if ((dataList as MutableList<ITEM>).addAll(list) && refresh) {
                    notifyDataSetChanged()
                }
            } else {
                printDataListError()
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bindView(item)
    }

    override fun getItemCount(): Int {
        return if (mDataList == null) 0 else mDataList!!.size
    }

    abstract class SimpleViewHolder<ITEM> : AbsItemViewHolder<ITEM>, OnItemEventListener<ITEM> {
        constructor(parent: ViewGroup, layoutRes: Int) : super(parent, layoutRes, null, null, false) {
            setItemEventListener(this)
            initViewHolder(null)
        }

        constructor(itemView: View) : super(itemView, null, null, false) {
            setItemEventListener(this)
            initViewHolder(null)
        }

        override fun onBindView(item: ITEM) {

        }

        @Suppress("UNCHECKED_CAST")
        protected fun <V : View> getItemView(): V {
            return itemView as V
        }

        override fun onItemClick(itemView:View, position: Int, data: ITEM) {

        }

        override fun onItemLongClick(itemView:View, position: Int, data: ITEM) {

        }

        override fun onItemChildClick(itemView:View, v: View, position: Int, data: ITEM) {

        }
    }
}
