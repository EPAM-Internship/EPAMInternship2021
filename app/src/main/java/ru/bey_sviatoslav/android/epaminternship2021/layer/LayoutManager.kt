package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import ru.bey_sviatoslav.android.epaminternship2021.R
import ru.bey_sviatoslav.android.epaminternship2021.view.CustomView

class LayoutManager(view: CustomView) {
    private val insidesBitmap: Bitmap = BitmapFactory.decodeResource(view.resources, R.drawable.body_insides)
    private val boardTopBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.board_top)
    private val boardBottomBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.board_bottom)
    private val batteryBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.battery)
    private val cameraBitmap = BitmapFactory.decodeResource(view.resources, R.drawable.camera)

    val backBitmap: Bitmap = BitmapFactory.decodeResource(view.resources, R.drawable.back)
    val firstLayerAction = LayerAction(1.0f, 1.4999f, backBitmap, listOf(insidesBitmap))
    val secondLayerAction = LayerAction(1.5f, 1.9999f, insidesBitmap, listOf(boardBottomBitmap, batteryBitmap, boardTopBitmap, cameraBitmap))
    val thirdLayer = Layer(
        2.0f,
        5.0f,
        listOf(insidesBitmap, boardBottomBitmap, batteryBitmap, boardTopBitmap, cameraBitmap),
        listOf(StupidObject(500f, 500f, Color.GREEN), StupidObject(500f, 700f, Color.RED))
    )
}