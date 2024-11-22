package com.example.weatherwear.data.model

data class Weather(
    val forecastDate : String,      // 예보 날짜
    val forecastTime : String,      // 예보 시간
    val temp: Double,               // 1시간 기온 (TMP)
    val minTemp: Double,            // 일 최저기온 (TMN)
    val maxTemp: Double,            // 일 최고기온 (TMX)
    val rainAmount: Double,         // 1시간 강수량 (PCP)
    val humid: Double,              // 습도 (REH)
    val windSpeed: Double,          // 풍속 (WSD)
    val rainProbability: Double,    // 강수확률 (POP)
    val rainType: String,           // 강수형태 (PTY)
    val skyCondition: String        // 하늘상태 (SKY)
)

//data class Weather2(
//
//    val hourlyWheather: List<HourlyWeather>, // 시간별 날씨 정보
//    val minTemp: Double,            // 일 최저기온 (TMN)
//    val maxTemp: Double           // 일 최고기온 (TMX)
//)
//
//data class HourlyWeather(
//    val temp: Double,               // 1시간 기온 (TMP)
//    val rainType: String,           // 강수형태 (PTY)
//    val skyCondition: String,        // 하늘상태 (SKY)
//    val rainAmount: Double,         // 1시간 강수량 (PCP)
//    val humid: Double,              // 습도 (REH)
//    val windSpeed: Double,          // 풍속 (WSD)
//    val rainProbability: Double    // 강수확률 (POP)
//)
