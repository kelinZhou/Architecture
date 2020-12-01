package com.kelin.architecture.domainimpl.repoimpl

import android.content.Context
import com.kelin.architecture.data.api.LogicApi
import com.kelin.architecture.domain.croe.repo.LogicRepo

/**
 * **描述:** 与业务逻辑相关的数据仓库。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 14:34
 *
 * **版本:** v 1.0.0
 */
class LogicRepoImpl(context: Context, baseUrl: String) : AbsApi(context, baseUrl), LogicRepo {

    private val api by lazy { getApiService(LogicApi::class.java) }

}
