package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

class BitmapObject(x: Float, y: Float, private val bitmap: Bitmap) : Object(x, y) {
    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(bitmap, x, y, paint)
    }
}