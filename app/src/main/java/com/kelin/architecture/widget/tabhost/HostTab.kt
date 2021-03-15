package com.kelin.architecture.widget.tabhost

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.kelin.architecture.EnvConfig

/**
 * **描述:** 首页导航。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/12 10:13 AM
 *
 * **版本:** v 1.0.0
 */
interface HostTab {
    /**
     * 名称。
     */
    val tabLabel: String

    /**
     * 图标。
     */
    @get:DrawableRes
    val icon: Int

    /**
     * 要加载的页面的字节码。
     */
    val cls: Class<out Fragment>

    /**
     * 标签
     */
    val tag: String
        get() = toString()

    /**
     * 真实的名称。
     */
    val tabName: String
        get() = if (this.tabLabel == "我的") {
            if (EnvConfig.IS_DEBUG) {
                "Debug"
            } else {
                tabLabel
            } + if (EnvConfig.IS_RELEASE) {
                ""
            } else {
                "-" + EnvConfig.getEnvType().alias
            }
        } else {
            tabLabel
        }
}