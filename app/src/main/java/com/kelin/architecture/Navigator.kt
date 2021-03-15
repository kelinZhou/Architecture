package com.kelin.architecture

import android.content.Context
import com.kelin.architecture.ui.main.MainActivity

/**
 * **描述:** 统一处理页面导航，方便日后维护。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 15:51
 *
 * **版本:** v 1.0.0
 */
object Navigator {

    /**
     * 跳转到主页。
     */
    fun jumpToMain(context: Context, defIndex: Int = 0) {
        MainActivity.start(context, defIndex)
    }

    /**
     * 跳转到登录页面。
     */
    fun jumpToAuth(context: Context) {
        jumpToMain(context)
    }
}