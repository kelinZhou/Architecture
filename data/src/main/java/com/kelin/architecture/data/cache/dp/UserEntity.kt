package com.kelin.architecture.data.cache.dp

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * **描述:** 用户信息
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/17 3:17 PM
 *
 * **版本:** v 1.0.0
 */
@Entity
class UserEntity (
    /**
     * 用户ID。
     */
    @PrimaryKey val uid: Long,
    /**
     * 用户名。
     */
    val username: String,
    /**
     * 用户头像。
     */
    val avatar: String,
    /**
     * 手机号。
     */
    val phoneNumber: String
)