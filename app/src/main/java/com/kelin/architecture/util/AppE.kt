@file:JvmName("UnitsKt")

package com.kelin.architecture.util

import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.kelin.architecture.core.AppModule

/**
 * **描述:** 应用层的扩展函数。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/6/30 5:04 PM
 *
 * **版本:** v 1.0.0
 */

/**
 * 单位转换相关。
 */
val Int.dp2px: Int
    get() = this.toFloat().dp2px

val Float.dp2px: Int
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, AppModule.getContext().resources.displayMetrics).toInt()

val Int.px2dp: Int
    get() = this.toFloat().px2dp

val Float.px2dp: Int
    get() = (this * 160 / AppModule.getContext().resources.displayMetrics.densityDpi + 0.5f).toInt()

val Int.sp2px: Int
    get() = this.toFloat().sp2px

val Float.sp2px: Int
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, AppModule.getContext().resources.displayMetrics).toInt()

val Int.px2sp: Int
    get() = this.toFloat().px2sp

val Float.px2sp: Int
    get() = (this / AppModule.getContext().resources.displayMetrics.scaledDensity + 0.5f).toInt()

//View相关的
fun Boolean.toVisibleOrGone(): Int {
    return if (this) View.VISIBLE else View.GONE
}

fun Boolean.toVisibleOrInvisible(): Int {
    return if (this) View.VISIBLE else View.INVISIBLE
}

var View.visibleOrGone: Boolean
    set(value) {
        visibility = value.toVisibleOrGone()
    }
    get() = visibility == View.VISIBLE

var View.visibleOrInvisible: Boolean
    set(value) {
        visibility = value.toVisibleOrInvisible()
    }
    get() = visibility == View.VISIBLE

fun View?.setTransitionName(name: Any): View? {
    if (this != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        transitionName = name.toString()
    }
    return this
}

@Suppress("unchecked_cast")
inline fun <V> ViewGroup.eachChild(each: (child: V) -> Unit) {
    for (i: Int in 0 until childCount) {
        each(getChildAt(i) as V)
    }
}

@Suppress("unchecked_cast")
inline fun <V> ViewGroup.eachChildIndex(start: Int = 0, each: (index: Int, child: V) -> Unit) {
    for (i: Int in start until childCount) {
        each(i, getChildAt(i) as V)
    }
}