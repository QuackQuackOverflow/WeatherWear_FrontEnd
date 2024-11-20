package com.example.weatherwear.data.model

data class ClothingSet(
    val id: Int,                              // 세트 식별자
    val recommendedClothings : List<Clothing> // 의상 객체 리스트
)
