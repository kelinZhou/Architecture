package com.kelin.architecture.ui.base.presenter

import android.view.View
import android.view.WindowManager
import com.kelin.architecture.ui.base.delegate.ItemListDelegate
import com.kelin.architecture.ui.base.listcell.Cell

import java.util.ArrayList

/**
 * **描述: ** 以ItemModel为UI Model的列表。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/4/28  下午5:41
 *
 * **版本: ** v 1.0.0
 */

abstract class ItemListFragmentPresenter<V : ItemListDelegate<VC, I>, VC : ItemListDelegate.ItemListDelegateCallback<I>, ID, I : Cell, W> :
    CommonListWrapperFragmentPresenter<V, VC, ID, I, W, List<I>>() {

    override val windowSoftInputModeFlags: Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN

    final override fun onListItemClick(itemView:View, position: Int, item: I) {
        if (!onInterceptListItemClick(itemView, position, item)) {
            item.onItemClick(requireActivity(), position)
        }
    }

    /**
     * 拦截条目点击事件，通常情况下各个Item的点击事件应当由Item自己处理，如果你希望自己处理而不让Item处理可以重写该方法并返回true。
     *
     * @param position 当前被点击的条目的layoutPosition。
     * @param item     当前被点击的Item。
     * @return 如果需要拦截不交给Item处理则返回true，否则返回false。
     */
    protected open fun onInterceptListItemClick(itemView:View, position: Int, item: I): Boolean {
        return false
    }

    final override fun onListItemLongClick(itemView:View, position: Int, item: I) {
        if (!onInterceptListItemLongClick(position, item)) {
            item.onItemLongClick(requireActivity(), position)
        }
    }

    protected open fun onInterceptListItemLongClick(position: Int, item: I): Boolean {
        return false
    }

    final override fun onListItemChildClick(itemView:View, position: Int, v: View, item: I) {
        if (!onInterceptListItemChildClick(position, v, item)) {
            item.onItemChildClick(requireActivity(), position, v)
        }
    }

    /**
     * 拦截条目子View的点击事件，通常情况下各个Item的点击事件应当由Item自己处理，如果你希望自己处理而不让Item处理可以重写该方法并返回true。
     * @param position 当前被点击的条目的layoutPosition。
     * @param v 当前被点击的View。
     * @param item     当前被点击的Item。
     * @return 如果需要拦截不交给Item处理则返回true，否则返回false。
     */
    protected open fun onInterceptListItemChildClick(position: Int, v: View, item: I): Boolean {
        return false
    }

    final override fun transformUIData(page: Int, data: W): List<I> {
        val itemList = ArrayList<I>()
        transformUIData(page, itemList, data)
        return itemList
    }

    protected abstract fun transformUIData(page: Int, itemList: MutableList<I>, data: W)

    open inner class ItemListDelegateCallbackImpl : CommonListDelegateCallbackImpl(), ItemListDelegate.ItemListDelegateCallback<I>
}
