package ru.bey_sviatoslav.android.epaminternship2021

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ru.bey_sviatoslav.android.epaminternship2021.databinding.ActivityMainBinding


private var mScaleGestureDetector: ScaleGestureDetector? = null
private var mScaleFactor: Float = 1.0f
private var mAlphaFactor: Float = 1.0f

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        supportActionBar?.hide()

        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener(binding.imageView))

        val observer: ViewTreeObserver = binding.imageView.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            binding.scaleFactorValue.text = mScaleFactor.toString()
        }

        setContentView(view)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector?.onTouchEvent(event)
        binding.xValue.text = event?.x?.toInt().toString()
        binding.yValue.text = event?.y?.toInt().toString()
        return super.onTouchEvent(event)
    }

    private class ScaleListener(var image: ImageView) : SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mAlphaFactor *= scaleGestureDetector.scaleFactor
            mAlphaFactor = 0.0f.coerceAtLeast(mAlphaFactor.coerceAtLeast(1.0f))
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = 0.1f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f))
            image.scaleX = mScaleFactor
            image.scaleY = mScaleFactor
            image.alpha = mScaleFactor
            return true
        }
    }
}