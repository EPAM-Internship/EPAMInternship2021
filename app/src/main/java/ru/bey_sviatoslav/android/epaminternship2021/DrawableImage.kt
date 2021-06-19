package ru.bey_sviatoslav.android.epaminternship2021

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.roundToInt


class DrawableImage @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mBitmap: Bitmap? = null
    private var insidesBitmap: Bitmap? = null
    private val displayMetrics = resources.displayMetrics
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0

    private var mPositionX: Float = 0f
    private var mPositionY: Float = 0f
    private var mLastTouchX: Float = 0f
    private var mLastTouchY: Float = 0f

    private var mFocusX = 0f
    private var mFocusY = 0f
    private var mLastFocusX = -1f
    private var mLastFocusY = -1f

    private companion object val INVALID_POINTER_ID = -1
    private var mActivePointerId = INVALID_POINTER_ID

    private var mScaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    var mScaleFactor: Float = 1.0f
    private var opacity: Int = 100
    var mMinScale: Float = 1.0f
    var mMaxScale: Float = 10.0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private fun loadImageOnCanvas(resourceId: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        mImageWidth = displayMetrics.widthPixels
        mImageHeight = (mImageWidth * aspectRatio).roundToInt()
        if (resourceId == R.drawable.s8_background) {
            mBitmap = Bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false)
        } else {
            insidesBitmap = Bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false)
            insidesBitmap?.let {
                insidesBitmap = adjustOpacity(it, 100)
            }
        }
        invalidate()
    }

    private fun adjustOpacity(bitmap: Bitmap, opacity: Int): Bitmap? {
        val mutableBitmap = if (bitmap.isMutable) bitmap else bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val colour = opacity and 0xFF shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        return mutableBitmap
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            mLastFocusX = -1f
            mLastFocusY = -1f

            return true;
        }
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mFocusX = scaleGestureDetector.focusX
            mFocusY = scaleGestureDetector.focusY

            if(mLastFocusX == -1f) mLastFocusX = mFocusX
            if(mLastFocusY == -1f) mLastFocusY = mFocusY


            mPositionX += (mFocusX - mLastFocusX)
            mPositionY += (mFocusY - mLastFocusY)

            mScaleFactor *= mScaleGestureDetector.scaleFactor
            mScaleFactor = mMinScale.coerceAtLeast(mScaleFactor.coerceAtMost(mMaxScale))

            mLastFocusX = mFocusX
            mLastFocusY = mFocusY

            invalidate()

            return true
        }
    }

    @SuppressLint("DrawAllocation", "ResourceAsColor")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mScaleFactor < 1.2f) {
            loadImageOnCanvas(R.drawable.s8_background)
        } else {
            loadImageOnCanvas(R.drawable.body_insides)
        }
        mBitmap?.let {
            canvas?.save()
            canvas?.translate(mPositionX, mPositionY)
            canvas?.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY)
            val centerX = displayMetrics.widthPixels.toFloat() / 2 - mImageWidth.toFloat() / 2
            val centerY = displayMetrics.heightPixels.toFloat() / 2 - mImageHeight.toFloat() / 2
            canvas?.drawBitmap(it, 0f, 0f, null)
            canvas?.restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector.onTouchEvent(event)

        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x / mScaleFactor
                val y = event.y / mScaleFactor

                mLastTouchX = x
                mLastTouchY = y

                mActivePointerId = event.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(mActivePointerId)

                val x = event.getX(pointerIndex) / mScaleFactor
                val y = event.getY(pointerIndex) / mScaleFactor

                if (!mScaleGestureDetector.isInProgress) {
                    val distanceX = x - mLastTouchX
                    val distanceY = y - mLastTouchY

                    mPositionX += distanceX
                    mPositionY += distanceY

                    Log.d("log-my", "\nx: $x\ny: $y")
                    Log.d("log-my", "\nmPositionX: $mPositionX\nmPositionY: $mPositionY")
                    Log.d("log-my", "\nmFocusX: $mFocusX\nmFocusY: $mFocusY")
                    Log.d("log-my", "\nmImageWidth: $mImageWidth\nmImageHeight: $mImageHeight")
                    Log.d("log-my", "\nwidth: $width\nheight: $height")

                    Log.d("log-my", (mImageWidth * mScaleFactor - width).toString())

//                    if ((mPositionX * -1) < 0) {
//                        mPositionX = 0f
//                    } else if ((mPositionX * -1) > mImageWidth * mScaleFactor - width) {
//                        mPositionX = (mImageWidth * mScaleFactor - width) * -1
//                    }
//                    if ((mPositionY * -1) < 0) {
//                        mPositionY = 0f
//                    } else if ((mPositionY * -1) > mImageHeight * mScaleFactor - height) {
//                        mPositionY = (mImageHeight * mScaleFactor - height) * -1
//                    }

                    invalidate()
                }

                mLastTouchX = x
                mLastTouchY = y
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = (event.action.and(MotionEvent.ACTION_POINTER_INDEX_MASK)).shr(MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = event.getX(newPointerIndex) / mScaleFactor
                    mLastTouchY = event.getY(newPointerIndex) / mScaleFactor
                    mActivePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }
}