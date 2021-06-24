package ru.bey_sviatoslav.android.epaminternship2021

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.roundToInt


class CustomView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var image: Bitmap

    var screenHeight: Int
    var screenWidth: Int

    var paint: Paint

    var gestures: GestureDetector
    var scaleGesture: ScaleGestureDetector

    var scale = 1.0f

    var realScale = 1.0f
    var minScale = 1.0f
    var maxScale = 1.9999f

    var horizontalOffset = 0f
    var verticalOffset = 0f

    var START = 0
    var WORKING = 1

    var isScaling = false

    var mode = START

    var drawMatrix: Matrix? = null

    var lastFocusX = 0f
    var lastFocusY = 0f

    private lateinit var backBitmap: Bitmap
    private lateinit var insidesBitmap: Bitmap
    private lateinit var boardTopBitmap: Bitmap

    //Best fit image display on canvas
    private fun initialize() {
        val imgPartRatio = image.width / image.height.toFloat()
        val screenRatio = screenWidth.toFloat() / screenHeight.toFloat()
        if (screenRatio > imgPartRatio) {
            scale = screenHeight.toFloat() / image.height.toFloat() // fit height
            horizontalOffset = (screenWidth.toFloat() - scale
                    * image.width.toFloat()) / 2.0f
            verticalOffset = 0f
        } else {
            scale = screenWidth.toFloat() / image.width.toFloat() // fit width
            horizontalOffset = 0f
            verticalOffset = (screenHeight.toFloat() - scale
                    * image.height.toFloat()) / 2.0f
        }
        invalidate()
    }

    private fun initializeBitmaps() {
        val backBitmapSrc = BitmapFactory.decodeResource(resources, R.drawable.back)
        backBitmap = Bitmap.createBitmap(backBitmapSrc,
                0, 0, backBitmapSrc.width, backBitmapSrc.height, matrix, true)

        val insidesBitmapSrc = BitmapFactory.decodeResource(resources, R.drawable.body_insides)
        insidesBitmap = Bitmap.createBitmap(insidesBitmapSrc,
                0, 0, insidesBitmapSrc.width, insidesBitmapSrc.height, matrix, true)

        val boardTopBitmapSrc = BitmapFactory.decodeResource(resources, R.drawable.board_top)
        boardTopBitmap = Bitmap.createBitmap(boardTopBitmapSrc,
                0, 0, boardTopBitmapSrc.width, boardTopBitmapSrc.height, matrix, true)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        canvas.drawColor(Color.WHITE)
        if (mode == START) {
            //This works perfectly as expected
            drawMatrix?.postTranslate(horizontalOffset, verticalOffset)
            drawMatrix?.postScale(scale, scale)
        }
        changeImage(realScale, canvas)
        canvas.restore()
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val transformationMatrix = Matrix()
            val focusX = detector.focusX
            val focusY = detector.focusY

            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY)

            realScale *= detector.scaleFactor

            if (isScaleInBounds(realScale)) {
                transformationMatrix.postScale(detector.scaleFactor, detector.scaleFactor)
                val focusShiftX = focusX - lastFocusX
                val focusShiftY = focusY - lastFocusY
                transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY)
                drawMatrix!!.postConcat(transformationMatrix)
                invalidate()
            } else {
                realScale = minScale.coerceAtLeast(realScale.coerceAtMost(maxScale))
            }

            lastFocusX = focusX
            lastFocusY = focusY

            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            mode = WORKING
            lastFocusX = detector.focusX
            lastFocusY = detector.focusY
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            isScaling = false
        }
    }

    inner class GestureListener : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        override fun onDown(e: MotionEvent): Boolean {
            isScaling = false
            return true
        }

        override fun onShowPress(p0: MotionEvent?) {}

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            mode = WORKING
            drawMatrix?.postTranslate(-distanceX, -distanceY)
            invalidate()
            return true
        }

        override fun onLongPress(p0: MotionEvent?) {}

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
            return false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGesture.onTouchEvent(event)
        gestures.onTouchEvent(event)
        return true
    }

    init {
        //initializing variables
        drawMatrix = Matrix()
        image = BitmapFactory.decodeResource(resources, R.drawable.back)
        //This is a full screen view
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
        paint = Paint()
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        paint.color = Color.WHITE
        scaleGesture = ScaleGestureDetector(getContext(), ScaleListener())
        gestures = GestureDetector(getContext(), GestureListener())
        mode = START
        initializeBitmaps()
        initialize()
    }

    private fun changeImage(scale: Float, canvas: Canvas) {
        when (scale) {
            in 1.0f..1.4999f -> {
                changeImageFromScaleToScaleWithOpacity(
                        backBitmap,
                        insidesBitmap,
                        false,
                        scale,
                        1.0f,
                        1.4999f,
                        canvas)
            }
            in 1.5f..1.9999f -> {
                changeImageFromScaleToScaleWithOpacity(
                        insidesBitmap,
                        boardTopBitmap,
                        true,
                        scale,
                        1.5f,
                        1.9999f,
                        canvas)
            }
            else -> {
                val bitmapSource: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.body_insides)
                val bitmap = Bitmap.createBitmap(bitmapSource,
                        0, 0, bitmapSource.width, bitmapSource.height, matrix, true)
                canvas.drawBitmap(bitmap, drawMatrix!!, paint)
            }
        }
    }

    private fun changeImageFromScaleToScaleWithOpacity(imageFrom: Bitmap,
                                                       imageTo: Bitmap,
                                                       imageToOnTopLayer: Boolean,
                                                       currentScale: Float,
                                                       scaleFrom: Float,
                                                       scaleTo: Float,
                                                       canvas: Canvas) {
        val paintAlpha = Paint()

        if (imageToOnTopLayer) {
            val opacityNormalized = (currentScale - scaleFrom) / (scaleTo - scaleFrom)
            paintAlpha.alpha = opacityNormalized.times(255).roundToInt()

            canvas.drawBitmap(imageFrom, drawMatrix!!, paint)
            canvas.drawBitmap(imageTo, drawMatrix!!, paintAlpha)
        } else {
            val opacityNormalized = 1 - (currentScale - scaleFrom) / (scaleTo - scaleFrom)
            paintAlpha.alpha = opacityNormalized.times(255).roundToInt()

            canvas.drawBitmap(imageTo, drawMatrix!!, paint)
            canvas.drawBitmap(imageFrom, drawMatrix!!, paintAlpha)
        }
    }

    private fun isScaleInBounds(currentScale: Float): Boolean {
        return currentScale in minScale..maxScale
    }
}