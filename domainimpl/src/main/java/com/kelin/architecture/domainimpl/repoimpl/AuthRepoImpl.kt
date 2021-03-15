package com.kelin.architecture.domainimpl.repoimpl

import android.content.Context
import com.kelin.architecture.data.api.AuthApi
import com.kelin.architecture.data.cache.dp.AppDatabase
import com.kelin.architecture.domain.croe.repo.AuthRepo
import com.kelin.architecture.domain.model.UserProfile
import io.reactivex.Observable
import java.lang.RuntimeException

/**
 * **描述:** 用户相关的数据仓库实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-16 13:55
 *
 * **版本:** v 1.0.0
 */
class AuthRepoImpl(context: Context, baseUrl: String) : AbsApi(context, baseUrl), AuthRepo {

    private val api by lazy { getApiService(AuthApi::class.java) }

    override fun refreshToken(): Observable<UserProfile> {
        return Observable.error(RuntimeException())
    }

    override fun logout(): Observable<Boolean> {
        AppDatabase.userDao.clear()//清除缓存的用户信息
        return Observable.just(true)
    }
}