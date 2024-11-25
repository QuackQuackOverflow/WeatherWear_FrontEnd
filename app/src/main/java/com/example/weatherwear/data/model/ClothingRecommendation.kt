package com.example.weatherwear.data.model

data class ClothingRecommendation(
    val temperature: String,          // 온도 (예: "15°C")
    val recommendations: List<String> // 추천 의상 리스트
)

