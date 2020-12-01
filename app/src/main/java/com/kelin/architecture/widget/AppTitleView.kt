package com.kelin.architecture.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat

/**
 * **描述:** 解决标题过长时可能会导致标题向右偏离的问题。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/10/10 5:41 PM
 *
 * **版本:** v 1.0.0
 */
class AppTitleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((resources.displayMetrics.widthPixels * 0.6f).toInt(), MeasureSpec.AT_MOST), heightMeasureSpec)
    }
}