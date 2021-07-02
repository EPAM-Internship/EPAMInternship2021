package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.bey_sviatoslav.android.epaminternship2021.R
import ru.bey_sviatoslav.android.epaminternship2021.view.CustomView

class LayoutManager(view: CustomView) {
    val backBitmap: Bitmap = BitmapFactory.decodeResource(view.resources, R.drawable.back)
    private val insidesBitmap: Bitmap = BitmapFactory.decodeResource(view.resources, R.drawable.body_insides)
    private val boardTopBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.board_top_)
    private val boardBottomBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.board_bottom_)
    private val batteryBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.battery_)
    private val cameraBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.camera_)

    private val backBitmapObject = BitmapObject(0f, 0f, backBitmap)
    private val insidesBitmapObject = BitmapObject(0f, 0f, insidesBitmap)
    private val boardTopBitmapObject = BitmapObject(471f, 822f, boardTopBitmap)
    private val boardBottomBitmapObject = BitmapObject(485f, 3237f, boardBottomBitmap)
    private val batteryBitmapObject = BitmapObject(880f, 1680f, batteryBitmap)
    private val cameraBitmapObject = BitmapObject(1055f, 1170f, cameraBitmap)

    val firstLayer = Layer(1.0f, 1.4999f, listOf(backBitmapObject))
    val secondLayer = Layer(1.5f, 1.9999f, listOf(insidesBitmapObject))
    val thirdLayer = Layer(2.0f, 5.0f, listOf(insidesBitmapObject, boardBottomBitmapObject, boardTopBitmapObject, batteryBitmapObject, cameraBitmapObject))
}