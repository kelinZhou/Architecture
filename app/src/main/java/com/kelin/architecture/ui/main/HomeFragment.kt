package com.kelin.architecture.ui.main

import com.kelin.architecture.ui.base.presenter.BaseFragmentPresenter
import com.kelin.architecture.ui.base.presenter.CommonFragmentPresenter
import io.reactivex.Observable


/**
 * **描述:** 首页。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021-03-15 15:40:15
 *
 * **版本:** v 1.0.0
 */
class HomeFragment : BaseFragmentPresenter<HomeDelegate, HomeDelegate.Callback>() {


    override val viewCallback: HomeDelegate.Callback by lazy { HomeDelegateCallback() }

    private inner class HomeDelegateCallback :  HomeDelegate.Callback
}
