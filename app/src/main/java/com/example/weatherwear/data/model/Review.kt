package com.example.weatherwear.data.model

data class Review(
    val memberEmail: String,    // 회원 이메일
    val evaluationScore: Int,   // 평가 점수 (-1, 0, 1)
    val temp : Double           // 어떤 기온에 그 옷을 입었는지
)
