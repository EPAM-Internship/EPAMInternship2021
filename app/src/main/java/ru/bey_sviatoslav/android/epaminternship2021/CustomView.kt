package ru.bey_sviatoslav.android.epaminternship2021

import android.content.Context
import android.graphics.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View


class CustomView(context: Context?, ic: Int) : View(context) {
    var currentLayer: Bitmap
    var nextLayer: Bitmap
    var layers = arrayOf<Array<BitmapInfo>>()
    var screenHeight: Int
    var screenWidth: Int
    var currentPaint: Paint
    var nextPaint: Paint
    var gestures: GestureDetector
    var scaleGesture: ScaleGestureDetector
    var scale = 1.0f
    var realScale = 1.0f
    var horizontalOffset = 0f
    var verticalOffset = 0f
    var START = 0
    var WORKING = 1
    var isScaling = false
    var touchX = 0f
    var touchY = 0f
    var mode = START
    var drawMatrix: Matrix? = null
    var lastFocusX = 0f
    var lastFocusY = 0f

    //Best fit image display on canvas
    private fun initialize() {
        val imgPartRatio = currentLayer.width / currentLayer.height.toFloat()
        val screenRatio = screenWidth.toFloat() / screenHeight.toFloat()
        if (screenRatio > imgPartRatio) {
            scale = screenHeight.toFloat() / currentLayer.height.toFloat() // fit height
            horizontalOffset = (screenWidth.toFloat() - scale
                    * currentLayer.width.toFloat()) / 2.0f
            verticalOffset = 0f
        } else {
            scale = screenWidth.toFloat() / currentLayer.width.toFloat() // fit width
            horizontalOffset = 0f
            verticalOffset = (screenHeight.toFloat() - scale
                    * currentLayer.height.toFloat()) / 2.0f
        }
        invalidate()
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
        canvas.drawBitmap(currentLayer, drawMatrix!!, currentPaint)
        canvas.drawBitmap(nextLayer, drawMatrix!!, nextPaint)
        canvas.restore()
    }

    inner class ScaleListener : OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val transformationMatrix = Matrix()
            val focusX = detector.focusX
            val focusY = detector.focusY

            //Zoom focus is where the fingers are centered,

            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY)

            transformationMatrix.postScale(detector.scaleFactor, detector.scaleFactor)
            realScale*=detector.scaleFactor

            currentLayer = changeLayer(realScale)
            //changeLayer(realScale)
/* Adding focus shift to allow for scrolling with two pointers down. Remove it to skip this functionality. This could be done in fewer lines, but for clarity I do it this way here */
            //Edited after comment by chochim

/* Adding focus shift to allow for scrolling with two pointers down. Remove it to skip this functionality. This could be done in fewer lines, but for clarity I do it this way here */
            //Edited after comment by chochim
            val focusShiftX = focusX - lastFocusX
            val focusShiftY = focusY - lastFocusY
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY)
            drawMatrix!!.postConcat(transformationMatrix)
            lastFocusX = focusX
            lastFocusY = focusY
            invalidate()
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            mode = WORKING
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
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
            drawMatrix?.postTranslate(-distanceX, -distanceY);
            invalidate();
            return true;
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGesture.onTouchEvent(event)
        gestures.onTouchEvent(event)
        return true
    }

    init {
        //initializing layers
        layers[0] = arrayOf(BitmapInfo(R.drawable.back, 0f, 0f, 1f))
        layers[1] = arrayOf(BitmapInfo(R.drawable.body_insides, 0f, 0f, 2f))
        layers[2] = arrayOf(BitmapInfo(R.drawable.battery, 0f, 0f, 4f), BitmapInfo(R.drawable.board_bottom, 0f, 0f, 4f), BitmapInfo(R.drawable.board_top, 0f, 0f, 2f))
        //initializing variables
        drawMatrix = Matrix()
        currentLayer = BitmapFactory.decodeResource(resources, ic)
        nextLayer = BitmapFactory.decodeResource(resources, R.drawable.body_insides)
        //This is a full screen view
        screenWidth = getResources().getDisplayMetrics().widthPixels
        screenHeight = getResources().getDisplayMetrics().heightPixels
        currentPaint = Paint()
        nextPaint = Paint()
        nextPaint.alpha = 0
        currentPaint.setAntiAlias(true)
        currentPaint.setFilterBitmap(true)
        currentPaint.setDither(true)
        currentPaint.setColor(Color.WHITE)
        scaleGesture = ScaleGestureDetector(getContext(),
                ScaleListener())
        gestures = GestureDetector(getContext(), GestureListener())
        mode = START
        initialize()
    }

    fun changeLayer(scale: Float): Bitmap {

        return if (scale >= 5){
            nextPaint.alpha = 255
            val bitmapSource: Bitmap  = BitmapFactory.decodeResource(resources, R.drawable.body_insides)
            Bitmap.createBitmap(bitmapSource,
                    0, 0, bitmapSource.width, bitmapSource.height, matrix, true)

        }
        else {
            nextPaint.alpha = 0
            val bitmapSource: Bitmap  = BitmapFactory.decodeResource(resources, R.drawable.back)
            Bitmap.createBitmap(bitmapSource,
                    0, 0, bitmapSource.width, bitmapSource.height, matrix, true)

        }
    }
}