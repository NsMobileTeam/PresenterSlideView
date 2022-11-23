package com.nextsense.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nextsense.presenterslideview.PresenterSlideView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainItems = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8)
        val mainSliderAdapter = NumbersAdapter()
        mainSliderAdapter.setItems(mainItems)
        val mainSlider = findViewById<PresenterSlideView>(R.id.psv_main_slide)
        mainSlider.setAdapter(mainSliderAdapter)

        val leftItems = arrayListOf(8, 9, 10, 11, 12, 13)
        val leftSliderAdapter = NumbersAdapter()
        leftSliderAdapter.setItems(leftItems)
        val leftSlider = findViewById<PresenterSlideView>(R.id.psv_left_slide)
        leftSlider.setAdapter(leftSliderAdapter)

        val rightItems = arrayListOf(14, 15, 16, 17, 18)
        val rightSliderAdapter = NumbersAdapter()
        rightSliderAdapter.setItems(rightItems)
        val rightSlider = findViewById<PresenterSlideView>(R.id.psv_right_slide)
        rightSlider.setAdapter(rightSliderAdapter)

        val manualItems = arrayListOf(19, 20, 21, 22, 23, 24, 25)
        val manualSliderAdapter = NumbersAdapter()
        manualSliderAdapter.setItems(manualItems)
        val manualSlider = findViewById<PresenterSlideView>(R.id.psv_manual_slide)
        manualSlider.setAdapter(manualSliderAdapter)
    }
}