package com.kelin.architecture.user

import com.kelin.architecture.domain.cache.UserCache
import com.kelin.architecture.domain.model.UserProfile
import com.kelin.architecture.domainimpl.UserCacheImpl


/**
 * **描述:** 用户信息管理。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-03  15:50
 *
 * **版本:** v 1.0.0
 */
class UserManager private constructor() {
    companion object {

        val instance: UserManager
                by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { UserManager() }
    }


    private val cache: UserCache by lazy { UserCacheImpl() }

    private var mUser: UserProfile? = null
        get() {
            if (field == null) {
                field = cache.get()
            }
            return field
        }

    val user: UserProfile
        get() = mUser ?: UserProfile.emptyInstance

    val userOrNull: UserProfile?
        get() = mUser

    val hasUserProfile: Boolean
        get() = mUser != null

}