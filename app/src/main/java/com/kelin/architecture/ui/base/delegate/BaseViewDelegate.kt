package com.kelin.architecture.ui.base.delegate

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.kelin.architecture.R
import com.kelin.architecture.core.AppModule
import com.kelin.architecture.domain.croe.exception.ApiException
import com.kelin.architecture.ui.base.presenter.ViewPresenter
import com.kelin.architecture.util.LogHelper
import com.kelin.architecture.util.NetWorkStateUtil
import com.kelin.architecture.util.text.SimpleTextWatch
import com.kelin.architecture.widget.statelayout.StatePage
import com.kelin.architecture.widget.statelayout.StatePageLayout

/**
 * **描述:** Delegate的基类，所有的Delegate都必须改类的派生类。
 *
 * **创建人:** kelin
 * **创建时间:** 2019/3/29  1:33 PM
 * **版本:** v 1.0.0
 */
abstract class BaseViewDelegate<VC : BaseViewDelegate.BaseViewDelegateCallback> : ViewDelegate<ViewPresenter<VC>> {

    private val defaultLoadingOption: StateOption by lazy { LoadingStateOption() }
    private val defaultRetryOption: StateOption by lazy { RetryStateOption() }
    private val defaultEmptyOption: StateOption by lazy { EmptyStateOption() }

    final override var containerView: View? = null
        private set

    protected var viewPresenter: ViewPresenter<VC>? = null

    protected val viewCallback: VC by lazy { viewPresenter!!.viewCallback }

    private val mOnCLickListener: View.OnClickListener by lazy { InnerOnclickListener() }

    /**
     * 所有界面都必须有重试和加载的view
     */
    protected lateinit var statePage: StatePage
        private set

    /**
     * 获取页面状态标识，也就是说当前页面需要拥有哪些状态下的页面。多个状态用 "|" 符号连接。
     *
     * @return 返回当前页面拥有的状态，这里默认实现为拥有[StatePage.HAVE_EMPTY_STATE]和
     * [StatePage.HAVE_RETRY_STATE]。
     * @see StatePage.HAVE_RETRY_STATE
     *
     * @see StatePage.HAVE_EMPTY_STATE
     *
     * @see StatePage.HAVE_LOADING_STATE
     */
    protected open val pageStateFlags: Int
        get() = StatePage.NOTHING_STATE

    protected val context: Context
        get() = viewPresenter?.getContext() ?: throw NullPointerException("the viewPresenter is null object!")

    protected val applicationContext: Context
        get() = context.applicationContext

    @get:LayoutRes
    abstract val rootLayoutId: Int

    @get:IdRes
    protected open val dataViewId: Int
        get() = 0

    /**
     * show加载数据前的view
     * 3个互斥
     */
    fun showLoadingView() {
        statePage.showLoadingView()
    }

    /**
     * show数据加载成功之后的view
     * 3个互斥
     */
    fun showDataView() {
        statePage.showDataView()
    }

    fun showRetryView(e: ApiException? = null) {
        if (e?.isNetworkError() == true || !NetWorkStateUtil.isConnected) {
            showRetryView(getString(R.string.poor_network_environment), R.drawable.img_network_unabailable)
        } else {
            showRetryView(e?.displayMessage ?: getString(R.string.load_error_click_retry), R.drawable.img_common_empty)
        }
    }

    fun showRetryView(tip: String?, @DrawableRes icon: Int = R.drawable.img_common_empty) {
        val retryView = statePage.retryView
        if (retryView != null) {
            defaultRetryOption.icon = icon
            defaultRetryOption.title = tip ?: getString(R.string.load_error_click_retry)
            refreshStatePageView(retryView, defaultRetryOption)
            statePage.showRetryView()
        }
    }

    fun showEmptyView() {
        statePage.showEmptyView()
    }

    @CallSuper
    open fun onCreate(presenter: ViewPresenter<VC>) {
        this.viewPresenter = presenter
    }

    @CallSuper
    open fun onPresenterDestroy() {
    }

    override fun presentView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {}

    @CallSuper
    override fun bindView(viewPresenter: ViewPresenter<VC>) {
    }

    @CallSuper
    override fun unbindView() {
        this.viewPresenter = null
    }

    protected fun <V : View> getView(id: Int): V? {
        return containerView?.findViewById(id)
    }

