package com.example.apitest

import RegionAndWeather
import RWCResponse
import com.example.weatherwear.data.model.Clothing
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.Weather

class SampleRWC {

    // 샘플 RWCResponse 생성 함수
    fun createSampleRWCResponse(): RWCResponse {
        // 샘플 날씨 데이터 생성
        val sampleWeather1 = Weather(
            forecastDate = "2024-11-23",
            forecastTime = "15:00",
            temp = 10.5,
            minTemp = 5.0,
            maxTemp = 12.0,
            rainAmount = 2.5,
            rainProbability = 40.0,
            rainType = "소나기",
            skyCondition = "구름 많음",
            humid = 70.0,
            windSpeed = 1.8
        )

        val sampleWeather2 = Weather(
            forecastDate = "2024-11-23",
            forecastTime = "18:00",
            temp = 8.0,
            minTemp = 5.0,
            maxTemp = 12.0,
            rainAmount = 0.0,
            rainProbability = 10.0,
            rainType = "없음",
            skyCondition = "맑음",
            humid = 65.0,
            windSpeed = 1.2
        )

        // 샘플 지역 및 날씨 정보 생성
        val sampleRegionAndWeather1 = RegionAndWeather(
            regionName = "서울",
            weather = sampleWeather1
        )

        val sampleRegionAndWeather2 = RegionAndWeather(
            regionName = "부산",
            weather = sampleWeather2
        )

        // 샘플 추천 의상 세트 생성
        val sampleClothingSet = ClothingSet(
            id = 1,
            recommendedClothings = listOf(
                Clothing(name = "롱패딩", type = "외투"),
                Clothing(name = "니트", type = "상의"),
                Clothing(name = "청바지", type = "하의"),
                Clothing(name = "부츠", type = "신발")
            )
        )

        // 최종 RWCResponse 생성
        return RWCResponse(
            regionAndWeather = listOf(sampleRegionAndWeather1, sampleRegionAndWeather2),
            clothingSet = sampleClothingSet
        )
    }
}
