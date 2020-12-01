package com.kelin.architecture.annotation

import android.annotation.SuppressLint
import android.view.WindowManager
import androidx.annotation.IntDef

/**
 * **描述: ** 用来限定设置软键盘启动模式的方法的参数的注解。
 *
 * **创建人: ** kelin
 *
 * **创建时间: ** 2020/3/5  下午1:36
 *
 * **版本: ** v 1.0.0
 */
@SuppressLint("UniqueConstants")
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    flag = true,
    value = [WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED,
        WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED,
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN,
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN,
        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE,
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE,
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED,
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING,
        WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION]
)
annotation class SoftInputModeFlags
