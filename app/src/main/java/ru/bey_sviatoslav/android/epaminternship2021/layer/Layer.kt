package ru.bey_sviatoslav.android.epaminternship2021.layer

import android.graphics.Bitmap

data class Layer(val scaleFrom: Float,
                 val scaleTo: Float,
                 val listOfBitmaps: List<Bitmap>)