package com.kelin.architecture.ui.base.flyweight.decoration

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.NinePatch
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import java.util.regex.Pattern

/**
 * **描述:** RecyclerView条目分割线的样式集。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020-03-10  8:37
 *
 * **版本:** v 1.0.0
 */
class RecyclerViewItemDecoration : RecyclerView.ItemDecoration() {

    /**
     * image resource id for R.java
     */
    private var mDrawableRid = 0
    /**
     * decoration color
     */
    private var mColor = Color.parseColor(DEFAULT_COLOR)
    private var mRepairColor = Color.WHITE
    /**
     * decoration thickness
     */
    private var mThickness: Int = 0
    /**
     * decoration dash with
     */
    private var mDashWidth = 0
    /**
     * decoration dash gap
     */
    private var mDashGap = 0
    private var mFirstLineVisible: Boolean = false
    private var mLastLineVisible: Boolean = false
    private var mPaddingStart = 0
    private var mPaddingEnd = 0
    /**
     * border line for grid mode
     */
    private var mGridLeftVisible: Boolean = false
    private var mGridRightVisible: Boolean = false
    private var mGridTopVisible: Boolean = false
    private var mGridBottomVisible: Boolean = false
    /**
     * spacing for grid mode
     */
    var mGridHorizontalSpacing: Int = 0
    var mGridVerticalSpacing: Int = 0

    /***
     * ignore the item types which won't be drew item decoration
     * only for mode horizontal and vertical
     */
    private var mIgnoreTypes: ArrayMap<Int, Int>? = null

    /**
     * direction mode for decoration
     */
    private var mMode: Int = 0

    private val mPaint by lazy {
        Paint().apply {
            color = mColor
            style = Paint.Style.STROKE
            strokeWidth = mThickness.toFloat()
        }
    }
    private val mRepairPaint by lazy {
        Paint().apply {
            color = mRepairColor
            style = Paint.Style.STROKE
            strokeWidth = mThickness.toFloat()
        }
    }

    private var mBmp: Bitmap? = null
    private var mNinePatch: NinePatch? = null
    /**
     * choose the real thickness for image or thickness
     */
    private var mCurrentThickness: Int = 0
    /**
     * sign for if the resource image is a ninepatch image
     */
    private var hasNinePatch = false
    /**
     * sign for if has get the parent RecyclerView LayoutManager mode
     */
    private var hasGetParentLayoutMode = false
    private var mContext: Context? = null

    private val isPureLine: Boolean = mDashGap == 0 && mDashWidth == 0

    fun setParams(context: Context, params: Param) {

        this.mContext = context

        this.mDrawableRid = params.drawableRid
        this.mColor = params.color
        this.mRepairColor = params.repairColor
        this.mThickness = params.thickness
        this.mDashGap = params.dashGap
        this.mDashWidth = params.dashWidth
        this.mPaddingStart = params.paddingStart
        this.mPaddingEnd = params.paddingEnd
        this.mFirstLineVisible = params.firstLineVisible
        this.mLastLineVisible = params.lastLineVisible
        this.mGridLeftVisible = params.gridLeftVisible
        this.mGridRightVisible = params.gridRightVisible
        this.mGridTopVisible = params.gridTopVisible
        this.mGridBottomVisible = params.gridBottomVisible
        this.mGridHorizontalSpacing = params.gridHorizontalSpacing
        this.mGridVerticalSpacing = params.gridVerticalSpacing
        if (params.ignoreTypes != null && params.ignoreTypes!!.size != 0) {
            this.mIgnoreTypes = ArrayMap()
            val ignoreTypeSize = params.ignoreTypes!!.size
            for (i in 0 until ignoreTypeSize) {
                this.mIgnoreTypes!![params.ignoreTypes!![i]] = params.ignoreTypes!![i]
            }
        }

    }

