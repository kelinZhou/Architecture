package com.kelin.architecture.ui.base.delegate

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelin.architecture.R
import com.kelin.architecture.ui.base.flyweight.adapter.CommonRecyclerViewAdapter
import com.kelin.architecture.ui.base.flyweight.callback.ItemHolderGenerator
import com.kelin.architecture.ui.base.flyweight.callback.OnItemEventListener
import com.kelin.architecture.ui.base.flyweight.viewholder.AbsItemViewHolder
import com.kelin.architecture.ui.base.listcell.Cell
import com.kelin.architecture.ui.base.presenter.ViewPresenter


/**
 * **描述:** 普通的列表页面。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-25 13:57:10
 *
 * **版本:** v 1.0.0
 */
abstract class NormalItemListDelegate<VC : NormalItemListDelegate.NormalItemListDelegateCallback<I>, I : Cell> : BaseViewDelegate<VC>(), CommonRecyclerViewAdapter.ItemTypeTranslator<I>, ItemHolderGenerator<AbsItemViewHolder<I>, I> {

    private val itemPool = SparseArray<I>()

    protected val adapter by lazy { CommonRecyclerViewAdapter(this, this, CommonOnItemEventListenerImpl()) }

    final override fun getUiItemType(position: Int, item: I): Int {
        val type = item.itemLayoutRes
        itemPool.put(type, item)
        return type
    }

    override val rootLayoutId: Int
        get() = R.layout.common_list

    protected open val recyclerViewId: Int
        get() = R.id.list_view

    protected val recyclerView: RecyclerView?
    get() = containerView?.findViewById(recyclerViewId)

    override fun bindView(viewPresenter: ViewPresenter<VC>) {
        super.bindView(viewPresenter)
        val listView = containerView?.findViewById<RecyclerView>(recyclerViewId)
        listView?.layoutManager = onCreateLayoutManager()
        listView?.adapter = adapter
    }

    protected open fun onCreateLayoutManager() = LinearLayoutManager(context)

    open fun setInitData(cells: List<I>) {
        adapter.setData(cells, true)
    }

    open fun addItem(position: Int, cell: I, refresh: Boolean = true) {
        adapter.addItem(position, cell, refresh)
    }

    open fun addItem(cell: I, refresh: Boolean = true) {
        adapter.addItem(cell, refresh)
    }

    open fun removeItem(position: Int, refresh: Boolean = true) {
        adapter.removeItem(position, refresh)
    }

    open fun notifyItemChanged(position: Int) {
        adapter.notifyItemChanged(position)
    }

    override fun createHolder(parent: ViewGroup, viewType: Int, l: OnItemEventListener<I>?): AbsItemViewHolder<I> {
        val itemModel = itemPool.get(viewType)
        return CommonItemViewHolder(parent, viewType, itemModel.clickableIds, l, itemModel)
    }

    protected open fun onItemModelCreated(cell: I, layoutRes: Int) {
    }

    protected open fun onItemClick(position: Int, cell: I): Boolean {
        return false
    }

    protected open fun onItemLongClick(position: Int, cell: I): Boolean {
        return false
    }

    protected open fun onItemChildClick(position: Int, v: View, item: I): Boolean {
        return false
    }

    inner class CommonItemViewHolder internal constructor(parent: ViewGroup, layoutRes: Int, clickableIds: IntArray?, listener: OnItemEventListener<I>?, private var itemModel: I) : AbsItemViewHolder<I>(parent, layoutRes, listener, clickableIds, false) {

        override val itemLongClickable: Boolean
            get() = itemModel.itemLongClickable

        override val itemClickable: Boolean
            get() = itemModel.itemClickable

        override val haveItemClickBg: Boolean
            get() = itemModel.haveItemClickBg

        @get:IdRes
        override val itemClickableViewId: Int
            get() = itemModel.itemClickableViewId

        @get:DrawableRes
        override val itemBackgroundResource: Int
            get() = itemModel.itemBackgroundResource

        init {
            initViewHolder(clickableIds)
            itemModel.onCreate(this)
            this@NormalItemListDelegate.onItemModelCreated(itemModel, layoutRes)
        }

        override fun onBindView(item: I) {
            itemModel = item
            item.bindData(this)
        }

        override fun onViewAttachedToWindow() {
            val dataList = adapter.dataList
            if (layoutPosition >= 0 && layoutPosition <= dataList.lastIndex) {
                dataList[layoutPosition].onViewAttachedToWindow(this, layoutPosition)
            }
        }

        override fun onViewDetachedFromWindow() {
            val dataList = adapter.dataList
            if (layoutPosition >= 0 && layoutPosition <= dataList.lastIndex) {
                dataList[layoutPosition].onViewDetachedFromWindow(this, layoutPosition)
            }
        }
    }

    internal inner class CommonOnItemEventListenerImpl : OnItemEventListener<I> {

        override fun onItemClick(itemView:View, position: Int, item: I) {
            if (!this@NormalItemListDelegate.onItemClick(position, item)) {
                viewCallback.onListItemClick(position, item)
            }
        }

        override fun onItemLongClick(itemView:View, position: Int, item: I) {
            if (!this@NormalItemListDelegate.onItemLongClick(position, item)) {
                viewCallback.onListItemLongClick(position, item)
            }
        }

        override fun onItemChildClick(itemView:View, v: View, position: Int, item: I) {
            if (!this@NormalItemListDelegate.onItemChildClick(position, v, item)) {
                viewCallback.onListItemChildClick(position, v, item)
            }
        }
    }

    interface NormalItemListDelegateCallback<I : Cell> : BaseViewDelegateCallback {
        fun onListItemClick(position: Int, item: I)
        fun onListItemLongClick(position: Int, item: I)
        fun onListItemChildClick(position: Int, v: View, item: I)
    }
}
