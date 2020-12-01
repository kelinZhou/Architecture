package com.kelin.architecture.core.proxy

import android.util.SparseArray
import com.kelin.architecture.EnvConfig
import com.kelin.architecture.core.AppModule
import com.kelin.architecture.core.proxy.executor.JobExecutor
import com.kelin.architecture.core.proxy.executor.ThreadExecutor
import com.kelin.architecture.core.proxy.executor.UIThread
import com.kelin.architecture.domain.croe.repo.*
import com.kelin.domainimpl.repoimpl.AuthRepoImpl
import com.kelin.domainimpl.repoimpl.CommonRepoImpl
import com.kelin.domainimpl.repoimpl.LogicRepoImpl
import com.kelin.domainimpl.repoimpl.UpdateRepoImpl

/**
 * **描述:** 获取数据的核心类。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/9  9:44 AM
 *
 * **版本:** v 1.0.0
 */
object API {
    private val repoCache = SparseArray<Repo>()

    val AUTH: AuthRepo
        get() = getRepo(Repo.Type.AUTH)
    val LOGIC: LogicRepo
        get() = getRepo(Repo.Type.LOGIC)
    val COMMON: CommonRepo
        get() = getRepo(Repo.Type.COMMON)
    val UPDATE: UpdateRepo
        get() = getRepo(Repo.Type.UPDATE)


    val provideThreadExecutor: ThreadExecutor = JobExecutor()
    val postExecutionThread: UIThread = UIThread()

    fun clear() {
        clearRepo()

        if (provideThreadExecutor is JobExecutor) {
            provideThreadExecutor.shutdown()
        }
        IdActionDataProxy.clearUseCase()
    }

    private fun clearRepo() {
        repoCache.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <API : Repo> getRepo(apiType: Repo.Type): API {
        var repo = repoCache[apiType.code]
        if (repo == null) {
            val context = AppModule.getContext()
            val baseUrl = EnvConfig.getEnv().API_BASE_URL
            repo = when (apiType) {
                Repo.Type.AUTH -> AuthRepoImpl(context, baseUrl)
                Repo.Type.LOGIC -> LogicRepoImpl(context, baseUrl)
                Repo.Type.COMMON -> CommonRepoImpl(context, baseUrl)
                Repo.Type.UPDATE -> UpdateRepoImpl(context, baseUrl)
            }
            repoCache.put(apiType.code, repo)
        }
        return repo as API
    }


}