    private fun initPaint(context: Context) {

        this.mBmp = BitmapFactory.decodeResource(context.resources, mDrawableRid)
        if (mBmp != null) {

            if (mBmp!!.ninePatchChunk != null) {
                hasNinePatch = true
                mNinePatch = NinePatch(mBmp, mBmp!!.ninePatchChunk, null)
            }

            if (mMode == RVItemDecorationConst.MODE_HORIZONTAL) {
                mCurrentThickness = if (mThickness == 0) mBmp!!.height else mThickness
            }
            if (mMode == RVItemDecorationConst.MODE_VERTICAL) {
                mCurrentThickness = if (mThickness == 0) mBmp!!.width else mThickness
            }
        }
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        if (parent.adapter == null || parent.childCount == 0) {
            return
        }
        mPaint.color = mColor
        when (mMode) {
            RVItemDecorationConst.MODE_HORIZONTAL -> drawHorizontal(c, parent)
            RVItemDecorationConst.MODE_VERTICAL -> drawVertical(c, parent)
            RVItemDecorationConst.MODE_GRID -> drawGrid(c, parent)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if (!hasGetParentLayoutMode) {
            compatibleWithLayoutManager(parent)
            hasGetParentLayoutMode = true
        }
        val viewPosition = parent.layoutManager!!.getPosition(view)

        if (mMode == RVItemDecorationConst.MODE_HORIZONTAL) {

            if (!isIgnoreType(parent.adapter!!.getItemViewType(viewPosition))) {
                if (!(!mLastLineVisible && viewPosition == parent.adapter!!.itemCount - 1)) {

                    if (mDrawableRid != 0) {
                        outRect.set(0, 0, 0, mCurrentThickness)
                    } else {
                        outRect.set(0, 0, 0, mThickness)
                    }

                }

                if (mFirstLineVisible && viewPosition == 0) {
                    if (mDrawableRid != 0) {
                        outRect.set(0, mCurrentThickness, 0, mCurrentThickness)
                    } else {
                        outRect.set(0, mThickness, 0, mThickness)
                    }
                }
            } else {
                outRect.set(0, 0, 0, 0)
            }

        } else if (mMode == RVItemDecorationConst.MODE_VERTICAL) {
            if (!isIgnoreType(parent.adapter!!.getItemViewType(viewPosition))) {
                if (!(!mLastLineVisible && viewPosition == parent.adapter!!.itemCount - 1)) {
                    if (mDrawableRid != 0) {
                        outRect.set(0, 0, mCurrentThickness, 0)
                    } else {
                        outRect.set(0, 0, mThickness, 0)
                    }

                }
                if (mFirstLineVisible && viewPosition == 0) {
                    if (mDrawableRid != 0) {
                        outRect.set(mCurrentThickness, 0, mCurrentThickness, 0)
                    } else {
                        outRect.set(mThickness, 0, mThickness, 0)
                    }
                }

            } else {
                outRect.set(0, 0, 0, 0)
            }
        } else if (mMode == RVItemDecorationConst.MODE_GRID) {
            val columnSize = (parent.layoutManager as GridLayoutManager).spanCount
            val itemSize = parent.adapter!!.itemCount

            if (mDrawableRid != 0) {
                if (hasNinePatch) {
                    setGridOffsets(outRect, viewPosition, columnSize, itemSize, 0)
                } else {
                    setGridOffsets(outRect, viewPosition, columnSize, itemSize, 1)
                }
            } else {
                setGridOffsets(outRect, viewPosition, columnSize, itemSize, -1)
            }
        }

    }

    /**
     * draw horizontal decoration
     *
     * @param c
     * @param parent
     */
    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {

        val childrenCount = parent.childCount

        if (parent.clipToPadding) {
            val top = parent.paddingTop
            val bottom = parent.height - parent.paddingBottom
            c.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        }

        if (mDrawableRid != 0) {

            if (mFirstLineVisible) {
                val childView = parent.getChildAt(0)
                val myY = childView.top

                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {

                    if (hasNinePatch) {
                        val rect = Rect(mPaddingStart + parent.paddingLeft, myY - mCurrentThickness, parent.width - parent.paddingRight - mPaddingEnd, myY)
                        mNinePatch!!.draw(c, rect)
                    } else {
                        c.drawBitmap(mBmp!!, (mPaddingStart + parent.paddingLeft).toFloat(), (myY - mCurrentThickness).toFloat(), mPaint)
                    }
                }
            }


            for (i in 0 until childrenCount) {
                if (!mLastLineVisible && i == childrenCount - 1) {
                    break
                }
                val childView = parent.getChildAt(i)

                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {

                    val myY = childView.bottom

                    if (hasNinePatch) {
                        val rect = Rect(mPaddingStart + parent.paddingLeft, myY, parent.width - parent.paddingRight - mPaddingEnd, myY + mCurrentThickness)
                        mNinePatch!!.draw(c, rect)
                    } else {
                        c.drawBitmap(mBmp!!, (mPaddingStart + parent.paddingLeft).toFloat(), myY.toFloat(), mPaint)
                    }
                }

            }

        } else {

            val isPureLine = isPureLine
            if (!isPureLine) {
                val effects = DashPathEffect(floatArrayOf(0f, 0f, mDashWidth.toFloat(), mThickness.toFloat()), mDashGap.toFloat())
                mPaint.pathEffect = effects
            }

            if (mFirstLineVisible) {

                val childView = parent.getChildAt(0)

                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {

                    val myY = childView.top - mThickness / 2F

                    val path = Path()
                    path.moveTo((mPaddingStart + parent.paddingLeft).toFloat(), myY)
                    path.lineTo((parent.width - mPaddingEnd - parent.paddingRight).toFloat(), myY)
                    c.drawPath(path, mPaint)
                }
            }

            for (i in 0 until childrenCount) {
                if (!mLastLineVisible && i == childrenCount - 1) {
                    break
                }
                val childView = parent.getChildAt(i)

                val position = parent.layoutManager!!.getPosition(childView)
                if (position >= 0 && !isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            position
                        )
                    )
                ) {

                    val myY = childView.bottom + mThickness / 2F
                    val path = Path()
                    if (mPaddingStart > 0) {
                        path.moveTo(parent.paddingLeft.toFloat(), myY)
                        path.lineTo((mPaddingStart + parent.paddingLeft).toFloat(), myY)
                        c.drawPath(path, mRepairPaint)
                        path.reset()
                    }
                    path.moveTo((mPaddingStart + parent.paddingLeft).toFloat(), myY)
                    path.lineTo((parent.width - mPaddingEnd - parent.paddingRight).toFloat(), myY)
                    c.drawPath(path, mPaint)
                    if (mPaddingEnd > 0) {
                        path.reset()
                        path.moveTo((parent.width - mPaddingEnd - parent.paddingRight).toFloat(), myY)
                        path.lineTo((parent.width - parent.paddingRight).toFloat(), myY)
                        c.drawPath(path, mRepairPaint)
                    }
                }

            }

        }
    }

    /**
     * draw vertival decoration
     *
     * @param c
     * @param parent
     */
    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childrenCount = parent.childCount

        if (parent.clipToPadding) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            c.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        }

        if (mDrawableRid != 0) {

            if (mFirstLineVisible) {
                val childView = parent.getChildAt(0)

                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {
                    val myX = childView.left
                    if (hasNinePatch) {
                        val rect = Rect(myX - mCurrentThickness, mPaddingStart + parent.paddingLeft, myX, parent.height - mPaddingEnd - parent.paddingRight)
                        mNinePatch!!.draw(c, rect)
                    } else {
                        c.drawBitmap(mBmp!!, (myX - mCurrentThickness).toFloat(), (mPaddingStart + parent.paddingLeft).toFloat(), mPaint)
                    }
                }
            }
            for (i in 0 until childrenCount) {
                if (!mLastLineVisible && i == childrenCount - 1) {
                    break
                }
                val childView = parent.getChildAt(i)

                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {

                    val myX = childView.right
                    if (hasNinePatch) {
                        val rect = Rect(myX, mPaddingStart + parent.paddingLeft, myX + mCurrentThickness, parent.height - mPaddingEnd - parent.paddingRight)
                        mNinePatch!!.draw(c, rect)
                    } else {
                        c.drawBitmap(mBmp!!, myX.toFloat(), (mPaddingStart + parent.paddingLeft).toFloat(), mPaint)
                    }
                }
            }

        } else {

            val isPureLine = isPureLine
            if (!isPureLine) {
                val effects = DashPathEffect(floatArrayOf(0f, 0f, mDashWidth.toFloat(), mThickness.toFloat()), mDashGap.toFloat())
                mPaint.pathEffect = effects
            }

            if (mFirstLineVisible) {
                val childView = parent.getChildAt(0)
                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {
                    val myX = childView.left - mThickness / 2F
                    val path = Path()
                    path.moveTo(myX, (mPaddingStart + parent.paddingLeft).toFloat())
                    path.lineTo(myX, (parent.height - mPaddingEnd - parent.paddingRight).toFloat())
                    c.drawPath(path, mPaint)
                }
            }

            for (i in 0 until childrenCount) {
                if (!mLastLineVisible && i == childrenCount - 1) {
                    break
                }
                val childView = parent.getChildAt(i)

                if (!isIgnoreType(
                        parent.adapter!!.getItemViewType(
                            parent.layoutManager!!.getPosition(childView)
                        )
                    )
                ) {

                    val myX = childView.right + mThickness / 2F
                    val path = Path()
                    path.moveTo(myX, (mPaddingStart + parent.paddingLeft).toFloat())
                    path.lineTo(myX, (parent.height - mPaddingEnd - parent.paddingRight).toFloat())
                    c.drawPath(path, mPaint)
                }

            }
        }
    }

    /**
     * draw grid decoration
     *
     * @param c
     * @param parent
     */
    private fun drawGrid(c: Canvas, parent: RecyclerView) {

        val childrenCount = parent.childCount
        val columnSize = (parent.layoutManager as GridLayoutManager).spanCount
        val itemSize = parent.adapter!!.itemCount

        if (mDrawableRid != 0) {

            mPaint.strokeWidth = mThickness.toFloat()

            for (i in 0 until childrenCount) {
                val childView = parent.getChildAt(i)
                val myT = childView.top
                val myB = childView.bottom
                val myL = childView.left
                val myR = childView.right
                val viewPosition = parent.layoutManager!!.getPosition(childView)

                //when columnSize/spanCount is One
                if (columnSize == 1) {
                    if (isFirstGridRow(viewPosition, columnSize)) {

                        if (mGridLeftVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL - mThickness, myT, myL, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), myT.toFloat(), mPaint)
                            }
                        }
                        if (mGridTopVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL - mThickness, myT - mThickness, myR + mThickness, myT)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }
                        if (mGridRightVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myR, myT, myR + mThickness, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myR.toFloat(), myT.toFloat(), mPaint)
                            }
                        }

                        //not first row
                    } else {

                        if (mGridLeftVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL - mThickness, myT - mThickness, myL, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }

                        if (mGridRightVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myR, myT - mThickness, myR + mThickness, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myR.toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }

                    }

                    if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                        if (mGridBottomVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL - mThickness, myB, myR + mThickness, myB + mThickness)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), myB.toFloat(), mPaint)
                            }
                        }
                    } else {
                        if (hasNinePatch) {
                            val rect = Rect(myL, myB, myR + mThickness, myB + mThickness)
                            mNinePatch!!.draw(c, rect)
                        } else {
                            c.drawBitmap(mBmp!!, myL.toFloat(), myB.toFloat(), mPaint)
                        }
                    }

                    //when columnSize/spanCount is Not One
                } else {
                    if (isFirstGridColumn(viewPosition, columnSize) && isFirstGridRow(viewPosition, columnSize)) {

                        if (mGridLeftVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL - mThickness, myT - mThickness, myL, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }

                        if (mGridTopVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL, myT - mThickness, myR, myT)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myL.toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }

                        if (itemSize == 1) {
                            if (mGridRightVisible) {
                                if (hasNinePatch) {
                                    val rect = Rect(myR, myT - mThickness, myR + mThickness, myB)
                                    mNinePatch!!.draw(c, rect)
                                } else {
                                    c.drawBitmap(mBmp!!, myR.toFloat(), (myT - mThickness).toFloat(), mPaint)
                                }
                            }
                        } else {
                            if (hasNinePatch) {
                                val rect = Rect(myR, myT - mThickness, myR + mThickness, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myR.toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }

                    } else if (isFirstGridRow(viewPosition, columnSize)) {

                        if (mGridTopVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL, myT - mThickness, myR, myT)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myL.toFloat(), (myT - mThickness).toFloat(), mPaint)
                            }
                        }

                        if (isLastGridColumn(viewPosition, itemSize, columnSize)) {
                            if (mGridRightVisible) {
                                if (hasNinePatch) {
                                    val rect = Rect(myR, myT - mThickness, myR + mThickness, myB)
                                    mNinePatch!!.draw(c, rect)
                                } else {
                                    c.drawBitmap(mBmp!!, myR.toFloat(), (myT - mThickness).toFloat(), mPaint)
                                }
                            }
                        } else {
                            if (hasNinePatch) {
                                val rect = Rect(myR, myT - mThickness, myR + mThickness, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myR.toFloat(), (myT - mBmp!!.height).toFloat(), mPaint)
                            }
                        }

                    } else if (isFirstGridColumn(viewPosition, columnSize)) {
                        if (mGridLeftVisible) {
                            if (hasNinePatch) {
                                val rect = Rect(myL - mThickness, myT, myL, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), myT.toFloat(), mPaint)
                            }
                        }

                        if (hasNinePatch) {
                            val rect = Rect(myR, myT, myR + mThickness, myB)
                            mNinePatch!!.draw(c, rect)
                        } else {
                            c.drawBitmap(mBmp!!, myR.toFloat(), myT.toFloat(), mPaint)
                        }
                    } else {
                        if (isLastGridColumn(viewPosition, itemSize, columnSize)) {
                            if (mGridRightVisible) {
                                if (hasNinePatch) {
                                    val rect = Rect(myR, myT - mThickness, myR + mThickness, myB)
                                    mNinePatch!!.draw(c, rect)
                                } else {
                                    c.drawBitmap(mBmp!!, myR.toFloat(), (myT - mBmp!!.height).toFloat(), mPaint)
                                }
                            }
                        } else {
                            if (hasNinePatch) {
                                val rect = Rect(myR, myT, myR + mThickness, myB)
                                mNinePatch!!.draw(c, rect)
                            } else {
                                c.drawBitmap(mBmp!!, myR.toFloat(), myT.toFloat(), mPaint)
                            }
                        }
                    }

                    //bottom line
                    if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                        if (mGridBottomVisible) {
                            if (itemSize == 1) {
                                if (hasNinePatch) {
                                    val rect = Rect(myL - mThickness, myB, myR + if (mGridRightVisible) mThickness else 0, myB + mThickness)
                                    mNinePatch!!.draw(c, rect)
                                } else {
                                    c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), myB.toFloat(), mPaint)
                                }
                            } else if (isLastGridColumn(viewPosition, itemSize, columnSize)) {
                                if (hasNinePatch) {
                                    val rect = Rect(myL - mThickness, myB, myR + mThickness, myB + mThickness)
                                    mNinePatch!!.draw(c, rect)
                                } else {
                                    c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), (myB + mThickness / 2F), mPaint)
                                }
                            } else {
                                if (hasNinePatch) {
                                    val rect = Rect(myL - mThickness, myB, myR + if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing, myB + mThickness)
                                    mNinePatch!!.draw(c, rect)
                                } else {
                                    c.drawBitmap(mBmp!!, (myL - mThickness).toFloat(), myB.toFloat(), mPaint)
                                }
                            }

                        }
                    } else {
                        if (hasNinePatch) {
                            val rect = Rect(myL - mThickness, myB, myR, myB + if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing)
                            mNinePatch!!.draw(c, rect)
                        } else {
                            c.drawBitmap(mBmp!!, (myL - mBmp!!.width).toFloat(), myB.toFloat(), mPaint)
                        }
                    }
                }

            }

        } else {
            if (!isPureLine) {
                val effects = DashPathEffect(floatArrayOf(0f, 0f, mDashWidth.toFloat(), mThickness.toFloat()), mDashGap.toFloat())
                mPaint.pathEffect = effects
            }
            for (i in 0 until childrenCount) {
                val childView = parent.getChildAt(i)
                val myT = childView.top
                val myB = childView.bottom
                val myL = childView.left
                val myR = childView.right
                val viewPosition = parent.layoutManager!!.getPosition(childView)

                //when columnSize/spanCount is One
                if (columnSize == 1) {
                    if (isFirstGridRow(viewPosition, columnSize)) {

                        if (mGridLeftVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myL - mThickness / 2F), myT.toFloat())
                            path.lineTo((myL - mThickness / 2F), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }
                        if (mGridTopVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myL - mThickness).toFloat(), myT - mThickness / 2F)
                            path.lineTo((myR + mThickness).toFloat(), myT - mThickness / 2F)
                            c.drawPath(path, mPaint)
                        }
                        if (mGridRightVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myR + mThickness / 2F), myT.toFloat())
                            path.lineTo((myR + mThickness / 2F), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                        //not first row
                    } else {

                        if (mGridLeftVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myL - mThickness / 2F), (myT - if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing).toFloat())
                            path.lineTo((myL - mThickness / 2F), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                        if (mGridRightVisible) {
                            val path = Path()
                            mPaint.strokeWidth = mThickness.toFloat()
                            path.moveTo((myR + mThickness / 2F), myT.toFloat())
                            path.lineTo((myR + mThickness / 2F), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                    }

                    if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                        if (mGridBottomVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myL - mThickness).toFloat(), myB + mThickness / 2F)
                            path.lineTo((myR + mThickness).toFloat(), myB + mThickness / 2F)
                            c.drawPath(path, mPaint)
                        }
                    } else {
                        mPaint.strokeWidth = mThickness.toFloat()
                        if (mGridVerticalSpacing != 0) {
                            mPaint.strokeWidth = mGridVerticalSpacing.toFloat()
                        }
                        val path = Path()
                        path.moveTo(myL.toFloat(), (myB + (if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing) / 2).toFloat())
                        path.lineTo((myR + mThickness).toFloat(), (myB + (if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing) / 2).toFloat())
                        c.drawPath(path, mPaint)
                    }

                    //when columnSize/spanCount is Not One
                } else {
                    if (isFirstGridColumn(viewPosition, columnSize) && isFirstGridRow(viewPosition, columnSize)) {

                        if (mGridLeftVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myL - mThickness / 2F), (myT - mThickness).toFloat())
                            path.lineTo((myL - mThickness / 2F), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                        if (mGridTopVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo(myL.toFloat(), (myT - mThickness / 2F))
                            path.lineTo(myR.toFloat(), (myT - mThickness / 2F))
                            c.drawPath(path, mPaint)

                        }

                        if (itemSize == 1) {
                            if (mGridRightVisible) {
                                mPaint.strokeWidth = mThickness.toFloat()
                                val path = Path()
                                path.moveTo((myR + mThickness / 2F), (myT - mThickness).toFloat())
                                path.lineTo((myR + mThickness / 2F), myB.toFloat())
                                c.drawPath(path, mPaint)
                            }
                        } else {
                            mPaint.strokeWidth = mThickness.toFloat()
                            if (mGridHorizontalSpacing != 0) {
                                mPaint.strokeWidth = mGridHorizontalSpacing.toFloat()
                            }
                            val path = Path()
                            path.moveTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), (myT - mThickness).toFloat())
                            path.lineTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                    } else if (isFirstGridRow(viewPosition, columnSize)) {

                        if (mGridTopVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo(myL.toFloat(), myT - mThickness / 2F)
                            path.lineTo(myR.toFloat(), myT - mThickness / 2F)
                            c.drawPath(path, mPaint)

                        }

                        if (isLastGridColumn(viewPosition, itemSize, columnSize)) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            if (mGridRightVisible) {

                                var alterY = 0
                                if (isLastSecondGridRowNotDivided(viewPosition, itemSize, columnSize)) {
                                    alterY = if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing
                                }
                                val path = Path()
                                path.moveTo((myR + mThickness / 2F), (myT - mThickness).toFloat())
                                path.lineTo((myR + mThickness / 2F), (myB + alterY).toFloat())
                                c.drawPath(path, mPaint)
                            }
                        } else {
                            if (mGridHorizontalSpacing != 0) {
                                mPaint.strokeWidth = mGridHorizontalSpacing.toFloat()
                            }
                            val path = Path()
                            path.moveTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), (myT - mThickness).toFloat())
                            path.lineTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                    } else if (isFirstGridColumn(viewPosition, columnSize)) {

                        if (mGridLeftVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            val path = Path()
                            path.moveTo((myL - mThickness / 2F), myT.toFloat())
                            path.lineTo((myL - mThickness / 2F), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }

                        mPaint.strokeWidth = mThickness.toFloat()
                        if (mGridHorizontalSpacing != 0) {
                            mPaint.strokeWidth = mGridHorizontalSpacing.toFloat()
                        }
                        val path = Path()
                        path.moveTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), myT.toFloat())
                        path.lineTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), myB.toFloat())
                        c.drawPath(path, mPaint)

                    } else {

                        mPaint.strokeWidth = mThickness.toFloat()

                        if (isLastGridColumn(viewPosition, itemSize, columnSize)) {

                            if (mGridRightVisible) {

                                var alterY = 0
                                if (isLastSecondGridRowNotDivided(viewPosition, itemSize, columnSize)) {
                                    alterY = if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing
                                }
                                val path = Path()
                                path.moveTo((myR + mThickness / 2F), (myT - if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing).toFloat())
                                path.lineTo((myR + mThickness / 2F), (myB + alterY).toFloat())
                                c.drawPath(path, mPaint)
                            }
                        } else {
                            if (mGridHorizontalSpacing != 0) {
                                mPaint.strokeWidth = mGridHorizontalSpacing.toFloat()
                            }
                            val path = Path()
                            path.moveTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), myT.toFloat())
                            path.lineTo((myR + (if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing) / 2).toFloat(), myB.toFloat())
                            c.drawPath(path, mPaint)
                        }
                    }

                    //bottom line
                    if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                        if (mGridBottomVisible) {
                            mPaint.strokeWidth = mThickness.toFloat()
                            if (itemSize == 1) {
                                val path = Path()
                                path.moveTo((myL - mThickness).toFloat(), myB + mThickness / 2F)
                                path.lineTo((myR + if (mGridRightVisible) mThickness else 0).toFloat(), myB + mThickness / 2F)
                                c.drawPath(path, mPaint)
                            } else if (isLastGridColumn(viewPosition, itemSize, columnSize)) {
                                val path = Path()
                                path.moveTo((myL - if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing).toFloat(), myB + mThickness / 2F)
                                path.lineTo((myR + mThickness).toFloat(), myB + mThickness / 2F)
                                c.drawPath(path, mPaint)
                            } else {
                                val path = Path()
                                path.moveTo((myL - if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing).toFloat(), myB + mThickness / 2F)
                                path.lineTo((myR + if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing).toFloat(), myB + mThickness / 2F)
                                c.drawPath(path, mPaint)
                            }

                        }
                    } else {
                        mPaint.strokeWidth = mThickness.toFloat()
                        if (mGridVerticalSpacing != 0) {
                            mPaint.strokeWidth = mGridVerticalSpacing.toFloat()
                        }
                        val path = Path()
                        path.moveTo((myL - if (mGridHorizontalSpacing == 0) mThickness else mGridHorizontalSpacing).toFloat(), (myB + (if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing) / 2).toFloat())
                        path.lineTo(myR.toFloat(), (myB + (if (mGridVerticalSpacing == 0) mThickness else mGridVerticalSpacing) / 2).toFloat())
                        c.drawPath(path, mPaint)
                    }
                }

            }
        }
    }

    /**
     * set offsets for grid mode
     *
     * @param outRect
     * @param viewPosition
     * @param columnSize
     * @param itemSize
     * @param tag          0 for ninepatch,1 for drawable bitmap
     */
    fun setGridOffsets(outRect: Rect, viewPosition: Int, columnSize: Int, itemSize: Int, tag: Int) {

        val x: Int
        val y: Int
        val borderThickness = mThickness
        if (tag == 0) {
            y = mThickness
            x = y
            mGridHorizontalSpacing = 0
            mGridVerticalSpacing = mGridHorizontalSpacing
        } else if (tag == 1) {
            x = mBmp!!.width
            y = mBmp!!.height
            mGridHorizontalSpacing = 0
            mGridVerticalSpacing = mGridHorizontalSpacing
        } else {

            if (mGridHorizontalSpacing != 0) {
                x = mGridHorizontalSpacing
            } else {
                x = mThickness
            }

            if (mGridVerticalSpacing != 0) {
                y = mGridVerticalSpacing
            } else {
                y = mThickness
            }

        }

        //when columnSize/spanCount is One
        if (columnSize == 1) {
            if (isFirstGridRow(viewPosition, columnSize)) {
                if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                    outRect.set(if (mGridLeftVisible) borderThickness else 0, if (mGridTopVisible) borderThickness else 0, if (mGridRightVisible) borderThickness else 0, if (mGridBottomVisible) borderThickness else y)
                } else {
                    outRect.set(if (mGridLeftVisible) borderThickness else 0, if (mGridTopVisible) borderThickness else 0, if (mGridRightVisible) borderThickness else 0, y)
                }

            } else {
                if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                    outRect.set(if (mGridLeftVisible) borderThickness else 0, 0, if (mGridRightVisible) borderThickness else 0, if (mGridBottomVisible) borderThickness else 0)
                } else {
                    outRect.set(if (mGridLeftVisible) borderThickness else 0, 0, if (mGridRightVisible) borderThickness else 0, y)
                }
            }
        } else {

            // A
            if (isFirstGridColumn(viewPosition, columnSize) && isFirstGridRow(viewPosition, columnSize)) {

                outRect.set(if (mGridLeftVisible) borderThickness else 0, if (mGridTopVisible) borderThickness else 0, if (itemSize == 1) borderThickness else x / 2, if (isLastGridRow(viewPosition, itemSize, columnSize)) borderThickness else y / 2)
                // B
            } else if (isFirstGridRow(viewPosition, columnSize) && isLastGridColumn(viewPosition, itemSize, columnSize)) {

                outRect.set(x / 2, if (mGridTopVisible) borderThickness else 0, if (mGridRightVisible) borderThickness else 0, if (isLastGridRow(viewPosition, itemSize, columnSize)) borderThickness else y / 2)

                // C
            } else if (isLastGridColumn(viewPosition, itemSize, columnSize) && isLastGridRow(viewPosition, itemSize, columnSize)) {
                outRect.set(x / 2, y / 2, if (mGridRightVisible) borderThickness else 0, if (mGridBottomVisible) borderThickness else 0)
                // D
            } else if (isFirstGridColumn(viewPosition, columnSize) && isLastGridRow(viewPosition, itemSize, columnSize)) {
                outRect.set(if (mGridLeftVisible) borderThickness else 0, y / 2, if (isLastGridColumn(viewPosition, itemSize, columnSize)) borderThickness else x / 2, if (mGridBottomVisible) borderThickness else 0)
                // E
            } else if (isFirstGridColumn(viewPosition, columnSize)) {
                outRect.set(if (mGridLeftVisible) borderThickness else 0, y / 2, if (isLastGridColumn(viewPosition, itemSize, columnSize)) borderThickness else x / 2, y / 2)

                // F
            } else if (isFirstGridRow(viewPosition, columnSize)) {
                outRect.set(x / 2, if (mGridTopVisible) borderThickness else 0, x / 2, if (isLastGridRow(viewPosition, itemSize, columnSize)) borderThickness else y / 2)
                // G
            } else if (isLastGridColumn(viewPosition, itemSize, columnSize)) {
                outRect.set(x / 2, y / 2, if (mGridRightVisible) borderThickness else 0, y / 2)
                // H
            } else if (isLastGridRow(viewPosition, itemSize, columnSize)) {
                outRect.set(x / 2, y / 2, x / 2, if (mGridBottomVisible) borderThickness else 0)
                // I
            } else {
                outRect.set(x / 2, y / 2, x / 2, y / 2)
            }
        }
    }

    /**
     * check if is one of the first columns
     *
     * @param position
     * @param columnSize
     * @return
     */
    private fun isFirstGridColumn(position: Int, columnSize: Int): Boolean {

        return position % columnSize == 0
    }

    /**
     * check if is one of the last columns
     *
     * @param position
     * @param columnSize
     * @return
     */
    private fun isLastGridColumn(position: Int, itemSize: Int, columnSize: Int): Boolean {
        var isLast = false
        if ((position + 1) % columnSize == 0) {
            isLast = true
        }
        return isLast
    }

    /**
     * check if is the first row of th grid
     *
     * @param position
     * @param columnSize
     * @return
     */
    private fun isFirstGridRow(position: Int, columnSize: Int): Boolean {
        return position < columnSize
    }

    /**
     * check if is the last row of the grid
     *
     * @param position
     * @param itemSize
     * @param columnSize
     * @return
     */
    private fun isLastGridRow(position: Int, itemSize: Int, columnSize: Int): Boolean {
        val temp = itemSize % columnSize
        if (temp == 0 && position >= itemSize - columnSize) {
            return true
        } else if (position >= itemSize / columnSize * columnSize) {
            return true
        }
        return false
    }

    /**
     * check if is the last second row of the grid when the itemSize cannot be divided by columnSize
     *
     * @param position
     * @param itemSize
     * @param columnSize
     * @return
     */
    private fun isLastSecondGridRowNotDivided(position: Int, itemSize: Int, columnSize: Int): Boolean {
        val temp = itemSize % columnSize
        return temp != 0 && itemSize - 1 - temp == position
    }

    /**
     * compatible with recyclerview layoutmanager
     *
     * @param parent
     */
    private fun compatibleWithLayoutManager(parent: RecyclerView) {

        if (parent.layoutManager != null) {
            if (parent.layoutManager is GridLayoutManager) {
                mMode = RVItemDecorationConst.MODE_GRID
            } else if (parent.layoutManager is LinearLayoutManager) {
                mMode = if ((parent.layoutManager as LinearLayoutManager).orientation == RecyclerView.HORIZONTAL) {
                    RVItemDecorationConst.MODE_VERTICAL
                } else {
                    RVItemDecorationConst.MODE_HORIZONTAL
                }
            }

        } else {
            mMode = RVItemDecorationConst.MODE_UNKNOWN
        }

        initPaint(mContext!!)

    }

    /**
     * check current item is ignore type
     *
     * @return
     */
    private fun isIgnoreType(viewType: Int): Boolean {
        return if (mIgnoreTypes == null || mIgnoreTypes!!.isEmpty) {
            false
        } else {
            mIgnoreTypes!!.containsKey(viewType)
        }
    }

    class Builder(private val context: Context) {

        private val params: Param = Param()

        fun create(): RecyclerViewItemDecoration {
            val recyclerViewItemDecoration = RecyclerViewItemDecoration()
            recyclerViewItemDecoration.setParams(context, params)
            return recyclerViewItemDecoration
        }

        fun drawableID(@DrawableRes drawableID: Int): Builder {
            params.drawableRid = drawableID
            return this
        }

        fun color(@ColorInt color: Int): Builder {
            params.color = color
            return this
        }

        fun color(color: String): Builder {
            if (isColorString(color)) {
                params.color = Color.parseColor(color)
            }
            return this
        }

        fun repairColor(@ColorInt color: Int): Builder {
            params.repairColor = color
            return this
        }

        fun repairColor(color: String): Builder {
            if (isColorString(color)) {
                params.repairColor = Color.parseColor(color)
            }
            return this
        }

        fun thickness(thickness: Int): Builder {
            params.thickness = if (thickness <= 0) {
                1
            } else {
                thickness
            }
            return this
        }

        fun dashWidth(dashWidth: Int): Builder {
            var dashWidth = dashWidth
            if (dashWidth < 0) {
                dashWidth = 0
            }
            params.dashWidth = dashWidth
            return this
        }

        fun dashGap(dashGap: Int): Builder {
            params.dashGap = if (dashGap < 0) 0 else dashGap
            return this
        }

        fun lastLineVisible(visible: Boolean): Builder {
            params.lastLineVisible = visible
            return this
        }

        fun firstLineVisible(visible: Boolean): Builder {
            params.firstLineVisible = visible
            return this
        }

        fun paddingStart(padding: Int): Builder {
            params.paddingStart = if (padding < 0) 0 else padding
            return this
        }

        fun paddingEnd(padding: Int): Builder {
            params.paddingEnd = if (padding < 0) 0 else padding
            return this
        }

        fun gridLeftVisible(visible: Boolean): Builder {
            params.gridLeftVisible = visible
            return this
        }

        fun gridRightVisible(visible: Boolean): Builder {
            params.gridRightVisible = visible
            return this
        }

        fun gridTopVisible(visible: Boolean): Builder {
            params.gridTopVisible = visible
            return this
        }

        fun gridBottomVisible(visible: Boolean): Builder {
            params.gridBottomVisible = visible
            return this
        }

        fun gridHorizontalSpacing(spacing: Int): Builder {
            params.gridHorizontalSpacing = if (spacing < 0) 0 else if (spacing % 2 != 0) spacing + 1 else spacing
            return this
        }

        fun gridVerticalSpacing(spacing: Int): Builder {
            params.gridVerticalSpacing = if (spacing < 0) 0 else if (spacing % 2 != 0) spacing + 1 else spacing
            return this
        }

        fun ignoreTypes(ignoreTypes: IntArray): Builder {
            params.ignoreTypes = ignoreTypes
            return this
        }
    }

    public class Param {

        var drawableRid = 0
        var color = Color.parseColor(DEFAULT_COLOR)
        var repairColor = Color.WHITE
        var thickness: Int = 0
        var dashWidth = 0
        var dashGap = 0
        var lastLineVisible: Boolean = false
        var firstLineVisible: Boolean = false
        var paddingStart: Int = 0
        var paddingEnd: Int = 0
        var gridLeftVisible: Boolean = false
        var gridRightVisible: Boolean = false
        var gridTopVisible: Boolean = false
        var gridBottomVisible: Boolean = false
        var gridHorizontalSpacing = 0
        var gridVerticalSpacing = 0
        var ignoreTypes: IntArray? = null
    }

    companion object {

        /**
         * default decoration color
         */
        private val DEFAULT_COLOR = "#ECECEC"

        /**
         * judge is a color string like #xxxxxx or #xxxxxxxx
         *
         * @param colorStr
         * @return
         */
        fun isColorString(colorStr: String): Boolean {
            return Pattern.matches("^#([0-9a-fA-F]{6}||[0-9a-fA-F]{8})$", colorStr)
        }
    }

}
