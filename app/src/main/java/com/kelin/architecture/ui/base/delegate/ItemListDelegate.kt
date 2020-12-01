package com.kelin.architecture.ui.base.delegate

import android.util.SparseArray
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.kelin.architecture.ui.base.flyweight.callback.OnItemEventListener
import com.kelin.architecture.ui.base.flyweight.viewholder.AbsItemViewHolder
import com.kelin.architecture.ui.base.listcell.Cell

/**
 * **描述: ** 以Cell为UI List的列表
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/4/28  下午5:43
 *
 * **版本: ** v 1.0.0
 */

abstract class ItemListDelegate<VC : ItemListDelegate.ItemListDelegateCallback<I>, I : Cell> :
        CommonListWrapperDelegate<VC, I, List<I>>() {

    private val itemPool = SparseArray<I>()

    protected val cellList: MutableList<I>?
        get() = adapterOrNull?.dataList

    final override fun getUiItemType(position: Int, item: I): Int {
        val type = item.itemLayoutRes
        itemPool.put(type, item)
        return type
    }

    protected open fun onItemModelCreated(itemModel: I, layoutRes: Int) {

    }

    protected fun getItemCellByType(viewType:Int): I = itemPool.get(viewType)

    override fun createHolder(parent: ViewGroup, viewType: Int, l: OnItemEventListener<I>?): AbsItemViewHolder<I> {
        val cell = getItemCellByType(viewType)
        return CommonItemViewHolder(parent, viewType, cell.clickableIds, l, cell)
    }

    override fun extractInitialDataList(initialData: List<I>): Collection<I> {
        return initialData
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
            this@ItemListDelegate.onItemModelCreated(itemModel, layoutRes)
        }

        override fun onBindView(item: I) {
            itemModel = item
            item.bindData(this)
        }

        override fun onViewAttachedToWindow() {
            val dataList = adapter.dataList
            if (layoutPosition >= 0 && layoutPosition <= dataList.lastIndex) {
                itemModel.onViewAttachedToWindow(this, layoutPosition)
            }
        }

        override fun onViewDetachedFromWindow() {
            val dataList = adapterOrNull?.dataList
            if (dataList != null && layoutPosition >= 0 && layoutPosition <= dataList.lastIndex) {
                itemModel.onViewDetachedFromWindow(this, layoutPosition)
            }
        }
    }

    interface ItemListDelegateCallback<I : Cell> : CommonListDelegateCallback<I>
}
