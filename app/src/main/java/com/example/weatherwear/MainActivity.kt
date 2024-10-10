package com.example.weatherwear

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var customDialog: ClothingPopup
    private lateinit var reviewPopup: ReviewPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // ImageButton들에 대한 클릭 리스너 설정
        findViewById<ImageButton>(R.id.clothingButton1).setOnClickListener {
            showClothingPopup()
        }
        findViewById<ImageButton>(R.id.clothingButton2).setOnClickListener {
            //수정 필요
            showReviewPopup()
        }
        findViewById<ImageButton>(R.id.clothingButton3).setOnClickListener {
            showClothingPopup()
        }
        findViewById<ImageButton>(R.id.clothingButton4).setOnClickListener {
            showClothingPopup()
        }
        findViewById<ImageButton>(R.id.clothingButton5).setOnClickListener {
            showClothingPopup()
        }

    }

    private fun showClothingPopup() {
        customDialog = ClothingPopup(this)
        customDialog.show()
    }

    private fun showReviewPopup() {
        reviewPopup = ReviewPopup(this)
        reviewPopup.show()
    }
}
