package com.kelin.architecture.ui.splash

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.kelin.architecture.R
import com.kelin.architecture.tools.statistics.StatisticsHelper
import com.kelin.architecture.ui.base.BasicActivity
import com.kelin.okpermission.OkPermission

/**
 * **描述:** 启动页。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/15 10:20 AM
 *
 * **版本:** v 1.0.0
 */
class Splash : BasicActivity() {

    private val splashHelper by lazy { SplashHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT < 19) {
            View.GONE
        } else {
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.color.common_window_background
            )
        )
        setContentView(R.layout.activity_splash)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        StatisticsHelper.init(applicationContext) //初始化统计
        tryApplyPermissions()
    }

    private fun tryApplyPermissions() {
        OkPermission.with(this)
            .addWeakPermissions(Manifest.permission.READ_PHONE_STATE)
            .checkAndApply { _, _ ->
                splashHelper.initAppSplash { action ->
                    handlerNextAction(action)
                }
            }
    }

    private fun handlerNextAction(action: SplashHelper.NextAction) {
        if (GuideHelper.isFirstOpen(applicationContext)) {
            replaceFragment(R.id.flFragmentContainer, GuideHelper.createInstance(action))
        } else {
            SplashHelper.handlerNextAction(this, action)
        }
    }
}