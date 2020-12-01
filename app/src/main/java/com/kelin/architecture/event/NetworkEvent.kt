package com.kelin.architecture.event

import com.kelin.architecture.annotation.NetworkType

/**
 * **描述:** 网络状态改变事件监听
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-06 10:27
 *
 * **版本:** v 1.0.0
 */
data class NetworkEvent(@NetworkType val networkType: Int, val isConnect: Boolean)