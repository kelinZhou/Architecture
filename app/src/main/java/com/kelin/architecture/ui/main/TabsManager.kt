package com.kelin.architecture.ui.main

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.kelin.architecture.R
import com.kelin.architecture.widget.tabhost.HostTab

/**
 * **描述:** 客户经理角色下首页的Tab。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/15 3:32 PM
 *
 * **版本:** v 1.0.0
 */
enum class TabsManager(val index: Int, override val tabLabel: String, override val cls: Class<out Fragment>, @DrawableRes override val icon: Int) : HostTab {
    HOME(0, "首页", HomeFragment::class.java, R.drawable.selector_tab_main_home),
    PROJECT(1, "项目", HomeFragment::class.java, R.drawable.selector_tab_main_home),
    ME(2, "我的", HomeFragment::class.java, R.drawable.selector_tab_main_home),
}