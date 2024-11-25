package com.example.weatherwear.data.sample

import com.example.weatherwear.data.model.ClothingRecommendation
import com.example.weatherwear.data.model.RWCResponse
import com.example.weatherwear.data.model.RegionAndWeather
import com.example.weatherwear.data.model.Weather
import kotlin.random.Random

object SampleRWC {

    private val random = Random(System.currentTimeMillis())

    // 샘플 RWCResponse 생성 함수
    fun createSampleRWCResponse(): RWCResponse {
        // Weather 리스트 생성
        val weatherList = mutableListOf<Weather>()
        val regionName = "테스트 지역"

        for (i in 1..72) {
            val forecastHour = (i - 1) % 24
            val forecastDate = when {
                i <= 24 -> "20241125"
                i <= 48 -> "20241126"
                else -> "20241127"
            }
            val forecastTime = String.format("%02d00", forecastHour)

            val randomTemp = random.nextInt(-5, 20).toDouble() // 온도 범위: -5°C ~ 20°C
            val randomRainType = listOf("0", "1", "2", "3", "4").random() // 강수 유형
            val randomSkyCondition = listOf("1", "3", "4").random() // 하늘 상태
            val randomHumid = random.nextDouble(30.0, 90.0)
            val randomWindSpeed = random.nextDouble(0.5, 5.0)
            val randomRainProbability = random.nextDouble(0.0, 100.0)
            val randomRainAmount = if (randomRainType == "0") 0.0 else random.nextDouble(0.0, 10.0)

            val weather = Weather(
                id = null,
                forecastDate = forecastDate,
                forecastTime = forecastTime,
                temp = randomTemp,
                minTemp = if (forecastHour == 0) randomTemp - random.nextInt(1, 5) else null,
                maxTemp = if (forecastHour == 12) randomTemp + random.nextInt(1, 5) else null,
                rainAmount = randomRainAmount,
                humid = randomHumid,
                windSpeed = randomWindSpeed,
                rainProbability = randomRainProbability,
                rainType = randomRainType,
                skyCondition = randomSkyCondition,
                lastUpdateTime = "20241125 1035"
            )
            weatherList.add(weather)
        }

        val regionAndWeather = RegionAndWeather(
            regionName = regionName,
            weather = weatherList // List<Weather>로 설정
        )

        // 의상 추천 리스트 생성
        val clothingRecommendations = listOf(
            ClothingRecommendation(
                temperature = "10.0°C",
                recommendations = listOf("하의 - 면바지", "아우터 - 재킷", "복합 - 레이어드 니트")
            ),
            ClothingRecommendation(
                temperature = "0.0°C",
                recommendations = listOf("복합 - 내복", "아우터 - 패딩", "복합 - 방한용품")
            )
        )

        // 최종 RWCResponse 생성
        return RWCResponse(
            regionWeather = regionAndWeather, // 단일 RegionAndWeather로 변경
            clothingRecommendations = clothingRecommendations
        )
    }
}
