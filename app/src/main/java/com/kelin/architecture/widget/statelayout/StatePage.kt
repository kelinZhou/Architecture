package com.kelin.architecture.widget.statelayout

import android.view.View
import androidx.annotation.ColorRes

/**
 * **描述:** 定义具有页面状态的接口。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-23  15:24
 *
 * **版本:** v 1.0.0
 */
interface StatePage {

    companion object {
        /**
         * 不拥有任何除数据展示页面之外的任何页面。
         */
        const val NOTHING_STATE = 0x0000_0000
        /**
         * 拥有加载中页面。
         */
        const val HAVE_LOADING_STATE = 0b0000_0001
        /**
         * 拥有失败重试页面。
         */
        const val HAVE_RETRY_STATE = HAVE_LOADING_STATE.shl(1)
        /**
         * 拥有数据为空页面。
         */
        const val HAVE_EMPTY_STATE = HAVE_LOADING_STATE.shl(2)
    }

    /**
     * 显示数据展示页面。
     */
    fun showDataView()

    /**
     * 显示数据加载中页面。
     */
    fun showLoadingView()

    /**
     * 显示数据加载失败需要重试页面。
     */
    fun showRetryView()

    /**
     * 显示数据为空时的页面。
     */
    fun showEmptyView()

    fun setBackground(@ColorRes color: Int)


    /**
     * 获取加载中页面。
     *
     * @return 返回加载中页面。
     */
    val loadingView: View?

    /**
     * 获取失败重试页面。
     *
     * @return 返回失败重试页面。
     */
    val retryView: View?

    /**
     * 获取数据为空时的页面。
     *
     * @return 返回数据为空的页面。
     */
    val emptyView: View?

    val dataView:View
}