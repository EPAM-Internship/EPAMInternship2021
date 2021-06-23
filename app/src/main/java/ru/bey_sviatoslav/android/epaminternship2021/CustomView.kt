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
    var screenHeight: Int
    var screenWidth: Int
    var paint: Paint
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
    var maxScale = 5

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

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)
        canvas.drawColor(Color.WHITE)
        if (mode == START) {
            //This works perfectly as expected
            drawMatrix?.postTranslate(horizontalOffset, verticalOffset)
            drawMatrix?.postScale(scale, scale)
        }
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
            realScale *= detector.scaleFactor

            changeAlpha(realScale)
            image = changeImage(realScale)

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
        mode = START
        initialize()
    }

    private fun changeImage(scale: Float): Bitmap {

        return if (scale >= maxScale) {
            val bitmapSource: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_battery)
            Bitmap.createBitmap(bitmapSource,
                    0, 0, bitmapSource.width, bitmapSource.height, matrix, true)

        } else {
            val bitmapSource: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.back)
            Bitmap.createBitmap(bitmapSource,
                    0, 0, bitmapSource.width, bitmapSource.height, matrix, true)

        }
    }

    private fun changeAlpha(scale: Float) {
        image.let {
            image = adjustOpacity(it,  scale/maxScale * 100)
        }

    }

    private fun adjustOpacity(bitmap: Bitmap, opacity: Float): Bitmap {
        val mutableBitmap = if (bitmap.isMutable) bitmap else bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val colour = opacity.toInt() and 0xFF shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        return mutableBitmap
    }
}