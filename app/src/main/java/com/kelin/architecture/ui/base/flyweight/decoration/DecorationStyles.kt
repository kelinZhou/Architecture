package com.kelin.architecture.ui.base.flyweight.decoration

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.kelin.architecture.util.dp2px

/**
 * **描述:** RecyclerView条目分割线的样式集。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-10  8:37
 *
 * **版本:** v 1.0.0
 */
object DecorationStyles {

    fun setDecorationStyle(recyclerView: RecyclerView, options: Options) {
        recyclerView.addItemDecoration(options.builder.create())
    }

    fun createCommonPaddingDecoration(context: Context): Options {
        return DecorationStyles.Options.newOption(context)
            .padding(20)
            .thickness(0.5F)
    }

    class Options private constructor(private val context: Context) {
        companion object {
            fun newOption(context: Context): Options {
                return Options(context)
            }
        }

        internal val builder = RecyclerViewItemDecoration.Builder(context)

        fun drawableID(@DrawableRes drawableID: Int): Options {
            builder.drawableID(drawableID)
            return this
        }

        fun color(@ColorInt color: Int): Options {
            builder.color(color)
            return this
        }

        fun color(color: String): Options {
            builder.color(color)
            return this
        }

        fun thickness(thickness: Int): Options {
            thickness(thickness.toFloat())
            return this
        }

        fun thickness(thickness: Float): Options {
            builder.thickness(thickness.dp2px)
            return this
        }

        fun dashWidth(dashWidth: Int): Options {
            dashWidth(dashWidth.toFloat())
            return this
        }

        fun dashWidth(dashWidth: Float): Options {
            builder.dashWidth(dashWidth.dp2px)
            return this
        }

        fun dashGap(dashGap: Int): Options {
            dashGap(dashGap.toFloat())
            return this
        }

        fun dashGap(dashGap: Float): Options {
            builder.dashGap(dashGap.dp2px)
            return this
        }

        fun lastLineVisible(visible: Boolean): Options {
            builder.lastLineVisible(visible)
            return this
        }

        fun firstLineVisible(visible: Boolean): Options {
            builder.firstLineVisible(visible)
            return this
        }

        fun padding(paddingStart: Int, paddingEnd: Int = paddingStart): Options {
            padding(paddingStart.toFloat(), paddingEnd.toFloat())
            return this
        }

        fun padding(paddingStart: Float, paddingEnd: Float = paddingStart): Options {
            builder.paddingStart(paddingStart.dp2px)
            builder.paddingEnd(paddingEnd.dp2px)
            return this
        }

        fun gridLeftVisible(visible: Boolean): Options {
            builder.gridLeftVisible(visible)
            return this
        }

        fun gridRightVisible(visible: Boolean): Options {
            builder.gridRightVisible(visible)
            return this
        }

        fun gridTopVisible(visible: Boolean): Options {
            builder.gridTopVisible(visible)
            return this
        }

        fun gridBottomVisible(visible: Boolean): Options {
            builder.gridBottomVisible(visible)
            return this
        }

        fun gridHorizontalSpacing(spacing: Int): Options {
            gridHorizontalSpacing(spacing.toFloat())
            return this
        }

        fun gridHorizontalSpacing(spacing: Float): Options {
            builder.gridHorizontalSpacing(spacing.dp2px)
            return this
        }

        fun gridVerticalSpacing(spacing: Int): Options {
            gridVerticalSpacing(spacing.toFloat())
            return this
        }

        fun gridVerticalSpacing(spacing: Float): Options {
            builder.gridVerticalSpacing(spacing.dp2px)
            return this
        }

        fun ignoreTypes(vararg ignoreTypes: Int): Options {
            builder.ignoreTypes(ignoreTypes)
            return this
        }


        fun repairColor(@ColorInt color: Int): Options {
            builder.repairColor(color)
            return this
        }

        fun repairColor(color: String): Options {
            builder.repairColor(color)
            return this
        }
    }
}