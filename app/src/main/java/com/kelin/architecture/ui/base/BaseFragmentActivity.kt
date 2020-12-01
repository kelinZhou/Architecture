package com.kelin.architecture.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.kelin.architecture.BuildConfig
import com.kelin.architecture.R
import com.kelin.architecture.core.SystemError
import com.kelin.architecture.tools.AppLayerErrorCatcher
import com.kelin.architecture.ui.base.common.CommonErrorFragment
import com.kelin.architecture.util.ToastUtil

/**
 * **描述:** 用来承载Fragment的基类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-09-26  16:23
 *
 * **版本:** v 1.0.0
 */
abstract class BaseFragmentActivity : BasicActivity() {

    @get:LayoutRes
    protected open val activityRootLayout: Int
        get() = R.layout.activity_common_layout

    @get:IdRes
    protected open val warpFragmentId: Int
        get() = R.id.fragment_container

    @get:IdRes
    protected open val toolbarId: Int
        get() = R.id.my_awesome_toolbar

    @get:IdRes
    protected open val toolbarTitleViewId: Int
        get() = R.id.toolbar_center_title

    @get:IdRes
    protected open val toolbarSubTitleViewId: Int
        get() = R.id.toolbar_sub_title

    protected open val addInitialFragmentEnable: Boolean
        get() = true

    protected open val needNavigationIcon: Boolean = true

    protected val curTarget: Int
        get() = intent.getIntExtra(KEY_TARGET_PAGE, PAGE_UNKNOWN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        setContentView(activityRootLayout)
        title = intent.getStringExtra(KEY_PAGE_TITLE)
        if (addInitialFragmentEnable) {
            addInitialFragment()
        }
        initTitleBar(getView(toolbarId), getView(toolbarTitleViewId), getView(toolbarSubTitleViewId), needNavigationIcon)
    }

    fun hideToolbarLine() {
        (getView<View>(warpFragmentId)?.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
    }

    private fun addInitialFragment() {
        val intent = intent
        if (curTarget == PAGE_UNKNOWN) {
            if (BuildConfig.DEBUG) {
                throw RuntimeException("The target page value is unknown!")
            } else {
                onJumpError(SystemError.NULL_ARGUMENT, RuntimeException("The target page value is unknown!"))
                return
            }
        }
        addFragment(warpFragmentId, getCurrentFragment(curTarget, intent))
    }

    protected open fun onJumpError(systemError: SystemError, exception: Throwable = RuntimeException(systemError.text)): CommonErrorFragment {
        ToastUtil.showShortToast(systemError.text)
        AppLayerErrorCatcher.throwException(exception)
        return CommonErrorFragment.createInstance(systemError)
    }

    protected abstract fun getCurrentFragment(targetPage: Int, intent: Intent): Fragment

    companion object {

        /**
         * 用来获取目标页面的键。
         */
        private const val KEY_TARGET_PAGE = "key_target_page"
        private const val KEY_PAGE_TITLE = "key_page_title"
        /**
         * 表示当前目标页面为未知的。
         */
        private const val PAGE_UNKNOWN = 0

        fun getJumpIntent(context: Context, activityClass: Class<out BaseFragmentActivity>, targetPage: Int, pageTitle: String = ""): Intent {
            val intent = generateJumpIntent(context, activityClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra(KEY_TARGET_PAGE, targetPage)
            intent.putExtra(KEY_PAGE_TITLE, pageTitle)
            return intent
        }
    }
}