    protected fun getString(@StringRes stringResId: Int): String {
        return (viewPresenter?.getContext() ?: AppModule.getContext()).resources.getString(stringResId)
    }

    protected fun getColor(@ColorRes colorId: Int): Int {
        return ContextCompat.getColor(viewPresenter?.getContext() ?: AppModule.getContext(), colorId)
    }


    protected fun getColorStateList(@ColorRes colorStateListId: Int): ColorStateList {
        return AppCompatResources.getColorStateList(viewPresenter?.getContext() ?: AppModule.getContext(), colorStateListId)
    }

    protected fun getDrawable(@DrawableRes drawableId: Int): Drawable? {
        return AppCompatResources.getDrawable(viewPresenter?.getContext() ?: AppModule.getContext(), drawableId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val statePageLayout = StatePageLayout(container?.context ?: context)
        statePageLayout.isFocusable = false
        statePageLayout.isClickable = false

        val rootView = inflater.inflate(rootLayoutId, container, false)
        val dataView = rootView.findViewById<View>(dataViewId)
        containerView = if (dataView == null || dataView === rootView) {
            statePageLayout.init(pageStateFlags, loadingStateLayout, retryStateLayout, emptyStateLayout, rootView)
            statePageLayout
        } else {
            statePageLayout.init(pageStateFlags, loadingStateLayout, retryStateLayout, emptyStateLayout, dataView)
            rootView
        }
        this.statePage = statePageLayout
        statePageLayout.emptyView?.findViewById<View>(R.id.btnStatePageButton)?.setOnClickListener { onEmptyButtonClick() }
        refreshEmptyView()
        refreshLoadingView()
        statePageLayout.loadingView?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                val loadingIcon = statePageLayout.loadingView?.findViewById<ImageView>(R.id.ivStatePageIcon)?.drawable
                if (loadingIcon != null && loadingIcon is AnimationDrawable) {
                    loadingIcon.start()
                }
            }

            override fun onViewDetachedFromWindow(v: View?) {
                val loadingIcon = statePageLayout.loadingView?.findViewById<ImageView>(R.id.ivStatePageIcon)?.drawable
                if (loadingIcon != null && loadingIcon is AnimationDrawable) {
                    loadingIcon.stop()
                }
            }

        })

        return containerView!!
    }

    private fun refreshLoadingView() {
        val stateView = statePage.loadingView
        if (stateView != null) {
            onConfigurationLoadingOption(defaultLoadingOption)
            refreshStatePageView(stateView, defaultLoadingOption)
        }
    }

    protected open fun onConfigurationLoadingOption(option: StateOption) {

    }

    protected fun refreshRetryView() {
        val stateView = statePage.retryView
        if (stateView != null) {
            onConfigurationRetryOption(defaultRetryOption)
            refreshStatePageView(stateView, defaultRetryOption)
        }
    }

    protected open fun onConfigurationRetryOption(option: StateOption) {

    }

    protected fun refreshEmptyView() {
        val stateView = statePage.emptyView
        if (stateView != null) {
            onConfigurationEmptyOption(defaultEmptyOption)
            refreshStatePageView(stateView, defaultEmptyOption)
        }
    }

    protected open fun onConfigurationEmptyOption(option: StateOption) {

    }

    protected open fun onEmptyButtonClick() {
    }

    override fun restoreInstanceState(savedInstanceState: Bundle?) {}

    override fun saveInstanceState(outState: Bundle) {}

    override fun destroyView(outState: Bundle?) {
        containerView = null
    }

    override fun postDelayed(delayMillis: Long, runner: () -> Unit) {
        containerView?.postDelayed(runner, delayMillis) ?: LogHelper.system.e("containerView is Null Object!")
    }

    protected fun listenerTextChanged(vararg views: EditText) {
        if (views.isNotEmpty()) {
            for (view in views) {
                view.addTextChangedListener(object : SimpleTextWatch(view) {
                    override fun afterTextChanged(et: TextView, s: Editable) {
                        this@BaseViewDelegate.onTextChanged(et, et is EditText, s)
                    }
                })
            }
        }
    }

    protected open fun onTextChanged(v: TextView, isEditText: Boolean, text: Editable) {}

    fun bindClickEvent(vararg views: View) {
        if (views.isNotEmpty()) {
            for (v in views) {
                v.setOnClickListener(mOnCLickListener)
            }
        }
    }


    protected open fun onViewClick(v: View) {}


    @get:LayoutRes
    protected open val loadingStateLayout: Int
        get() = R.layout.state_layout_loading

    @get:LayoutRes
    protected open val retryStateLayout: Int
        get() = R.layout.state_layout_common

    @get:LayoutRes
    protected open val emptyStateLayout: Int
        get() = R.layout.state_layout_common


    private inner class InnerOnclickListener : View.OnClickListener {
        override fun onClick(v: View) {
            // 防止重复点击
            //            if (ViewHelper.isDoubleClick(v.hashCode())) {
            //                return;
            //            }
            onViewClick(v)
        }
    }

    interface BaseViewDelegateCallback : ViewDelegate.ViewDelegateCallback

    companion object {
        fun refreshStatePageView(stateView: View, option: StateOption) {
            val iconView = stateView.findViewById<ImageView>(R.id.ivStatePageIcon)
            iconView?.visibility = if (option.icon == 0) {
                View.GONE
            } else {
                iconView?.setImageResource(option.icon)
                View.VISIBLE
            }
            val titleView = stateView.findViewById<TextView>(R.id.tvStatePageTitle)
            titleView?.visibility = if (option.title.isEmpty()) {
                View.GONE
            } else {
                titleView?.text = option.title
                if (option.titleColor != 0) {
                    titleView.setTextColor(option.titleColor)
                }
                View.VISIBLE
            }
            val subTitleView = stateView.findViewById<TextView>(R.id.tvStatePageSubTitle)
            subTitleView?.visibility = if (option.subTitle.isEmpty()) {
                View.GONE
            } else {
                subTitleView.text = option.subTitle
                if (option.subTitleColor != 0) {
                    subTitleView.setTextColor(option.subTitleColor)
                }
                View.VISIBLE
            }
            val button = stateView.findViewById<TextView>(R.id.btnStatePageButton)
            button?.visibility = if (option.btnText.isEmpty()) {
                View.GONE
            } else {
                button.text = option.btnText
                if (option.btnTextColor != 0) {
                    button.setTextColor(option.btnTextColor)
                }
                if (option.btnBackground != 0) {
                    button.setBackgroundResource(option.btnBackground)
                }
                View.VISIBLE
            }
        }
    }

    interface StateOption {
        var icon: Int

        var title: String

        var titleColor: Int

        var subTitle: String

        var subTitleColor: Int

        var btnText: String

        var btnTextColor: Int

        var btnBackground: Int
    }

    class SimpleStateOption(override var icon: Int, override var title: String, override var subTitle: String = "", override var btnText: String = "") : StateOption {

        override var titleColor: Int = 0

        override var subTitleColor: Int = 0

        override var btnTextColor: Int = 0

        override var btnBackground: Int = 0
    }

    private inner class LoadingStateOption : StateOption {

        override var icon: Int = 0

        override var title: String = getString(R.string.loading_please_wait)

        override var titleColor: Int = getColor(R.color.grey_666)

        override var subTitle: String = ""

        override var subTitleColor: Int = getColor(R.color.grey_666)

        override var btnText: String = ""

        override var btnTextColor: Int = getColor(android.R.color.white)

        override var btnBackground: Int = 0
    }

    private inner class RetryStateOption : StateOption {

        override var icon: Int = R.drawable.img_common_empty

        override var title: String = getString(R.string.load_error_click_retry)

        override var titleColor: Int = getColor(R.color.grey_666)

        override var subTitle: String = ""

        override var subTitleColor: Int = getColor(R.color.grey_666)

        override var btnText: String = ""

        override var btnTextColor: Int = getColor(android.R.color.white)

        override var btnBackground: Int = 0
    }

    private inner class EmptyStateOption : StateOption {

        override var icon: Int = R.drawable.img_common_empty

        override var title: String = getString(R.string.no_data)

        override var titleColor: Int = getColor(R.color.grey_666)

        override var subTitle: String = ""

        override var subTitleColor: Int = getColor(R.color.grey_666)

        override var btnText: String = ""

        override var btnTextColor: Int = getColor(android.R.color.white)

        override var btnBackground: Int = 0
    }
}
