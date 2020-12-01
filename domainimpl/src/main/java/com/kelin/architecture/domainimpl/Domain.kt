package com.kelin.architecture.domainimpl

import android.content.Context
import com.kelin.architecture.data.cache.dp.AppDatabase

/**
 * **描述:** 中间层
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 2:01 PM
 *
 * **版本:** v 1.0.0
 */
object Domain {
    fun init(context: Context) {
        AppDatabase.init(context)
    }
}