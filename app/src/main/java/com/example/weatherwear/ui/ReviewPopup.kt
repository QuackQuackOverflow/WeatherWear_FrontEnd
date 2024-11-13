package com.example.weatherwear.ui

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import com.example.weatherwear.R

// ReviewPopup.kt

import android.widget.ImageButton  // AppCompatImageButton으로 변경

class ReviewPopup(context: Context) : Dialog(context) {

    private val closeReviewButton: ImageButton  // Button에서 ImageButton으로 변경
    private val coldButton: Button
    private val hotButton: Button
    private val goodButton: Button  // "마음에 들어요" 버튼 추가

    init {
        setContentView(R.layout.activity_review_popup)  // Review 팝업 레이아웃

        // 뒤로가기(닫기 버튼) 액션 처리
        closeReviewButton = findViewById(R.id.closeReview) as ImageButton  // AppCompatImageButton으로 변경
        closeReviewButton.setOnClickListener {
            dismiss()
        }

        // "추웠어요" 버튼 액션 처리
        coldButton = findViewById(R.id.btn_cold)
        coldButton.setOnClickListener {
            Toast.makeText(context, "추웠어요 선택됨", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // "더웠어요" 버튼 액션 처리
        hotButton = findViewById(R.id.btn_hot)
        hotButton.setOnClickListener {
            Toast.makeText(context, "더웠어요 선택됨", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // "마음에 들어요" 버튼 액션 처리
        goodButton = findViewById(R.id.btn_good)
        goodButton.setOnClickListener {
            Toast.makeText(context, "마음에 들어요 선택됨", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}
