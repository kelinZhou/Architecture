package com.kelin.architecture.ui.splash

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager

/**
 * **描述:** 引导页的帮助工具。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/1/6 5:14 PM
 *
 * **版本:** v 1.0.0
 */
object GuideHelper {
    /**
     * 当前引导页的版本，如果再某次升级后需要显示引导页，则需要加大版本号。
     */
    private const val VERSION = 1

    /**
     * 用来获取点击进入App按钮之后的Action。
     */
    private const val KEY_NEXT_ACTION = "key_next_action"

    /**
     * 用来获取上一个引导页版本号的键。
     */
    private const val KEY_LAST_VERSION = "key_last_guide_version_code"

    fun createInstance(action: SplashHelper.NextAction): GuideFragment {
        val fragment = GuideFragment()
        val bundle = Bundle()
        bundle.putSerializable(KEY_NEXT_ACTION, action)
        fragment.arguments = bundle
        return fragment
    }

    fun isFirstOpen(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val versionCode = sp.getInt(KEY_LAST_VERSION, -1)
        return versionCode < VERSION
    }

    fun setNotFirstOpen(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(KEY_LAST_VERSION, VERSION)
            .apply()
    }

    fun getNextAction(arguments: Bundle?): SplashHelper.NextAction {
        return arguments?.getSerializable(KEY_NEXT_ACTION) as? SplashHelper.NextAction
            ?: throw RuntimeException("You must set the next action!")
    }
}