package com.kelin.architecture.ui.base.flyweight.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.kelin.architecture.R
import kotlinx.android.extensions.LayoutContainer


/**
 * **描述: ** ViewHolder的基类，所有的RecyclerView中的ViewHolder都应该继承该类。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2020/3/10  下午3:00
 *
 * **版本: ** v 1.0.0
 */
abstract class BaseViewHolder protected constructor(view:View, initEnable: Boolean) : RecyclerView.ViewHolder(view), LayoutContainer {

    protected constructor(parent: ViewGroup, layoutRes: Int, initEnable: Boolean):this(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false), initEnable)

    /**
     * 是否需要给条目设置点击背景色。
     */
    protected open val haveItemClickBg: Boolean
        get() = true

    /**
     * 获取条目的背景资源文件。
     */
    @get:DrawableRes
    protected open val itemBackgroundResource: Int
        get() = R.drawable.selector_recycler_item_bg


    @get:IdRes
    protected open val itemClickableViewId: Int
        get() = 0

    override val containerView: View?
        get() = itemView

    init {
        if (initEnable) {
            initViewHolder()
        }
    }

    protected fun initViewHolder() {
        if (haveItemClickBg) {
            val itemClickView = itemView.findViewById(itemClickableViewId) ?: itemView
            val resource = itemBackgroundResource
            if (resource != 0) {
                itemClickView.setBackgroundResource(resource)
            }
        }
    }
}