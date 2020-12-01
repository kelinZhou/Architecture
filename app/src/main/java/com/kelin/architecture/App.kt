package com.kelin.architecture

import androidx.multidex.MultiDexApplication
import com.kelin.architecture.core.AppModule

/**
 * **描述:** 应用程序的入口。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 10:17 AM
 *
 * **版本:** v 1.0.0
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        EnvConfig.init(this)
        AppModule.init(this)
    }
}