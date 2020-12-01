package com.kelin.architecture.widget.statelayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * **描述:** 具有页面状态的View
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-9  8:42
 *
 * **版本:** v 1.0.0
 */
class StatePageLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), StatePage {
    /**
     * 数据展示页面。
     */
    private var mDataView: View? = null
    /**
     * 数据加载中页面。
     */
    private var mLoadingView: View? = null
    /**
     * 数据加载失败重试页面。
     */
    private var mRetryView: View? = null
    /**
     * 数据为空时的页面。
     */
    private var mEmptyView: View? = null
    /**
     * 用来记录当前的页面状态。
     */
    private var mStateFlags = StatePage.NOTHING_STATE

    fun init(@StateFlags flags: Int, @LayoutRes loadingView: Int, @LayoutRes retryView: Int, @LayoutRes emptyView: Int, dataView: View? = null) {
        mStateFlags = flags
        if (dataView != null) {
            val dataParent = dataView.parent as? ViewGroup
            if (dataParent != null) {
                val index = dataParent.indexOfChild(dataView)
                val lp = dataView.layoutParams
                dataParent.removeView(dataView)
                addView(dataView, generateDefaultLayoutParams())
                dataParent.addView(this, index, lp)
            } else {
                addView(dataView, generateDefaultLayoutParams())
            }
            mDataView = dataView
        }
        if (flags != StatePage.NOTHING_STATE) {
            if (mStateFlags and StatePage.HAVE_LOADING_STATE > 0) {
                val view = inflateView(loadingView)
                if (view != null) {
                    addView(view, generateDefaultLayoutParams())
                    mLoadingView = view
                }
            }
            if (mStateFlags and StatePage.HAVE_RETRY_STATE > 0) {
                val view = inflateView(retryView)
                if (view != null) {
                    view.visibility = View.GONE
                    addView(view, generateDefaultLayoutParams())
                    mRetryView = view
                }
            }
            if (mStateFlags and StatePage.HAVE_EMPTY_STATE > 0) {
                val view = inflateView(emptyView)
                if (view != null) {
                    view.visibility = View.GONE
                    addView(view, generateDefaultLayoutParams())
                    mEmptyView = view
                }
            }
        }
    }

    override fun setBackground(color: Int) {
        mEmptyView?.setBackgroundResource(color)
        mRetryView?.setBackgroundResource(color)
        mLoadingView?.setBackgroundResource(color)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(-1, -1)
    }

    private fun inflateView(layoutRes: Int): View? {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    override fun showDataView() {
        changePageState(dataView = true, loadingView = false, retryView = false, emptyView = false)
    }

    override fun showLoadingView() {
        changePageState(dataView = false, loadingView = true, retryView = false, emptyView = false)
    }

    override fun showRetryView() {
        changePageState(dataView = false, loadingView = false, retryView = true, emptyView = false)
    }

    override fun showEmptyView() {
        changePageState(dataView = false, loadingView = false, retryView = false, emptyView = true)
    }

    override val loadingView: View?
        get() = mLoadingView

    override val retryView: View?
        get() = mRetryView

    override val emptyView: View?
        get() = mEmptyView

    override val dataView: View
        get() = mDataView!!

    private fun changePageState(dataView: Boolean, loadingView: Boolean, retryView: Boolean, emptyView: Boolean) {
        if (mStateFlags != StatePage.NOTHING_STATE && (dataView || loadingView || retryView || emptyView)) {
            val dataVb = mapperToVisibility(dataView)
            if (mDataView != null) {
                mDataView?.visibility = dataVb
            } else {
                for (i: Int in 0 until childCount) {
                    val child = getChildAt(i)
                    if (child != mLoadingView && child != mRetryView && child != mEmptyView) {
                        child.visibility = dataVb
                    }
                }
            }
            mLoadingView?.visibility = mapperToVisibility(loadingView)
            mRetryView?.visibility = mapperToVisibility(retryView)
            mEmptyView?.visibility = mapperToVisibility(emptyView)
        }
    }

    private fun mapperToVisibility(dataView: Boolean) = if (dataView) View.VISIBLE else View.GONE
}