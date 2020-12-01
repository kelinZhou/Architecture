package com.kelin.architecture.data.token

import android.content.Context
import com.kelin.architecture.data.util.sp.SecurePreferences
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * **描述:** 已SecurePreferences为存储介质的TokenManager。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/5/21  1:44 PM
 *
 * **版本:** v 1.0.0
 */
abstract class SecurePreferencesTokenManager : TokenManager {

    private var currentToken: String? = null
    private val taskExecutor: Executor by lazy { Executors.newSingleThreadExecutor() }

    abstract val tokenKey: String

    override fun saveToken(context: Context, token: String) {
        currentToken = token
        getPreferences(context).edit().putString(tokenKey, token).apply()
    }

    override fun asyncSaveToken(context: Context, token: String) {
        taskExecutor.execute {
            currentToken = token
            getPreferences(context).edit().putString(tokenKey, token).apply()
        }
    }

    override fun getToken(context: Context): String {
        if (currentToken == null) {
            currentToken = getPreferences(context).getString(tokenKey, "")
        }
        return currentToken!!
    }

    override fun removeToken(context: Context) {
        currentToken = null
        getPreferences(context).edit().remove(tokenKey).apply()
    }

    override fun asyncRemoveToken(context: Context) {
        taskExecutor.execute {
            currentToken = null
            getPreferences(context).edit().remove(tokenKey).apply()
        }
    }

    private fun getPreferences(context: Context): SecurePreferences {
        return SecurePreferences(context, "com.kelin.architecture", "com_kelin_architecture_SecureTokenSharedPreferences")
    }
}