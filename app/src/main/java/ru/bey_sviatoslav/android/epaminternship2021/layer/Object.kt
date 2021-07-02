package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.Canvas
import android.graphics.Paint

abstract class Object(val x: Float, val y: Float) {
    abstract fun draw(canvas: Canvas, paint: Paint)
}