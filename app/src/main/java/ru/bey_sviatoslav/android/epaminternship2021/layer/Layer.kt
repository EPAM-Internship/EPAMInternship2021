package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.*

data class Layer(
    val scaleFrom: Float,
    val scaleTo: Float,
    val listOfBitmaps: List<Bitmap>,
    val listOfObjects: List<Object>,
)

abstract class Object(
    val x: Float,
    val y: Float
) {
    abstract fun draw(canvas: Canvas)
}

class BitmapObject(
    x: Float,
    y: Float,
    val bitmap: Bitmap
): Object(x,y) {
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f,0f,null)
    }
}

class StupidObject(
    x: Float,
    y: Float,
    val color: Int
): Object(x,y) {
    override fun draw(canvas: Canvas) {
        canvas.drawRect(RectF(x,y,x+300,y+300), Paint().apply { color = this@StupidObject.color })
    }
}