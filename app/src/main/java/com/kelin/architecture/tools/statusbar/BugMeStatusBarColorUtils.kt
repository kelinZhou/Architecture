package com.kelin.architecture.tools.statusbar

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.kelin.architecture.tools.AppLayerErrorCatcher
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * 摘自魅族开发文档
 * 2017年05月19日17:04:47
 */
object BugMeStatusBarColorUtils {
    private var mSetStatusBarColorIcon: Method? = null
    private var mSetStatusBarDarkIcon: Method? = null
    private var mStatusBarColorFiled: Field? = null
    private var SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0

    init {
        try {
            mSetStatusBarColorIcon =
                Activity::class.java.getMethod("setStatusBarDarkIcon", Int::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            AppLayerErrorCatcher.throwException(e)
        }

        try {
            mSetStatusBarDarkIcon =
                Activity::class.java.getMethod("setStatusBarDarkIcon", Boolean::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            AppLayerErrorCatcher.throwException(e)
        }

        try {
            mStatusBarColorFiled = WindowManager.LayoutParams::class.java.getField("statusBarColor")
        } catch (e: NoSuchFieldException) {
            AppLayerErrorCatcher.throwException(e)
        }

        try {
            val field = View::class.java.getField("SYSTEM_UI_FLAG_LIGHT_STATUS_BAR")
            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = field.getInt(null)
        } catch (e: Exception) {
            AppLayerErrorCatcher.throwException(e)
        }

    }

    /**
     * 判断颜色是否偏黑色
     *
     * @param color 颜色
     * @param level 级别
     * @return
     */
    fun isBlackColor(color: Int, level: Int): Boolean {
        val grey = toGrey(color)
        return grey < level
    }

    /**
     * 颜色转换成灰度值
     *
     * @param rgb 颜色
     * @return　灰度值
     */
    fun toGrey(rgb: Int): Int {
        val blue = rgb and 0x000000FF
        val green = rgb and 0x0000FF00 shr 8
        val red = rgb and 0x00FF0000 shr 16
        return red * 38 + green * 75 + blue * 15 shr 7
    }

    /**
     * 设置状态栏字体图标颜色
     *
     * @param activity 当前activity
     * @param color    颜色
     */
    fun setStatusBarDarkIcon(activity: Activity, color: Int) {
        if (mSetStatusBarColorIcon != null) {
            try {
                mSetStatusBarColorIcon!!.invoke(activity, color)
            } catch (e: IllegalAccessException) {
                AppLayerErrorCatcher.throwException(e)
            } catch (e: InvocationTargetException) {
                AppLayerErrorCatcher.throwException(e)
            }

        } else {
            val whiteColor = isBlackColor(color, 50)
            if (mStatusBarColorFiled != null) {
                setStatusBarDarkIcon(
                    activity,
                    whiteColor,
                    whiteColor
                )
                setStatusBarDarkIcon(activity.window, color)
            } else {
                setStatusBarDarkIcon(activity, whiteColor)
            }
        }
    }

    /**
     * 设置状态栏字体图标颜色(只限全屏非activity情况)
     *
     * @param window 当前窗口
     * @param color  颜色
     */
    fun setStatusBarDarkIcon(window: Window, color: Int): Boolean {
        return try {
            val flag = setStatusBarColor(window, color)
            if (Build.VERSION.SDK_INT > 22) {
                setStatusBarDarkIcon(window.decorView, true)
            }
            flag
        } catch (e: Exception) {
            AppLayerErrorCatcher.throwException(e)
            false
        }

    }

    /**
     * 设置状态栏字体图标颜色
     *
     * @param activity 当前activity
     * @param dark     是否深色 true为深色 false 为白色
     */
    fun setStatusBarDarkIcon(activity: Activity, dark: Boolean) {
        setStatusBarDarkIcon(activity, dark, true)
    }

    private fun changeMeiZuFlag(winParams: WindowManager.LayoutParams, flagName: String, on: Boolean): Boolean {
        try {
            val f = winParams.javaClass.getDeclaredField(flagName)
            f.isAccessible = true
            val bits = f.getInt(winParams)
            val f2 = winParams.javaClass.getDeclaredField("meizuFlags")
            f2.isAccessible = true
            var oldFlags = f2.getInt(winParams)
            val meiZuFlags = if (on) {
                oldFlags or bits
            } else {
                oldFlags and bits.inv()
            }
            if (oldFlags != meiZuFlags) {
                f2.setInt(winParams, meiZuFlags)
                return true
            }
        } catch (e: Throwable) {
            AppLayerErrorCatcher.throwException(e)
        }

        return false
    }

    /**
     * 设置状态栏颜色
     *
     * @param view
     * @param dark
     */
    private fun setStatusBarDarkIcon(view: View, dark: Boolean) {
        val oldVis = view.systemUiVisibility
        var newVis = oldVis
        if (dark) {
            newVis = newVis or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            newVis = newVis and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        if (newVis != oldVis) {
            view.systemUiVisibility = newVis
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param window
     * @param color
     */
    private fun setStatusBarColor(window: Window, color: Int): Boolean {
        val winParams = window.attributes
        if (mStatusBarColorFiled != null) {
            try {
                val oldColor = mStatusBarColorFiled!!.getInt(winParams)
                if (oldColor != color) {
                    mStatusBarColorFiled!!.set(winParams, color)
                    window.attributes = winParams
                }
                return true
            } catch (e: IllegalAccessException) {
                AppLayerErrorCatcher.throwException(e)
            }

        }
        return false
    }

    /**
     * 设置状态栏字体图标颜色(只限全屏非activity情况)
     *
     * @param window 当前窗口
     * @param dark   是否深色 true为深色 false 为白色
     */
    fun setStatusBarDarkIcon(window: Window, dark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            return changeMeiZuFlag(
                window.attributes,
                "MEIZU_FLAG_DARK_STATUS_BAR_ICON",
                dark
            )
        } else {
            val decorView = window.decorView
            setStatusBarDarkIcon(decorView, dark)
            return setStatusBarColor(window, 0)
        }
    }

    /**
     * mSetStatusBarDarkIcon是否为空，为空的话，直接修改flag
     *
     * @param activity
     * @param dark
     * @param flag
     */
    private fun setStatusBarDarkIcon(activity: Activity, dark: Boolean, flag: Boolean) {
        if (mSetStatusBarDarkIcon != null) {
            try {
                mSetStatusBarDarkIcon!!.invoke(activity, dark)
            } catch (e: IllegalAccessException) {
                AppLayerErrorCatcher.throwException(e)
            } catch (e: InvocationTargetException) {
                AppLayerErrorCatcher.throwException(e)
            }

        } else {
            if (flag) {
                setStatusBarDarkIcon(activity.window, dark)
            }
        }
    }
}
