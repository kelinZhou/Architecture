package com.kelin.architecture.data.cache.dp

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.lang.NullPointerException

/**
 * **描述:** 应用中的数据库。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/17 3:14 PM
 *
 * **版本:** v 1.0.0
 */

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {

//        private val migrations1_2 by lazy {
//            object : Migration(1, 2) {
//                override fun migrate(database: SupportSQLiteDatabase) {
//                    database.execSQL("ALTER TABLE UserEntity ADD ncUserId TEXT NOT NULL DEFAULT '';")
//                    database.execSQL("ALTER TABLE UserEntity ADD ncUserName TEXT NOT NULL DEFAULT '';")
//                    database.execSQL("ALTER TABLE UserEntity ADD ncDepartmentId TEXT;")
//                    database.execSQL("ALTER TABLE UserEntity ADD ncDepartmentName TEXT;")
//                    database.execSQL("ALTER TABLE UserEntity ADD platformId TEXT NOT NULL DEFAULT '';")
//                    database.execSQL("ALTER TABLE UserEntity ADD platformName TEXT NOT NULL DEFAULT '';")
//                    database.execSQL("ALTER TABLE UserEntity ADD departmentId TEXT NOT NULL DEFAULT '';")
//                    database.execSQL("ALTER TABLE UserEntity ADD departmentName TEXT NOT NULL DEFAULT '';")
//                }
//            }
//        }

        private const val DATABASE_NAME = "kelin_architecture_room_database"

        private var realDB: AppDatabase? = null

        private val db: AppDatabase
            get() = realDB ?: throw NullPointerException("You got call the init method before use AppDatabase.")

        val userDao: UserDao
            get() = db.userDao

        fun init(context: Context, databaseName: String = DATABASE_NAME) {
            realDB = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, databaseName)
                .allowMainThreadQueries()
//                .addMigrations(migrations1_2)
                .build()
        }
    }

    abstract val userDao: UserDao
}