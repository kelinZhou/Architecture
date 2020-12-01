package com.kelin.architecture.data.util

import android.util.Log
import java.util.Hashtable

/**
 * **描述:** 日志工具。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/3/26  8:54 PM
 *
 * **版本:** v 1.0.0
 */
class LogHelper private constructor(
    /**
     * 开发者。
     */
    private val developer: DEVELOPER
) {


    /**
     * 获取当前方法的详细信息
     * 具体到方法名、方法行，方法所在类的文件名
     *
     * @return
     */
    private//本地方法native  jni
    //线程
    //构造方法
    val functionName: String?
        get() {
            val sts = Thread.currentThread().stackTrace ?: return null
            for (st in sts) {
                if (st.isNativeMethod) {
                    continue
                }
                if (st.className == Thread::class.java.name) {
                    continue
                }
                if (st.className == this.javaClass.name) {
                    continue
                }
                val split = st.className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val className = if (split.isEmpty()) st.className else split[split.size - 1]
                return ("[ Thread: " + Thread.currentThread().name + " Class: "
                        + className + " Line:" + st.lineNumber + " Method: "
                        + st.methodName + " ]")
            }
            return null
        }

    private enum class DEVELOPER constructor(val devName: String, val tag: String) {
        YUAN_ZHEN("yuanzhen", "@yuanzhen@:"),
        SU_MING_HUI("suminghui", "@suminghui@"),
        KELIN("kelin", "@Kelin@:"),
        SYSTEM("system", "@system@:");
    }

    private fun showLog(): Boolean {
        // 只有系统和本人的日志才能输出
        return !sIsFilterOtherDeveloperLog || sCurDeveloperName.equals(
            developer.devName,
            ignoreCase = true
        ) || DEVELOPER.SYSTEM.devName == developer.devName
    }


    fun i(msg: String) {
        i(null, msg)
    }

    fun i(tag: String?, msg: String) {
        if (sIsDebug && showLog() && msg.isNotEmpty()) {
            Log.i(tag + "   [+" + developer.tag + "]", "$msg--$functionName")
        }
    }

    fun d(tag: String, msg: String) {
        if (tag.isEmpty() || msg.isEmpty()) {
            return
        }

        if (sIsDebug && showLog()) {
            Log.d(tag + "   [+" + developer.tag + "]", msg)
        }
    }

    fun d(msg: String) {
        if (msg.isEmpty()) {
            return
        }
        if (sIsDebug && showLog()) {
            Log.d("[+" + developer.tag + "]", "$msg--$functionName")
        }
    }

    fun e(tag: String, msg: String) {
        if (tag.isEmpty() || msg.isEmpty()) {
            return
        }
        if (sIsDebug && showLog()) {
            Log.e(tag + "   [+" + developer.tag + "]", msg)
        }
    }

    fun e(msg: String) {
        if (msg.isEmpty()) {
            return
        }
        if (sIsDebug && showLog()) {
            Log.e("[+" + developer.tag + "]", "$msg--$functionName")
        }
    }


    fun e(tag: String, msg: String, e: Exception) {
        if (tag.isEmpty() || msg.isEmpty()) {
            return
        }

        if (sIsDebug && showLog()) {
            Log.e(tag + "   [+" + developer.tag + "]", msg, e)
        }
    }

    companion object {
        /**
         * 过滤其他开发者日志
         */
        private const val FILTER_OTHER_DEVELOPER_LOG = true

        private var sIsDebug = true
        private var sCurDeveloperName: String = ""
        private var sIsFilterOtherDeveloperLog = true
        /**
         * 表示输出方法信息及位置。
         */
        private var sLogMethodInfo = true
        private val sLoggerTable = Hashtable<String, LogHelper>()

        /**
         * 初始化函数。需要在Application启动的时候进行初始化。
         *
         * @param developerName           开发者名称，也是电脑名称。如果你的电脑没有设置过名称请设置一下。
         * @param isDebug                 当前是否是debug模式。
         * @param logMethodInfo           是否打印日志所处的方法的信息。
         * @param filterOtherDeveloperLog 是否过滤其他开发者日志，如果为true表示你讲看不到其他开发者的日志，false则可以。默认为true。
         */
        @JvmOverloads
        fun init(
            developerName: String,
            isDebug: Boolean,
            logMethodInfo: Boolean = sLogMethodInfo,
            filterOtherDeveloperLog: Boolean = FILTER_OTHER_DEVELOPER_LOG
        ) {
            sCurDeveloperName = developerName
            sIsDebug = isDebug
            sLogMethodInfo = logMethodInfo
            sIsFilterOtherDeveloperLog = filterOtherDeveloperLog
        }

        val yuan: LogHelper
            get() = getLogger(DEVELOPER.YUAN_ZHEN)

        val kelin: LogHelper
            get() = getLogger(DEVELOPER.KELIN)

        val su: LogHelper
            get() = getLogger(DEVELOPER.SU_MING_HUI)

        val system: LogHelper
            get() = getLogger(DEVELOPER.SYSTEM)

        private fun getLogger(developer: DEVELOPER): LogHelper {
            var classLogger = sLoggerTable[developer.tag]
            if (classLogger == null) {
                classLogger = LogHelper(developer)
                sLoggerTable[developer.tag] = classLogger
            }
            return classLogger
        }


        fun logAll(tag: String, message: String) {
            val sb = StringBuffer()
            val sts = Thread.currentThread().stackTrace
            for (i in sts.indices) {
                val methodName = sts[i].methodName
                val className = sts[i].className
                val lineNumber = sts[i].lineNumber
                sb.append(methodName).append("(").append(className).append("-").append(lineNumber).append(");")
                    .append("\n")
            }
            Log.i(tag, "$message-->$sb")
        }
    }
}
