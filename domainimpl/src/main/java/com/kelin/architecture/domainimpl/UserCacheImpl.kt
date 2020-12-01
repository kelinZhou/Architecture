package com.kelin.architecture.domainimpl

import com.kelin.architecture.data.cache.dp.AppDatabase
import com.kelin.architecture.domain.cache.UserCache
import com.kelin.architecture.domain.model.UserProfile

/**
 * **描述:** 用户缓存的具体实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 9:53 AM
 *
 * **版本:** v 1.0.0
 */
class UserCacheImpl : UserCache {
    override fun get(): UserProfile? {
        return AppDatabase.userDao.get()?.mapToUserProfile()
    }
}