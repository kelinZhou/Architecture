package com.kelin.architecture.domainimpl.repoimpl

import android.content.Context
import com.kelin.architecture.domain.croe.repo.UpdateRepo
import com.kelin.architecture.data.api.UpdateApi

/**
 * **描述:** 通用的数据仓库。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 14:35
 *
 * **版本:** v 1.0.0
 */
class UpdateRepoImpl(context: Context, baseUrl: String) : AbsApi(context, baseUrl), UpdateRepo {

    private val api by lazy { getApiService(UpdateApi::class.java) }

}