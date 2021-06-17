package ru.bey_sviatoslav.android.epaminternship2021

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup


/**
 * Created by hiren.patel on 25-04-2016.
 */
class ZoomableViewGroup : ViewGroup {
    // these matrices will be used to move and zoom image
    private val matrix_ = Matrix()
    private val matrixInverse = Matrix()
    private val savedMatrix = Matrix()
    private var mode = NONE

    // remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var lastEvent: FloatArray? = null
    private var initZoomApplied = false
    private var mDispatchTouchEventWorkingArray = FloatArray(2)
    private var mOnTouchEventWorkingArray = FloatArray(2)
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        mDispatchTouchEventWorkingArray[0] = ev.x
        mDispatchTouchEventWorkingArray[1] = ev.y
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray)
        ev.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1])
        return super.dispatchTouchEvent(ev)
    }

    private fun scaledPointsToScreenPoints(a: FloatArray): FloatArray {
        matrix_.mapPoints(a)
        return a
    }

    private fun screenPointsToScaledPoints(a: FloatArray): FloatArray {
        matrixInverse.mapPoints(a)
        return a
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    /**
     * Determine the space between the first two fingers
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    private fun init(context: Context) {}
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                child.layout(l, t, l + child.measuredWidth, t + child.measuredHeight)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val values = FloatArray(9)
        matrix_.getValues(values)
        val container_width = values[Matrix.MSCALE_X] * widthSize
        val container_height = values[Matrix.MSCALE_Y] * heightSize

        //Log.d("zoomToFit", "m width: "+container_width+" m height: "+container_height);
        //Log.d("zoomToFit", "m x: "+pan_x+" m y: "+pan_y);
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                if (i == 0 && !initZoomApplied && child.width > 0) {
                    val c_w = child.width
                    val c_h = child.height

                    //zoomToFit(c_w, c_h, container_width, container_height);
                }
            }
        }
    }

    private fun zoomToFit(c_w: Int, c_h: Int, container_width: Float, container_height: Float) {
        val proportion_firstChild = c_w.toFloat() / c_h.toFloat()
        val proportion_container = container_width / container_height

        //Log.d("zoomToFit", "firstChildW: "+c_w+" firstChildH: "+c_h);
        //Log.d("zoomToFit", "proportion-container: "+proportion_container);
        //Log.d("zoomToFit", "proportion_firstChild: "+proportion_firstChild);
        if (proportion_container < proportion_firstChild) {
            val initZoom = container_height / c_h
            //Log.d("zoomToFit", "adjust height with initZoom: "+initZoom);
            matrix_.postScale(initZoom, initZoom)
            matrix_.postTranslate(-1 * (c_w * initZoom - container_width) / 2, 0f)
            matrix_.invert(matrixInverse)
        } else {
            val initZoom = container_width / c_w
            //Log.d("zoomToFit", "adjust width with initZoom: "+initZoom);
            matrix_.postScale(initZoom, initZoom)
            matrix_.postTranslate(0f, -1 * (c_h * initZoom - container_height) / 2)
            matrix_.invert(matrixInverse)
        }
        initZoomApplied = true
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.setMatrix(matrix_)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // handle touch events here
        mOnTouchEventWorkingArray[0] = event.x
        mOnTouchEventWorkingArray[1] = event.y
        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray)
        event.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1])
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix_)
                start[event.x] = event.y
                mode = DRAG
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrix_)
                    midPoint(mid, event)
                    mode = ZOOM
                }
                lastEvent = FloatArray(4)
                lastEvent!![0] = event.getX(0)
                lastEvent!![1] = event.getX(1)
                lastEvent!![2] = event.getY(0)
                lastEvent!![3] = event.getY(1)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                matrix_.set(savedMatrix)
                val dx = event.x - start.x
                val dy = event.y - start.y
                matrix_.postTranslate(dx, dy)
                matrix_.invert(matrixInverse)
            } else if (mode == ZOOM) {
                val newDist = spacing(event)
                if (newDist > 10f) {
                    matrix_.set(savedMatrix)
                    val scale = newDist / oldDist
                    matrix_.postScale(scale, scale, mid.x, mid.y)
                    matrix_.invert(matrixInverse)
                }
            }
        }
        invalidate()
        return true
    }

    companion object {
        // we can be in one of these 3 states
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
}