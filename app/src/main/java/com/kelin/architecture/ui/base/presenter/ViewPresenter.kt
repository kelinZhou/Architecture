package com.kelin.architecture.ui.base.presenter

import android.content.Context
import com.kelin.architecture.ui.base.delegate.ViewDelegate

/**
 * **描述:** 负责业务逻辑的处理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/3/29  1:20 PM
 *
 * **版本:** v 1.0.0
 */
interface ViewPresenter<VC : ViewDelegate.ViewDelegateCallback> {

    fun getContext(): Context?

    val viewCallback: VC
}