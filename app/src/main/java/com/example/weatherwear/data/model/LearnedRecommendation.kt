package com.example.weatherwear.data.model

data class LearnedRecommendation(
    val memberEmail: String,      // LoginPrefs에서 memberEmail 불러오기
    val temperature: Double,      // temperature
    val optimizedClothing: String // JSON 형태의 추천 의류 목록
)