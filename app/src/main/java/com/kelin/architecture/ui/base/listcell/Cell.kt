package com.kelin.architecture.ui.base.listcell

import android.content.Context
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.extensions.LayoutContainer

/**
 * **描述: ** Cell类似于iOS中的Cell,这里其实可以看成ViewHolder的代理，用来处理ViewHolder中的绑定数据及初始化View。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2020/3/10  下午12:00
 *
 * **版本: ** v 1.0.0
 */

interface Cell : LayoutContainer {
    /**
     * 获取条目类型。
     */
    @get:LayoutRes
    val itemLayoutRes: Int

    @get:IdRes
    val clickableIds: IntArray?

    @get:IdRes
    val itemClickableViewId: Int

    @get:DrawableRes
    val itemBackgroundResource: Int

    fun onCreate(parent: RecyclerView.ViewHolder)

    fun bindData(parent: RecyclerView.ViewHolder)

    fun onItemClick(context: Context, position: Int)

    fun onItemLongClick(context: Context, position: Int)

    fun onItemChildClick(context: Context, position: Int, v: View)

    fun needFilterDoubleClick(v: View): Boolean

    fun onViewAttachedToWindow(parent: RecyclerView.ViewHolder, position: Int)

    fun onViewDetachedFromWindow(parent: RecyclerView.ViewHolder, position: Int)

    fun setIsRecyclable(recyclable: Boolean)

    val itemClickable: Boolean

    val itemLongClickable: Boolean

    val haveItemClickBg: Boolean

    val spanSize: Int

    companion object {

        fun equals(a: Cell?, b: Cell): Boolean {
            return a == b || (a != null && a == b)
        }
    }
}
