package ru.bey_sviatoslav.android.epaminternship2021.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import ru.bey_sviatoslav.android.epaminternship2021.layer.Layer
import ru.bey_sviatoslav.android.epaminternship2021.layer.LayoutManager
import kotlin.math.roundToInt


class CustomView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var image: Bitmap
    private var screenHeight: Int = resources.displayMetrics.heightPixels
    private var screenWidth: Int = resources.displayMetrics.widthPixels
    private var paint: Paint = Paint()
    private var gestures: GestureDetector
    private var scaleGesture: ScaleGestureDetector
    private var scale = 1.0f
    private var horizontalOffset = 0f
    private var verticalOffset = 0f
    private var START = 0
    private var WORKING = 1
    private var isScaling = false
    private var mode = START
    private var drawMatrix: Matrix = Matrix()
    private var lastFocusX = 0f
    private var lastFocusY = 0f
    private var layoutManager: LayoutManager
    private var firstLayer: Layer
    private var secondLayer: Layer
    private var thirdLayer: Layer

    var realScale = 1.0f
    var minScale = 1.0f
    var maxScale = 5.0f


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

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        canvas.drawColor(Color.WHITE)
        if (mode == START) {
            drawMatrix.postTranslate(horizontalOffset, verticalOffset)
            drawMatrix.postScale(scale, scale)
        }
        canvas.concat(drawMatrix)
        drawImageAccordingToScale(realScale, canvas)
        canvas.restore()
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val transformationMatrix = Matrix()
            val focusX = detector.focusX
            val focusY = detector.focusY

            transformationMatrix.postTranslate(-focusX, -focusY)

            realScale *= detector.scaleFactor

            if (isScaleInBounds(realScale)) {
                transformationMatrix.postScale(detector.scaleFactor, detector.scaleFactor)
                val focusShiftX = focusX - lastFocusX
                val focusShiftY = focusY - lastFocusY
                transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY)
                drawMatrix.postConcat(transformationMatrix)
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
            drawMatrix.postTranslate(-distanceX, -distanceY)
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
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        paint.color = Color.WHITE
        scaleGesture = ScaleGestureDetector(getContext(), ScaleListener())
        gestures = GestureDetector(getContext(), GestureListener())
        mode = START
        layoutManager = LayoutManager(this)
        image = layoutManager.backBitmap
        firstLayer = layoutManager.firstLayer
        secondLayer = layoutManager.secondLayer
        thirdLayer = layoutManager.thirdLayer
        initialize()
    }

    private fun drawImageAccordingToScale(scale: Float, canvas: Canvas) {
        when (scale) {
            in firstLayer.scaleFrom..firstLayer.scaleTo -> {
                changeLayerFromScaleToScaleWithOpacity(firstLayer, secondLayer, false, scale, canvas)
            }
            in secondLayer.scaleFrom..secondLayer.scaleTo -> {
                changeLayerFromScaleToScaleWithOpacity(secondLayer, thirdLayer, true, scale, canvas)
            }
            in thirdLayer.scaleFrom..thirdLayer.scaleTo -> {
                drawBitmaps(thirdLayer, canvas, paint)
            }
        }
    }

    private fun changeLayerFromScaleToScaleWithOpacity(layerFrom: Layer, layerTo: Layer, layerToOnTop: Boolean, scale: Float, canvas: Canvas) {
        val paintAlpha = Paint()

        if (layerToOnTop) {
            val opacityNormalized = getOpacityNormalized(scale, layerFrom)
            paintAlpha.alpha = opacityNormalized.times(255).roundToInt()

            drawBitmaps(layerFrom, canvas, paint)
            drawBitmaps(layerTo, canvas, paintAlpha)
        } else {
            val opacityNormalizedReversed = getOpacityNormalizedReversed(scale, layerFrom)
            paintAlpha.alpha = opacityNormalizedReversed.times(255).roundToInt()

            drawBitmaps(layerTo, canvas, paint)
            drawBitmaps(layerFrom, canvas, paintAlpha)
        }
    }

    private fun drawBitmaps(layerAction: Layer, canvas: Canvas, paint: Paint) {
        layerAction.listOfObjects.forEach {
            it.draw(canvas, paint)
        }
    }

    private fun getOpacityNormalized(currentScale: Float, layer: Layer): Float {
        return (currentScale - layer.scaleFrom) / (layer.scaleTo - layer.scaleFrom)
    }

    private fun getOpacityNormalizedReversed(currentScale: Float, layer: Layer): Float {
        return 1 - getOpacityNormalized(currentScale, layer)
    }

    private fun isScaleInBounds(currentScale: Float): Boolean {
        return currentScale in minScale..maxScale
    }
}