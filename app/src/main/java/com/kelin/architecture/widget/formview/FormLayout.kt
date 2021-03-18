package com.kelin.architecture.widget.formview

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.kelin.architecture.R

/**
 * **描述:** 表单控件
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-22 19:11
 *
 * **版本:** v 1.0.0
 */
class FormLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    val guidelinePercent: Float

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.FormLayout)
            guidelinePercent = ta.getFloat(R.styleable.FormLayout_layoutGuidelinePercent, 0.31F)
            ta.recycle()
        } else {
            guidelinePercent = 0.31F
        }
    }
}