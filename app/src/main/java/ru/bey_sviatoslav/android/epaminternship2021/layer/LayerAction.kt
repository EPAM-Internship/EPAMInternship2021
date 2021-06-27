package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.Bitmap

data class LayerAction(val scaleFrom: Float,
                       val scaleTo: Float,
                       val bitmapFrom: Bitmap,
                       val listOfBitmaps: List<Bitmap>)
