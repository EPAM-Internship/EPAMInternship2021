package ru.bey_sviatoslav.android.epaminternship2021

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import ru.bey_sviatoslav.android.epaminternship2021.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        val observer: ViewTreeObserver = binding.drawableImage.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            binding.scaleFactorValue.text = binding.drawableImage.mScaleFactor.toString()
        }

        supportActionBar?.hide()

        setContentView(view)
    }
}