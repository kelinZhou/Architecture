package com.kelin.architecture.domain.croe.repo

import io.reactivex.Observable


/**
 * **描述:** 用户体系相关的仓库。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/9  9:44 AM
 *
 * **版本:** v 1.0.0
 */
interface AuthRepo : Repo {
    fun logout(): Observable<Boolean>
}
