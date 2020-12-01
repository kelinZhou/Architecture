package com.kelin.architecture.ui.base.delegate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kelin.architecture.ui.base.presenter.ViewPresenter
import kotlinx.android.extensions.LayoutContainer

/**
 * **描述:** 负责View操作的代理，主要负责处理交互逻辑。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/29  1:23 PM
 *
 * **版本:** v 1.0.0
 */
interface ViewDelegate<P : ViewPresenter<out ViewDelegate.ViewDelegateCallback>> : LayoutContainer {

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    fun bindView(viewPresenter: P)

    fun restoreInstanceState(savedInstanceState: Bundle?)

    fun saveInstanceState(outState: Bundle)

    fun unbindView()

    fun destroyView(outState: Bundle?)

    fun presentView(viewPresenter: P, savedInstanceState: Bundle?)

    fun postDelayed(delayMillis: Long, runner: () -> Unit)

    interface ViewDelegateCallback
}
