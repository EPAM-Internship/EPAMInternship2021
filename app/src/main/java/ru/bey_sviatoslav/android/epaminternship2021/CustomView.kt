package ru.bey_sviatoslav.android.epaminternship2021

import android.content.Context
import android.graphics.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View


class CustomView(context: Context?, ic: Int) : View(context) {
    var image: Bitmap
    //private val bitmapSource: Bitmap = BitmapFactory.decodeResource(resources, ic)
    var screenHeight: Int
    var screenWidth: Int
    var paint: Paint
    var gestures: GestureDetector
    var scaleGesture: ScaleGestureDetector
    var scale = 1.0f
    var horizontalOffset = 0f
    var verticalOffset = 0f
    var NORMAL = 0
    var ZOOM = 1
    var DRAG = 2
    var isScaling = false
    var touchX = 0f
    var touchY = 0f
    var mode = NORMAL
    var drawMatrix: Matrix? = null
    var lastFocusX = 0f
    var lastFocusY = 0f

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

    protected override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawBitmap(image, drawMatrix!!, paint);
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
            mode = ZOOM
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            mode = NORMAL
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
        //initializing variables
        drawMatrix = Matrix()
        image = BitmapFactory.decodeResource(resources, ic)
        //This is a full screen view
        screenWidth = getResources().getDisplayMetrics().widthPixels
        screenHeight = getResources().getDisplayMetrics().heightPixels
        paint = Paint()
        paint.setAntiAlias(true)
        paint.setFilterBitmap(true)
        paint.setDither(true)
        paint.setColor(Color.WHITE)
        scaleGesture = ScaleGestureDetector(getContext(),
                ScaleListener())
        gestures = GestureDetector(getContext(), GestureListener())
        mode = NORMAL
        initialize()
    }
}