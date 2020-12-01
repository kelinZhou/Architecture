package com.kelin.architecture.ui.base.presenter

import android.view.View
import com.kelin.architecture.ui.base.delegate.NormalItemListDelegate
import com.kelin.architecture.ui.base.listcell.Cell


/**
 * **描述:** 普通的(无网络请求的)列表页面。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-25 13:57:10
 *
 * **版本:** v 1.0.0
 */
abstract class NormalItemListFragment<V : NormalItemListDelegate<VC, I>, VC : NormalItemListDelegate.NormalItemListDelegateCallback<I>, I : Cell> : BaseFragmentPresenter<V, VC>() {

    /**
     * 拦截条目点击事件，通常情况下各个Item的点击事件应当由Item自己处理，如果你希望自己处理而不让Item处理可以重写该方法并返回true。
     *
     * @param position 当前被点击的条目的layoutPosition。
     * @param item     当前被点击的Item。
     * @return 如果需要拦截不交给Item处理则返回true，否则返回false。
     */
    protected open fun onInterceptListItemClick(position: Int, item: I): Boolean {
        return false
    }

    protected open fun onInterceptListItemLongClick(position: Int, item: I): Boolean {
        return false
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

    open inner class NormalItemListDelegateCallbackImpl : NormalItemListDelegate.NormalItemListDelegateCallback<I> {
        override fun onListItemClick(position: Int, item: I) {
            if (!onInterceptListItemClick(position, item)) {
                item.onItemClick(requireActivity(), position)
            }
        }

        override fun onListItemLongClick(position: Int, item: I) {
            if (!onInterceptListItemLongClick(position, item)) {
                item.onItemLongClick(requireActivity(), position)
            }
        }

        override fun onListItemChildClick(position: Int, v: View, item: I) {
            if (!onInterceptListItemChildClick(position, v, item)) {
                item.onItemChildClick(requireActivity(), position, v)
            }
        }
    }
}
