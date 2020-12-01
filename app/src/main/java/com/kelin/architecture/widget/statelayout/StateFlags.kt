package com.kelin.architecture.widget.statelayout

import androidx.annotation.IntDef
import com.kelin.architecture.widget.statelayout.StatePage


/**
 * **描述:** StateFlags限定注解。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2019-07-24  11:43
 *
 * **版本:** v 1.0.0
 */
@IntDef(StatePage.HAVE_LOADING_STATE, StatePage.HAVE_RETRY_STATE, StatePage.HAVE_EMPTY_STATE)
@Retention(AnnotationRetention.SOURCE)
annotation class StateFlags