package com.example.weatherwear

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.Toast

class ReviewPopup(context: Context) : Dialog(context) {

//    private val closeReviewButton: Button
//    private val coldButton: Button
//    private val hotButton: Button

    init {
        setContentView(R.layout.activity_review_popup)  // Review 팝업 레이아웃

//        //뒤로가기(받기 버튼) 액션 처리
//        closeReviewButton = findViewById(R.id.closeReview)
//        closeReviewButton.setOnClickListener {
//            dismiss()
//        }
//        //추웠어요 버튼 액션 처리
//        coldButton = findViewById(R.id.btn_cold)
//        coldButton.setOnClickListener {
//            Toast.makeText(context, "추웠어요 선택됨", Toast.LENGTH_SHORT).show()
//            dismiss()
//        }
//        //추웠어요 버튼 액션 처리
//        hotButton = findViewById(R.id.btn_hot)
//        hotButton.setOnClickListener {
//            Toast.makeText(context, "더웠어요 선택됨", Toast.LENGTH_SHORT).show()
//            dismiss()
//        }

    }
}
