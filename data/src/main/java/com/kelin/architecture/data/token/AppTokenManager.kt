package com.kelin.architecture.data.token

/**
 * **描述:** App的Token管理工具。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/5/21  2:02 PM
 *
 * **版本:** v 1.0.0
 */
class AppTokenManager private constructor(override val tokenKey: String = "com_kelin_architecture_key_authorization") : SecurePreferencesTokenManager() {
    companion object {
        val instance: TokenManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AppTokenManager() }
    }
}