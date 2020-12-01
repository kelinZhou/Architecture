package com.kelin.architecture.widget

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

/**
 * **描述:** 被修复的SwipeRefreshLayout。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-9  8:42
 *
 * **版本:** v 1.0.0
 */
class FixedRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {
    private var startY = 0f
    private var startX = 0f
    // 记录viewPager是否拖拽的标记
    private var isVpDragging = false
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop


    internal class SavedState : BaseSavedState {
        var isRefreshing = false

        constructor(superState: Parcelable?) : super(superState)
        /**
         * Constructor called from [.CREATOR]
         */
        constructor(`in`: Parcel) : super(`in`){
            isRefreshing = `in`.readByte().toInt() != 0
        }

        @RequiresApi(Build.VERSION_CODES.N)
        constructor(`in`: Parcel, loader: ClassLoader? = null) : super(`in`, loader ?: SavedState::class.java.classLoader) {
            isRefreshing = `in`.readByte().toInt() != 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (isRefreshing) 1 else 0).toByte())
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.isRefreshing = isRefreshing
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录手指按下的位置
                startY = ev.y
                startX = ev.x
                // 初始化标记
                isVpDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (isVpDragging) {
                    return false
                }
                // 获取当前手指位置
                val endY = ev.y
                val endX = ev.x
                val distanceX = abs(endX - startX)
                val distanceY = abs(endY - startY)
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isVpDragging = true
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->  // 初始化标记
                isVpDragging = false
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev)
    }
}