package com.kelin.architecture.data.token

import android.content.Context

/**
 * **描述:** Token的管理工具。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/5/21  1:40 PM
 *
 * **版本:** v 1.0.0
 */
interface TokenManager {

    /**
     * Save one token.
     * @param context The application context.
     * @param token your token.
     */
    fun saveToken(context: Context, token: String)

    /**
     * Save one token asynchronously.
     * @param context The application context.
     * @param token your token.
     */
    fun asyncSaveToken(context: Context, token: String)

    /**
     * Get your saved token, if you got a empty string that means you not saved anything token.
     * @param context The application context.
     */
    fun getToken(context: Context): String

    /**
     * Remove token you have saved.
     * @param context The application context.
     */
    fun removeToken(context: Context)

    /**
     * Remove token you have saved asynchronously.
     * @param context The application context.
     */
    fun asyncRemoveToken(context: Context)
}