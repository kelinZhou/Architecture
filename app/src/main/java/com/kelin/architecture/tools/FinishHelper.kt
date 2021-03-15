package com.kelin.architecture.tools

import com.kelin.architecture.util.ToastUtil

/**
 * **描述: ** 页面销毁工具。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2018/6/20  上午10:43
 *
 * **版本: ** v 1.0.0
 */
class FinishHelper private constructor() {
    private var createAt: Long = 0

    fun canFinish(): Boolean {
        val curTime = System.currentTimeMillis()
        return if (curTime - createAt <= 1000) {
            ToastUtil.cancel()
            true
        } else {
            ToastUtil.showShortToast("再按一次退出程序")
            createAt = curTime
            false
        }
    }

    companion object {
        fun createInstance(): FinishHelper {
            return FinishHelper()
        }
    }
}
