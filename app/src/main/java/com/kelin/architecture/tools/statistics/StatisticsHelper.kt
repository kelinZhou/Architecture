package com.kelin.architecture.tools.statistics

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.kelin.architecture.core.AppModule

/**
 * **描述:** 统计的帮助类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-05  18:06
 *
 * **版本:** v 1.0.0
 */
object StatisticsHelper {

    /**
     * 统计的初始化，如果不是在Application的onCreate中调用的就必须在Application的onCreate中先调用```StatisticsHelper.preInit(this)```方法。
     */
    fun init(context: Context) {
//        val env = EnvConfig.getEnv()
//        UMConfigure.init(context, env.UM_APP_KEY, BuildConfig.CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "")
//        UMConfigure.setLogEnabled(EnvConfig.IS_DEBUG)
//        PlatformConfig.setWeixin(env.WE_CHAT_APP_ID, env.WE_CHAT_APP_SECRET)
//        PlatformConfig.setWXFileProvider("${context.packageName}.fileProvider")
//        PlatformConfig.setQQZone(env.QQ_APP_ID, env.QQ_APP_KEY)
//        PlatformConfig.setQQFileProvider("${context.packageName}.fileProvider")
    }

    /**
     * 统计用户登录。
     */
    fun onProfileSignIn(context: Context, userId: String, type: SignInType) {
//        MobclickAgent.onProfileSignIn(type.signInType, userId)
//        StatService.setUserId(context, userId)
    }

    /**
     * 统计用户退出登录。
     */
    fun onProfileSignOut(context: Context) {
//        MobclickAgent.onProfileSignOff()
//        StatService.setUserId(context, null)
    }

    fun onActivityResume(context: Activity) {
//        MobclickAgent.onResume(context)
    }

    fun onActivityPause(context: Activity) {
//        MobclickAgent.onPause(context)
    }

    /**
     * 统计页面启动。
     */
    fun onPageResume(fragment: Fragment, pageName: String = fragment.javaClass.simpleName) {
        //友盟统计
//        MobclickAgent.onPageStart(pageName)
        //百度统计
//        StatService.onPageStart(fragment.requireContext(), pageName)
    }

    /**
     * 统计页面结束。
     */
    fun onPagePause(fragment: Fragment, pageName: String = fragment.javaClass.simpleName) {
        //友盟统计
//        MobclickAgent.onPageEnd(pageName)
        //百度统计
//        StatService.onPageEnd(fragment.requireContext(), pageName)
    }

    /**
     * 自定义计数事件。
     */
    fun onEvent(action: StatisticsEvents, label: Int) {
        onEvent(action, label.toString())
    }

    /**
     * 自定义计数事件。
     */
    fun onEvent(action: StatisticsEvents, label: String? = null) {
        if (label.isNullOrEmpty()) {
//            MobclickAgent.onEvent(AppModule.getContext(), action.eventId)
        } else {
//            MobclickAgent.onEvent(AppModule.getContext(), action.eventId, label)
        }
    }

    /**
     * 自定义计数事件。
     */
    fun onEvent(action: StatisticsEvents, duration: Long) {
//        MobclickAgent.onEventValue(AppModule.getContext(), action.eventId, null, (duration / 1000).toInt())
    }
}