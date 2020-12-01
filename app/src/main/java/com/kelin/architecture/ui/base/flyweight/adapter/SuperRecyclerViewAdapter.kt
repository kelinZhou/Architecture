package com.kelin.architecture.ui.base.flyweight.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelin.architecture.ui.base.flyweight.callback.ItemHolderGenerator
import com.kelin.architecture.ui.base.flyweight.callback.OnItemEventListener
import com.kelin.architecture.util.LogHelper
import com.kelin.architecture.widget.statelayout.FixedRecyclerView

/**
 * **描述:** RecyclerView适配器的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/29  1:23 PM
 *
 * **版本:** v 1.0.0
 */
abstract class SuperRecyclerViewAdapter<T, VH>(
    private val itemEventListener: OnItemEventListener<T>? = null,
    val dataList: MutableList<T> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() where VH : RecyclerView.ViewHolder, VH : SuperRecyclerViewAdapter.ItemViewHolderInterface<T> {

    private var pageSize: Int = 20
    var holderGenerator: ItemHolderGenerator<out VH, T>? = null
    private var adapterObservable: RecyclerView.AdapterDataObserver? = null

    /**
     * loadMore管理器。
     */
    private var lM: LoadMoreLayoutManager? = null
    private var rvLayoutManager: LinearLayoutManager? = null

    /**
     * 加载更多时的回调。
     * isReload 是否是在loadMore过程中失败了然后点击重试后的再次回调。
     */
    private var onLoadMoreListener: ((isReload: Boolean) -> Boolean)? = null

    private val itemPositionOffset: Int
        get() = if (!isLoadMoreUsable() || lM!!.noCurStateLayoutId()) 0 else 1

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView.layoutManager is LinearLayoutManager) {
            rvLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //处理loadMore
                    if (dx + dy > 0 && isLoadMoreUsable() && lM!!.isLoadState) {
                        if (lM!!.isInTheLoadMore || lM!!.isNoMoreState) {
                            return
                        } else {
                            val lastVisibleItemPosition = findLastVisibleItemPosition()
                            val size = dataList.size
                            val targetPosition = size - lM!!.loadMoreOffset
                            if (targetPosition <= 0 || lastVisibleItemPosition == targetPosition || lastVisibleItemPosition == size) {
                                startLoadMore()
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * 设置加载更多是否可用。如果有时候你的页面虽然是支持分页加载的，但是特殊情况下你并不希望展示加载中的条目，这时就可以通过
     * 调用此方法禁止加载中条目的显示。
     *
     * 例如：当你的总条目不足以显示一页的时候（假如你每页数据是20条，但是你总数据一共才5条），这时候你就可以通过调用这个方法
     * 禁止加载中条目的显示。
     *
     * @param usable true表示可用，false表示不可用。
     */
    fun setLoadMoreUsable(usable: Boolean) {
        lM?.setLoadMoreUsable(usable)
    }

    /**
     * 加载更多是否可用。
     */
    private fun isLoadMoreUsable(): Boolean {
        return lM?.isUsable ?: false
    }

    /**
     * 寻找最后一个可见的条目。
     *
     * @return 返回最后一个可见的条目所在的索引位置。
     */
    internal fun findLastVisibleItemPosition(): Int {
        return rvLayoutManager!!.findLastVisibleItemPosition()
    }


    /**
     * 开始加载更多。
     */
    private fun startLoadMore() {
        LogHelper.system.i("SuperRecyclerViewAdapter", "开始加载更多")
        onLoadMoreListener?.run {
            if (invoke(false)) {
                lM?.isInTheLoadMore = true
            }
        }
    }

    /**
     * 开始加载更多。
     */
    private fun reloadMore() {
        LogHelper.system.i("SuperRecyclerViewAdapter", "重新开始加载更多")
        onLoadMoreListener?.run {
            if (invoke(true)) {
                lM?.isInTheLoadMore = true
            }
        }
    }

    /**
     * 当加载更多完成后要调用此方法，否则不会触发下一次LoadMore事件。
     */
    fun setLoadMoreFinish() {
        LogHelper.system.i("SuperRecyclerViewAdapter", "加载完成")
        lM?.isInTheLoadMore = false
    }

    /**
     * 当加载更多失败后要调用此方法，否则没有办法点击重试加载更多。
     */
    fun setLoadMoreFailed() {
        LogHelper.system.i("SuperRecyclerViewAdapter", "加载失败")
        lM?.isInTheLoadMore = false
        lM?.setRetryState()
    }

    /**
     * 如果你的页面已经没有更多数据可以加载了的话，应当调用此方法。调用了此方法后就不会再触发LoadMore事件，否则还会触发。
     */
    fun setNoMoreData(noMoreData: Boolean) {
        if (noMoreData) {
            LogHelper.system.i("SuperRecyclerViewAdapter", "已加载到最后一页，加载完成")
            lM?.isNoMoreState = dataList.size < pageSize
        } else {
            lM?.isLoadState = true
        }
    }

    fun isNoMoreData() = lM?.isNoMoreState ?: true

    open fun setData(collection: Collection<T>, refresh: Boolean = true) {
        lM?.setLoadState()
        dataList.clear()
        dataList.addAll(collection)
        if (refresh) {
            notifyRefresh()
        } else {
            adapterObservable?.onChanged()
        }
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        if (observer is FixedRecyclerView.FixedAdapterDataObserver) {
            adapterObservable = observer
        }
    }

    protected open fun notifyRefresh() {
        notifyDataSetChanged()
    }


    open fun addItem(position: Int, itemData: T, refresh: Boolean = true) {
        dataList.add(position, itemData)
        if (refresh) {
            notifyItemInserted(position)
        }
    }

    open fun addItem(position: Int, itemData: List<T>, refresh: Boolean = true) {
        dataList.addAll(position, itemData)
        if (refresh) {
            notifyItemRangeInserted(position, itemData.size)
        }
    }

    fun addItem(itemData: T, refresh: Boolean = true) {
        addItem(itemCount, itemData, refresh)
    }

    fun notifyItem(itemData: T) {
        val position = getItemPosi(itemData)
        dataList[position] = itemData
        notifyItemChanged(position)
    }


    open fun removeItem(position: Int, refresh: Boolean = true): T? {
        val data = dataList.removeAt(position)
        if (data != null && refresh) {
            notifyItemRemoved(position)
        }
        return data

    }

    open fun removeItem(position: Int, count: Int, refresh: Boolean = true) {
        for (i: Int in 1..count) {
            dataList.removeAt(position)
        }
        if (refresh) {
            notifyItemRangeRemoved(position, count)
        }
    }

    open fun removeAll(items: Collection<T>, refresh: Boolean = true) {
        dataList.removeAll(items)
        if (refresh) {
            notifyRefresh()
        }
    }

    open fun removeItem(item: T): Int {
        val index = dataList.indexOf(item)
        val r = dataList.remove(item)
        if (r && index > -1) {
            notifyItemRemoved(index)
        }
        return index
    }


    open fun moveItem(fromPosition: Int, toPosition: Int) {
        dataList.add(toPosition, dataList.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
    }

    open fun addAll(collection: Collection<T>, refresh: Boolean = false, index: Int = -1) {
        if (index >= 0) {
            dataList.addAll(index, collection)
            if (refresh) {
                notifyItemRangeInserted(index, collection.size)
            }
        } else {
            val positionStart = dataList.size + itemPositionOffset
            dataList.addAll(collection)
            if (refresh) {
                notifyItemRangeInserted(positionStart, collection.size)
            }
        }
    }

    open fun clear(refresh: Boolean = false) {
        val size = dataList.size
        dataList.clear()
        if (refresh) notifyItemRangeRemoved(0, size)
    }

    fun getItem(position: Int): T {
        return dataList[position]
    }

    fun getItemPosi(data: T): Int {
        return dataList.indexOf(data)
    }

    final override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 0 else dataList.size + itemPositionOffset
    }

    final override fun getItemViewType(position: Int): Int {
        return if (isLoadMoreItem(position)) lM!!.itemLayoutId else getItemType(position)
    }

    protected abstract fun getItemType(position: Int): Int

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == lM?.itemLayoutId) {
            LoadMoreItemViewHolder(lM!!.getLayoutView(parent) {
                lM!!.setLoadState()
                reloadMore()
            })
        } else {
            holderGenerator?.createHolder(parent, viewType, itemEventListener)
                ?: onCreateViewHolder(parent, viewType, itemEventListener)
        }
    }

    protected abstract fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
        itemEventListener: OnItemEventListener<T>?
    ): VH

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    @Suppress("UNCHECKED_CAST")
    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (holder is ItemViewHolderInterface<*> && !isLoadMoreItem(position)) {
            val vh = holder as VH
            if (payloads.isEmpty()) {
                vh.bindView(getItem(position))
            } else {
                val o = payloads[0]
                if (o is Bundle) {
                    vh.bindPartView(position, getItem(position), o)
                } else {
                    vh.bindView(getItem(position))
                }
            }
        } /*else {
            val loadingView = holder.itemView.findViewById<ImageView>(R.id.ivLoading)
            if (loadingView != null && loadingView.drawable is AnimationDrawable) {
                (loadingView.drawable as AnimationDrawable).start()
            }
        }*/
    }

    private fun isLoadMoreItem(position: Int): Boolean {
        return isLoadMoreUsable() && !lM!!.noCurStateLayoutId() && position == dataList.size
    }

    final override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as VH).onViewAttachedToWindow()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as VH).onViewDetachedFromWindow()
    }

    /**
     * 设置加载更多时显示的布局。所有布局资源的宽高应当一样。
     *
     * @param loadMoreLayoutId 加载更多时显示的布局的资源ID。
     * @param retryLayoutId    加载更多失败时显示的布局。
     * @param pageSize 每页的数据大小，用来处理当数据不满一页时是否显示noMoreDataLayout的逻辑。
     * @param listener         加载更多被触发的监听。
     */
    fun setLoadMoreView(@LayoutRes loadMoreLayoutId: Int, @LayoutRes retryLayoutId: Int, pageSize: Int, listener: ((isReload: Boolean) -> Boolean)) {
        setLoadMoreView(loadMoreLayoutId, retryLayoutId, 0, pageSize, listener)
    }

    /**
     * 设置加载更多时显示的布局。所有布局资源的宽高应当一样。
     *
     * @param loadMoreLayoutId   加载更多时显示的布局的资源ID。
     * @param retryLayoutId      加载更多失败时显示的布局。
     * @param noMoreDataLayoutId 没有更多数据时显示的布局。
     * @param pageSize 每页的数据大小，用来处理当数据不满一页时是否显示noMoreDataLayout的逻辑。
     * @param listener           加载更多被触发的监听。
     */
    fun setLoadMoreView(
        @LayoutRes loadMoreLayoutId: Int, @LayoutRes retryLayoutId: Int, @LayoutRes noMoreDataLayoutId: Int, pageSize: Int, listener: ((isReload: Boolean) -> Boolean)
    ) {
        setLoadMoreView(loadMoreLayoutId, retryLayoutId, noMoreDataLayoutId, 0, pageSize, listener)
    }

    /**
     * 设置加载更多时显示的布局。所有布局资源的宽高应当一样。
     *
     * @param loadMoreLayoutId   加载更多时显示的布局的资源ID。
     * @param retryLayoutId      加载更多失败时显示的布局。
     * @param noMoreDataLayoutId 没有更多数据时显示的布局。
     * @param offset             加载更多触发位置的偏移值。偏移范围只能是1-10之间的数值。正常情况下是loadMoreLayout显示的时候就开始触发，
     * 但如果设置了该值，例如：2，那么就是在loadMoreLayout之前的两个位置的时候开始触发。
     * @param pageSize 每页的数据大小，用来处理当数据不满一页时是否显示noMoreDataLayout的逻辑。
     * @param listener           加载更多被触发的监听。
     */
    fun setLoadMoreView(
        @LayoutRes loadMoreLayoutId: Int,
        @LayoutRes retryLayoutId: Int,
        @LayoutRes noMoreDataLayoutId: Int,
        @Size(min = 1, max = 10) offset: Int,
        pageSize: Int,
        listener: ((isReload: Boolean) -> Boolean)
    ) {
        this.pageSize = pageSize
        setLoadMoreView(LoadMoreLayoutManager(loadMoreLayoutId, retryLayoutId, noMoreDataLayoutId, offset), listener)
    }

    /**
     * 设置加载更多时显示的布局。
     *
     * @param layoutInfo LoadMore布局信息对象。
     * @param listener   加载更多被触发的监听。
     */
    private fun setLoadMoreView(layoutInfo: LoadMoreLayoutManager, listener: ((isReload: Boolean) -> Boolean)) {
        lM = layoutInfo
        onLoadMoreListener = listener
    }

    interface ItemViewHolderInterface<T> {

        /**
         * 绑定数据。
         */
        fun bindView(item: T)

        /**
         * 绑定Diff对比之后的有差异的数据，所有有差异的数据都存放在payload中。
         */
        fun bindPartView(position: Int, item: T, payload: Bundle)

        fun onViewAttachedToWindow()

        fun onViewDetachedFromWindow()
    }

    private inner class LoadMoreItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemViewHolderInterface<T> {
        override fun onViewAttachedToWindow() {}

        override fun onViewDetachedFromWindow() {}

        override fun bindView(item: T) {}

        override fun bindPartView(position: Int, item: T, payload: Bundle) {}
    }
}
