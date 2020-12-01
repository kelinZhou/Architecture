package com.kelin.domainimpl.repoimpl

import android.content.Context
import com.kelin.architecture.data.api.CommonApi
import com.kelin.architecture.domain.croe.repo.CommonRepo

/**
 * **描述:** 通用的数据仓库。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 14:35
 *
 * **版本:** v 1.0.0
 */
class CommonRepoImpl(context: Context, baseUrl: String) : AbsApi(context, baseUrl), CommonRepo {

    private val api by lazy { getApiService(CommonApi::class.java) }

}