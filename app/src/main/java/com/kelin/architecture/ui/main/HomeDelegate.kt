package com.kelin.architecture.ui.main

import com.kelin.architecture.R
import com.kelin.architecture.ui.base.delegate.BaseViewDelegate


/**
 * **描述:** 首页。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-03-15 15:40:15
 *
 * **版本:** v 1.0.0
 */
class HomeDelegate : BaseViewDelegate<HomeDelegate.Callback>() {

    override val rootLayoutId = R.layout.fragment_home

    interface Callback : BaseViewDelegateCallback
}
