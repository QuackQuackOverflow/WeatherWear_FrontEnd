package com.example.weatherwear.data.model

data class Review(
    val memberEmail: String,    // 회원 이메일
    val evaluationScore: Int,   // 평가 점수 (-1, 0, 1)
    val evaluationDate: String  // 평가 날짜 (YYYY-MM-DD)
)
