package com.kelin.architecture.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Parcelable
import androidx.core.net.ConnectivityManagerCompat
import com.kelin.architecture.core.AppModule

/**
 * **描述:** 网络状态相关的工具类。
 *
 *
 * **创建人:** kelin
 *
 *
 * **创建时间:** 2020-03-09 09:46
 *
 *
 * **版本:** v 1.0.0
 */
class NetWorkStateUtil private constructor() {

    /**
     * 网络改变广播。
     */
    abstract class ConnectivityChangeReceiver : BroadcastReceiver() {
        var isRegister = false
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            val networkInfo = if (extras == null) null else extras.getParcelable<Parcelable>(ConnectivityManager.EXTRA_NETWORK_INFO) as NetworkInfo?
            if (networkInfo != null) {
                if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                    val type = networkInfo.type
                    if (networkInfo.isConnected) {
                        if (type == ConnectivityManager.TYPE_WIFI && !sIsWifiConnected) {
                            sIsWifiConnected = true
                            onConnected(ConnectivityManager.TYPE_WIFI)
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            sIsWifiConnected = false
                            onConnected(ConnectivityManager.TYPE_MOBILE)
                        }
                    } else {
                        sIsWifiConnected = false
                        if (!isConnected) {
                            onDisconnected(type)
                        }
                    }
                }
            }
        }

        /**
         * 当链接断开的时候执行。如果该方法被执行就说明当前已经没有任何可以使用网络了。
         *
         * @param type 表示当前断开链接的类型，是WiFi还是流量。如果为 [ConnectivityManager.TYPE_WIFI] 则说明当前断开链接
         * 的是WiFi，如果为 [ConnectivityManager.TYPE_MOBILE] 则说明当前断开链接的是流量。
         */
        protected abstract fun onDisconnected(type: Int)

        /**
         * 当链接成功后执行。
         *
         * @param type 表示当前链接的类型，是WiFi还是流量。如果为 [ConnectivityManager.TYPE_WIFI] 则说明当前链接
         * 成功的是WiFi，如果为 [ConnectivityManager.TYPE_MOBILE] 则说明当前链接成功的是流量。
         */
        protected abstract fun onConnected(type: Int)

        companion object {
            val FILTER = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        }
    }

    companion object {

        private val context: Context
            get() = AppModule.getContext()

        private var sIsWifiConnected = false

        /**
         * 检查当前WIFI是否连接，两层意思——是否连接，连接是不是WIFI
         *
         * @return true 表示当前网络处于连接状态，且是WIFI，否则返回false。
         */
        @get:SuppressLint("MissingPermission")
        val isWifiConnected: Boolean
            get() {
                val info = getConnectivityManager(context).activeNetworkInfo
                return info != null && info.isConnected && ConnectivityManager.TYPE_WIFI == info.type
            }

        /**
         * 检查当前GPRS是否连接，两层意思——是否连接，连接是不是GPRS
         *
         * @return true 表示当前网络处于连接状态，且是GPRS，否则返回false。
         */
        @get:SuppressLint("MissingPermission")
        val isGprsConnected: Boolean
            get() {
                val info = getConnectivityManager(context).activeNetworkInfo
                return info != null && info.isConnected && ConnectivityManager.TYPE_MOBILE == info.type
            }

        /**
         * 检查当前是否连接。
         *
         * @return true 表示当前网络处于连接状态，否则返回false。
         */
        @get:SuppressLint("MissingPermission")
        val isConnected: Boolean
            get() {
                val info = getConnectivityManager(context).activeNetworkInfo
                return info != null && info.isConnected
            }

        /**
         * 对大数据传输时，需要调用该方法做出判断，如果流量敏感，应该提示用户。
         *
         * @return true表示流量敏感，false表示不敏感。
         */
        @get:SuppressLint("MissingPermission")
        val isActiveNetworkMetered: Boolean
            get() = ConnectivityManagerCompat.isActiveNetworkMetered(getConnectivityManager(context))// 反射失败，默认开启

        /**
         * 移动流量开关是否被打开。
         *
         * @return 返回true表示流量是开启的，false表示是关闭的。注意开启并不代表当前的网络连接就是移动流量，只是单单开启了开关而已。
         */
        @get:SuppressLint("DiscouragedPrivateApi")
        val isMobileEnabled: Boolean
            get() {
                try {
                    val getMobileDataEnabledMethod = ConnectivityManager::class.java.getDeclaredMethod("getMobileDataEnabled")
                    getMobileDataEnabledMethod.isAccessible = true
                    return getMobileDataEnabledMethod.invoke(getConnectivityManager(context)) as Boolean
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // 反射失败，默认开启
                return true
            }

        private fun getConnectivityManager(context: Context): ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        /**
         * 注册网络改变广播。
         *
         * @param receiver [ConnectivityChangeReceiver] 广播对象。
         */
        fun registerReceiver(receiver: ConnectivityChangeReceiver): Intent? {
            receiver.isRegister = true
            return context.registerReceiver(receiver, ConnectivityChangeReceiver.FILTER)
        }

        /**
         * 反注册网络改变广播。
         *
         * @param receiver 已经被注册过的 [ConnectivityChangeReceiver] 广播对象。
         */
        fun unregisterReceiver(receiver: ConnectivityChangeReceiver) {
            context.unregisterReceiver(receiver)
            receiver.isRegister = false
        }
    }

    init {
        throw InstantiationError("Utility class don't need to instantiate！")
    }
}