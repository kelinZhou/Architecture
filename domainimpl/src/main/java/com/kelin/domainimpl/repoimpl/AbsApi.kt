package com.kelin.domainimpl.repoimpl

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kelin.architecture.data.core.HttpLogInterceptor
import com.kelin.architecture.data.core.NoneNullGsonTypeAdapter
import com.kelin.architecture.data.core.ResponseConverterFactory
import com.kelin.architecture.data.token.AppTokenManager
import com.kelin.architecture.data.util.sp.SpUtil
import com.kelin.architecture.domain.croe.exception.ApiException
import io.reactivex.Observable
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * **描述:** 用来描述所有API的特性。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019/4/15  4:01 PM
 *
 * **版本:** v 1.0.0
 */
abstract class AbsApi(
    /**
     * 上下文。
     */
    protected val context: Context,
    /**
     * 服务器地址。
     */
    baseUrl: String
) {

    companion object {
        private const val KEY_CURRENT_USER_ID = "key_current_user_id"
    }

    private val headerInterceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", AppTokenManager.instance.getToken(context))
                .header("appVersion", context.packageManager.getPackageInfo(context.packageName, 0)?.versionName ?: "")
                .header("client", "android")
                .method(original.method(), original.body())
                .build()

            chain.proceed(request)
        }
    }

    protected val okHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, 0x0640_0000))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLogInterceptor())
            .addInterceptor(headerInterceptor)
            .build()
    }

    private val defGson: Gson by lazy {
        GsonBuilder().apply {
            registerTypeAdapterFactory(NoneNullGsonTypeAdapter())
        }.create()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ResponseConverterFactory.create(defGson))
            .build()
    }

    /**
     * 保存以及获取当前用户的Id。
     */
    protected var currentUserId: Long? = null
        get() {
            if (field == null) {
                field = SpUtil.getLong(context, KEY_CURRENT_USER_ID, -1).let { if (it < 0) null else it }
            }
            return field
        }
        set(value) {
            if (value == null) {
                SpUtil.remove(context, KEY_CURRENT_USER_ID)
            } else {
                SpUtil.putLong(context, KEY_CURRENT_USER_ID, value)
            }
            field = value
        }

    protected fun <S> getApiService(api: Class<S>, service: Retrofit = retrofit): S {
        return service.create(api)
    }

    protected fun <F> createField(fieldName: String, value: F): RequestBody {
        return createFields(Pair(fieldName, value))
    }

    protected fun <F> createFields(vararg pairs: Pair<String, F>): RequestBody {
        return if (pairs.isEmpty()) {
            throw NullPointerException("The argument must not be null!!")
        } else {
            RequestBody.create(MediaType.parse("application/json"), Gson().toJson(mapOf(*pairs)))
        }
    }

    protected fun createPageFieldsPair(page: Int, size: Int): Array<Pair<String, Int>> {
        return arrayOf(
            Pair("pageNum", page),
            Pair("pageSize", size)
        )
    }

    protected fun createPageFields(page: Int, size: Int): RequestBody {
        return createFields(Pair("pageNum", page), Pair("pageSize", size))
    }

    protected fun createBody(body: Any): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), Gson().toJson(body))
    }

    protected fun <T> apiError(msg: String): Observable<T> {
        return Observable.just(1).map { throw ApiException(ApiException.Error.PERMISSION_ERROR.code, msg) }
    }


    protected fun serviceError(error: ApiException.Error = ApiException.Error.SERVICE_ERROR): Nothing = throw ApiException(error)
}