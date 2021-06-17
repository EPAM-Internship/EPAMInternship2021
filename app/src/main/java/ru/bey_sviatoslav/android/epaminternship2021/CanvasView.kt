package ru.bey_sviatoslav.android.epaminternship2021

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(MainActivity().MyBitmap(this, R.drawable.motherboard))
    }


    internal inner class MyBitmap(context: Context?,ic : Int ) : View(context) {


        val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val bitmap: Bitmap
        private val bitmapSource: Bitmap = BitmapFactory.decodeResource(resources, ic)
        private val myMatrix: Matrix

        init {
            myMatrix = Matrix()
            bitmap = Bitmap.createBitmap(bitmapSource, 0, 0, bitmapSource.width, bitmapSource.height, matrix, true);
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
        }
    }

}
