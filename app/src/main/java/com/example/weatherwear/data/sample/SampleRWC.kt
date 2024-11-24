package com.example.weatherwear.data.samples

import RWResponse
import RWCResponse
import com.example.weatherwear.data.model.Clothing
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.Weather

object SampleRWC {

    // 샘플 RWCResponse 생성 함수
    fun createSampleRWCResponse(): RWCResponse {
        // 샘플 날씨 데이터 생성
        val sampleWeather1 = Weather(
            forecastDate = "20241123", // 수정된 날짜 형식
            forecastTime = "1500",    // 수정된 시간 형식
            temp = 10.5,
            minTemp = 5.0,
            maxTemp = 12.0,
            rainAmount = 2.5,
            rainProbability = 40.0,
            rainType = "비",          // 수정된 rainType
            skyCondition = "구름 많음", // 수정된 skyCondition
            humid = 70.0,
            windSpeed = 1.8
        )

        val sampleWeather2 = Weather(
            forecastDate = "20241123",
            forecastTime = "1800",
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

        val sampleWeather3 = Weather(
            forecastDate = "20241124",
            forecastTime = "0900",
            temp = 2.0,
            minTemp = -1.0,
            maxTemp = 4.0,
            rainAmount = 5.0,
            rainProbability = 80.0,
            rainType = "눈",
            skyCondition = "구름 많음",
            humid = 85.0,
            windSpeed = 2.3
        )

        val sampleWeather4 = Weather(
            forecastDate = "20241124",
            forecastTime = "1200",
            temp = 5.5,
            minTemp = 3.0,
            maxTemp = 8.0,
            rainAmount = 0.0,
            rainProbability = 0.0,
            rainType = "없음",
            skyCondition = "맑음",
            humid = 50.0,
            windSpeed = 1.0
        )

        val sampleWeather5 = Weather(
            forecastDate = "20241125",
            forecastTime = "0000",
            temp = -3.0,
            minTemp = -5.0,
            maxTemp = -1.0,
            rainAmount = 1.0,
            rainProbability = 20.0,
            rainType = "눈",
            skyCondition = "구름 많음",
            humid = 90.0,
            windSpeed = 3.5
        )

        // 샘플 지역 및 날씨 정보 생성
        val sampleRWResponse1 = RWResponse(
            regionName = "서울",
            weather = sampleWeather1
        )

        val sampleRWResponse2 = RWResponse(
            regionName = "부산",
            weather = sampleWeather2
        )

        val sampleRWResponse3 = RWResponse(
            regionName = "대구",
            weather = sampleWeather3
        )

        val sampleRWResponse4 = RWResponse(
            regionName = "광주",
            weather = sampleWeather4
        )

        val sampleRWResponse5 = RWResponse(
            regionName = "제주",
            weather = sampleWeather5
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
            regionAndWeather = listOf(
                sampleRWResponse1,
                sampleRWResponse2,
                sampleRWResponse3,
                sampleRWResponse4,
                sampleRWResponse5
            ),
            clothingSet = sampleClothingSet
        )
    }
}
