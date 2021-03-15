package com.kelin.architecture.ui.splash

import android.app.Activity
import android.content.Context
import android.os.Handler
import com.kelin.apkUpdater.util.NetWorkStateUtil
import com.kelin.architecture.Navigator
import com.kelin.architecture.core.proxy.API
import com.kelin.architecture.core.proxy.ProxyFactory
import com.kelin.architecture.tools.statistics.StatisticsHelper
import com.rj.payinjoy.modeltype.AppSignInType

/**
 * **描述:** 启动页的帮助者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/5/16 1:21 PM
 *
 * **版本:** v 1.0.0
 */
class SplashHelper(val context: Context) {

    enum class NextAction {
        NETWORK_ERROR,
        MAIN,
        LOGIN
    }

    companion object {
        /**
         * Splash的最小显示时长，如果低于这个时间则手动延迟至这个时长。
         */
        private const val DURATION: Long = 500

        fun handlerNextAction(activity: Activity, action: NextAction) {
            when (action) {
                NextAction.MAIN -> {
                    Navigator.jumpToMain(activity)
                    activity.finish()
                }
                else -> {
                    Navigator.jumpToAuth(activity)
                    activity.finish()
                }
            }
        }
    }


    /**
     * 用来记录启动时的时间戳。
     */
    private var startTime = 0L
    private var finish: ((NextAction) -> Unit)? = null

    fun initAppSplash(finishListener: (NextAction) -> Unit) {
        finish = finishListener
        if (NetWorkStateUtil.isConnected(context.applicationContext)) {
            startTime = System.currentTimeMillis()
            refreshToken()
        } else {
            finishListener(NextAction.NETWORK_ERROR)
        }
    }

    private fun refreshToken() {
        ProxyFactory.createProxy {
            API.AUTH.refreshToken()
        }.setNotToast()
            .onSuccess {
                StatisticsHelper.onProfileSignIn(context, it.username, AppSignInType.TOKEN)
                onFinished(NextAction.MAIN)
            }
            .onFailed {
                onFinished(NextAction.LOGIN)
            }
            .request()
    }

    private fun onFinished(action: NextAction) {
        val spaceMillis = System.currentTimeMillis() - startTime
        if (spaceMillis + 100 >= DURATION) {
            finish!!(action)
        } else {
            Handler().postDelayed({ finish!!(action) }, DURATION - spaceMillis)
        }
    }
}