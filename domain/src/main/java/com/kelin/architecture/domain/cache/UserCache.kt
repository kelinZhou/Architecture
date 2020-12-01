package com.kelin.architecture.domain.cache

import com.kelin.architecture.domain.model.UserProfile

/**
 * **描述:** 用户提供者
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 9:47 AM
 *
 * **版本:** v 1.0.0
 */
interface UserCache : Cache<UserProfile>