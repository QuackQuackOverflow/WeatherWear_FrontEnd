package com.example.weatherwear.data.sample

import RegionAndWeather
import RWCResponse
import com.example.weatherwear.data.model.Clothing
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.Weather
import kotlin.random.Random

object SampleRWC {

    private val random = Random(System.currentTimeMillis())

    // 샘플 RWCResponse 생성 함수
    fun createSampleRWCResponse(): RWCResponse {
        // Weather 객체 48개를 생성
        val weatherList = mutableListOf<RegionAndWeather>()
        for (i in 1..48) {
            val randomTemp = random.nextInt(0, 21) // 0~20 사이의 난수 온도
            val randomSkyCondition = if (random.nextBoolean()) "맑음" else "흐림"
            val hour = (i - 1) % 24
            val forecastTime = String.format("%02d00", hour) // 00:00, 01:00 형태의 시간 생성
            val forecastDate = if (i <= 24) "20241123" else "20241124" // 날짜는 24개씩 나누어 생성

            val weather = Weather(
                forecastDate = forecastDate,
                forecastTime = forecastTime,
                temp = randomTemp.toDouble(),
                minTemp = (randomTemp - random.nextInt(1, 5)).toDouble(),
                maxTemp = (randomTemp + random.nextInt(1, 5)).toDouble(),
                rainAmount = random.nextDouble(0.0, 5.0),
                rainProbability = random.nextDouble(0.0, 100.0),
                rainType = if (random.nextBoolean()) "비" else "눈",
                skyCondition = randomSkyCondition,
                humid = random.nextDouble(30.0, 90.0),
                windSpeed = random.nextDouble(0.5, 5.0)
            )

            val regionAndWeather = RegionAndWeather(
                regionName = "테스트용 지역 이름",
                weather = weather
            )
            weatherList.add(regionAndWeather)
        }

        // 샘플 추천 의상 세트 생성
        val sampleClothingSet = ClothingSet(
            id = 1,
            recommendedClothings = List(6) { index ->
                Clothing(name = "테스트용 옷${index + 1}", type = "상의")
            }
        )

        // 최종 RWCResponse 생성
        return RWCResponse(
            regionAndWeather = weatherList,
            clothingSet = sampleClothingSet
        )
    }
}
