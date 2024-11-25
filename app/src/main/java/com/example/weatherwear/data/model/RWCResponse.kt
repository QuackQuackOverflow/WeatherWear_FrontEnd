package com.example.weatherwear.data.model

data class RWCResponse(
    val regionWeather: RegionAndWeather?,                      // 지역 및 날씨 정보 (첫 번째 Map)
    val clothingRecommendations: List<ClothingRecommendation>? // 의상 추천 정보 (그 이후 Maps)
)
