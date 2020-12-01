package com.kelin.architecture.domainimpl

import com.kelin.architecture.data.cache.dp.UserEntity
import com.kelin.architecture.domain.model.UserProfile

/**
 * **描述:** data层与domain层数据模型的相互映射。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 9:57 AM
 *
 * **版本:** v 1.0.0
 */

fun UserEntity.mapToUserProfile(): UserProfile {
    return UserProfile(uid, username, avatar, phoneNumber)
}