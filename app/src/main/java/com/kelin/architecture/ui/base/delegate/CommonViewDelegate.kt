package com.kelin.architecture.ui.base.delegate

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.kelin.architecture.ui.base.presenter.ViewPresenter
import com.kelin.architecture.widget.statelayout.StatePage

/**
 * **描述:** 可以初始化数据并且有页面状态的ViewDelegate。
 *
 * **创建人:** kelin
 * **创建时间:** 2020/3/9  1:33 PM
 * **版本:** v 1.0.0
 */
abstract class CommonViewDelegate<VC : CommonViewDelegate.CommonViewDelegateCallback, D> : BaseViewDelegate<VC>() {


    override val pageStateFlags: Int
        get() = StatePage.HAVE_RETRY_STATE or StatePage.HAVE_LOADING_STATE

    /**
     * retry view是否可以点击
     */
    protected open val isRetryViewClickable: Boolean
        get() = true

    /**
     * empty view是否可以点击
     */
    protected open val isEmptyViewClickable: Boolean
        get() = true


    @CallSuper
    override fun presentView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {
        if (isRetryViewClickable && statePage.retryView != null) {
            bindClickEvent(statePage.retryView!!)
        }

        if (isEmptyViewClickable && statePage.emptyView != null) {
            bindClickEvent(statePage.emptyView!!)
        }
    }

    abstract fun setInitialData(data: D)

    /**
     * 判断指定的View是不是RetryView。
     */
    private fun isRetryView(v: View): Boolean {
        return statePage.retryView.let { it != null && (it.id == v.id || it === v) }
    }

    private fun isEmptyView(v: View): Boolean {
        return statePage.emptyView.let { it != null && (it.id == v.id || it === v) }
    }


    @CallSuper
    override fun onViewClick(v: View) {
        super.onViewClick(v)
        if (isRetryView(v)) {
            viewCallback.onRetry()
        }else if (isEmptyView(v)) {
            viewCallback.onRetry()
        }
    }


    interface CommonViewDelegateCallback : BaseViewDelegateCallback {
        fun onRetry()

        fun onRefresh(showProgress: Boolean = false)
    }
}
