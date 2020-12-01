package com.kelin.architecture.data.cache.dp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * **描述:** 用户相关的dao。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/17 3:20 PM
 *
 * **版本:** v 1.0.0
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity")
    fun get(): UserEntity?

    @Insert
    fun insert(user: UserEntity)

    @Query("DELETE FROM UserEntity")
    fun clear()
}