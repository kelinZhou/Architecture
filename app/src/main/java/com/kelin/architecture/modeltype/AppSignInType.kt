package com.rj.payinjoy.modeltype

import com.kelin.architecture.tools.statistics.SignInType


/**
 * **描述:** 登录类型。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/12/11 10:58 AM
 *
 * **版本:** v 1.0.0
 */
enum class AppSignInType(override val type: String) : SignInType {
    /**
     * token登录。
     */
    TOKEN("Token"),

    /**
     * 手机号登录。
     */
    PHONE("Phone"),

    /**
     * 账号密码登录。
     */
    PSW("Password");
}
