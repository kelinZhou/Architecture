package com.kelin.architecture.domain.model

/**
 * **描述:** 用户信息。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/1 9:43 AM
 *
 * **版本:** v 1.0.0
 */
data class UserProfile(

    /**
     * 用户ID。
     */
    val uid: Long,
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
) {
    companion object{
        val emptyInstance: UserProfile = UserProfile(0, "", "", "")
    }
}