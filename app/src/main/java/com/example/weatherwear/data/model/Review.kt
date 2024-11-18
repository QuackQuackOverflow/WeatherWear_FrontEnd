package com.example.weatherwear.data.model

data class Review(
    val clothingId: String, // 평가받은 옷 ID 또는 이름
    val feedback: String    // "추웠어요", "더웠어요", "마음에 들어요"
)
