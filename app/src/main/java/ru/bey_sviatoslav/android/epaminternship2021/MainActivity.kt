package ru.bey_sviatoslav.android.epaminternship2021

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(MainActivity().DetailedView(this, R.drawable.motherboard))
    }


    internal inner class DetailedView(context: Context?, ic: Int) : View(context) {


        val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val bitmap: Bitmap
        private val bitmapSource: Bitmap = BitmapFactory.decodeResource(resources, ic)
        private val myMatrix: Matrix
        private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor

                // Don't let the object get too small or too large.
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f))

                invalidate()
                return true
            }

        }
        private var scaleDetector: ScaleGestureDetector? = null
        private var scaleFactor = 1f

        init {
            myMatrix = Matrix()
            bitmap = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.width, bitmapSource.height, matrix, true);
            scaleDetector = ScaleGestureDetector(context, scaleListener)
        }

        override fun onTouchEvent(ev: MotionEvent): Boolean {
            // Let the ScaleGestureDetector inspect all events.
            scaleDetector?.onTouchEvent(ev)
            return true
        }

        override fun onDraw(canvas: Canvas) {
            canvas.save();
            canvas.scale(scaleFactor, scaleFactor);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
            canvas.restore();
        }
    }

}