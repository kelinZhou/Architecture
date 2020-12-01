package com.kelin.architecture.ui.base.flyweight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.Size
import com.kelin.architecture.R

/**
 * 描述 用来描述加载更多的布局信息。
 * 创建人 kelin
 * 创建时间 2020/3/10  下午2:39
 * 版本 v 1.0.0
 */
internal class LoadMoreLayoutManager(
    /**
     * 加载更多时显示的布局文件ID。
     */
    @param:LayoutRes private val loadMoreLayoutId: Int,
    /**
     * 加载更多失败时显示的布局文件ID。
     */
    @param:LayoutRes private val retryLayoutId: Int,
    /**
     * 没有更多数据时显示的布局文件ID。
     */
    @param:LayoutRes private val noMoreDataLayoutId: Int,
    /**
     * 提前加载设置。
     */
    @Size(min = 1, max = 10) offset: Int
) {

    companion object {
        private const val STATE_FAILED = 0x000000F0
        private const val STATE_NO_MORE = 0x000000F1
        private const val STATE_LOAD = 0x000000F2
    }

    /**
     * 获取加载更多触发时机的偏移值。
     */
    val loadMoreOffset: Int = if (offset < 0) 0 else if (offset > 10) 10 else offset

    private var mCurState = STATE_LOAD

    /**
     * 加载更多时显示的View。
     */
    private var mLoadMoreLayout: View? = null

    /**
     * 加载更多失败时显示的View。
     */
    private var mRetryLayout: View? = null

    /**
     * 没有更多数据时显示的View。
     */
    private var mNoMoreDataLayout: View? = null
    /**
     * 判断是否正在加载中。
     */
    /**
     * 是否正在加载更多，通过此变量做判断，防止LoadMore重复触发。
     */
    var isInTheLoadMore = false
    /**
     * 判断加载更多是否可用。
     *
     * @return 返回true表示可用，false表示不可用。
     */
    /**
     * 加载更多是否可用。
     */
    var isUsable = true
        private set

    /**
     * 用来承载所有状态下视图的容器，LoadMore条目的根布局。
     */
    private var mRootView: ViewGroup? = null

    /**
     * 用来记录当前是否不足一页数据。
     */
    private var mLessThenOnePage = false

    /**
     * 用来切换到没有更多数据的视图的任务。
     */
    private val mNoMoreDataStateChangeRunnable = Runnable {
        if (mLessThenOnePage) {
            setVisible(null, mNoMoreDataLayout, mRetryLayout, mLoadMoreLayout)
        } else {
            setVisible(mNoMoreDataLayout, mRetryLayout, mLoadMoreLayout)
        }
    }


    init {
        setVisible(mLoadMoreLayout, mRetryLayout, mNoMoreDataLayout)
    }

    /**
     * 设置状态为加载失败，点击重试。
     */
    fun setRetryState() {
        mCurState = STATE_FAILED
        setVisible(mRetryLayout, mNoMoreDataLayout, mLoadMoreLayout)
    }

    /**
     * 判断是否是加载失败点击重试状态。
     *
     * @return 返回true表示是，false表示不是。
     */
    val isRetryState: Boolean
        get() = mCurState == STATE_FAILED

    /**
     * 判断是否是没有更多数据状态。
     *
     * @return 返回true表示是，false表示不是。
     */
    var isNoMoreState: Boolean
        get() = mCurState == STATE_NO_MORE
        set(lessThenOnePage) {
            isInTheLoadMore = false
            mCurState = STATE_NO_MORE
            //这里做延迟是为了将新数据加载完成之后才切换视图，为了避免新数据还没有插入就已经看到了没有更多数据的提示。
            //如果不在乎这个细节可以不延时。
            mLessThenOnePage = lessThenOnePage
            mRootView?.postDelayed(mNoMoreDataStateChangeRunnable, 200)
        }

    /**
     * 设置状态为加载更多。
     */
    fun setLoadState() {
        isLoadState = false
    }

    private fun setVisible(visible: View?, vararg goneViews: View?) {
        if (visible != null) {
            visible.visibility = View.VISIBLE
        }
        for (view in goneViews) {
            view?.visibility = View.GONE
        }
    }
    /**
     * 判断是否为加载更多状态。
     *
     * @return 返回true表示是，false表示不是。
     */
    /**
     * 设置状态为加载更多。
     */
    var isLoadState: Boolean
        get() = mCurState == STATE_LOAD
        set(force) {
            if (force || mCurState == STATE_FAILED) {
                mCurState = STATE_LOAD
                setVisible(mLoadMoreLayout, mRetryLayout, mNoMoreDataLayout)
            }
        }

    /**
     * 获取LoadMore条目布局ID。
     */
    @get:LayoutRes
    val itemLayoutId: Int
        get() = R.layout.item_load_more_group_layout

    /**
     * 获取某个状态下应当显示的View。
     *
     * @param parent 当前的RecyclerView对象。
     * @return 如果条件满足返回对应的View。否则返回null。
     */
    fun getLayoutView(parent: ViewGroup, retryClickListener: ((v: View) -> Unit)?): View {
        val inflater = LayoutInflater.from(parent.context)
        val rootView = inflater.inflate(itemLayoutId, parent, false) as ViewGroup
        mRootView = rootView
        mLoadMoreLayout = addView(loadMoreLayoutId, mRootView, inflater, true)
        mRetryLayout = addView(retryLayoutId, mRootView, inflater, false)
        mRetryLayout?.setOnClickListener(retryClickListener)
        mNoMoreDataLayout = addView(noMoreDataLayoutId, mRootView, inflater, false)
        when (mCurState) {
            STATE_FAILED -> setVisible(mRetryLayout, mLoadMoreLayout, mNoMoreDataLayout)
            STATE_NO_MORE -> if (mLessThenOnePage) {
                setVisible(null, mNoMoreDataLayout, mRetryLayout, mLoadMoreLayout)
            } else {
                setVisible(mNoMoreDataLayout, mRetryLayout, mLoadMoreLayout)
            }
            STATE_LOAD -> setVisible(mLoadMoreLayout, mNoMoreDataLayout, mRetryLayout)
        }
        return rootView
    }

    /**
     * 添加一个布局视图到指定的父容器中。
     *
     * @param layoutId        要添加的布局视图的LayoutId。
     * @param parent          要指定的父容器。
     * @param inflater        填充器对象。
     * @param resetParentSize 是否根据被添加的视图的宽高重置父容器的宽高。
     */
    private fun addView(layoutId: Int, parent: ViewGroup?, inflater: LayoutInflater, resetParentSize: Boolean): View {
        return if (layoutId == 0) {
            val emptyChild = TextView(parent!!.context)
            parent.addView(emptyChild, ViewGroup.LayoutParams(0, 0))
            emptyChild
        } else {
            val contentView = inflater.inflate(layoutId, parent, false)
            if (resetParentSize) {
                val lp = contentView.layoutParams
                val plp = parent!!.layoutParams
                if (plp != null && lp != null) {
                    plp.width = lp.width
                    plp.height = lp.height
                    parent.layoutParams = plp
                }
            }
            parent!!.addView(contentView)
            contentView
        }
    }

    /**
     * 判断是否没有正确的状态。
     *
     * @return 如果是返回true，否者返回false。
     */
    fun noCurStateLayoutId(): Boolean {
        var layoutId = 0
        when (mCurState) {
            STATE_LOAD -> layoutId = loadMoreLayoutId
            STATE_FAILED -> layoutId = retryLayoutId
            STATE_NO_MORE -> layoutId = noMoreDataLayoutId
        }
        return layoutId == 0
    }

    /**
     * 设置加载更多是否可用。
     *
     * @param usable true表示可用，false表示不可用。默认为true。
     */
    fun setLoadMoreUsable(usable: Boolean) {
        isUsable = usable
    }
}