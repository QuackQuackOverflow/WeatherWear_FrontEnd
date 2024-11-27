package com.example.weatherwear.data.model

import java.io.Serializable

data class Weather(
    val id : Int?,                   // 식별자
    val forecastDate : String?,      // 예보 날짜
    val forecastTime : String?,      // 예보 시간
    val temp: Double?,               // 1시간 기온 (TMP)
    val minTemp: Double?,            // 일 최저기온 (TMN)
    val maxTemp: Double?,            // 일 최고기온 (TMX)
    val rainAmount: Double?,         // 1시간 강수량 (PCP)
    val humid: Double?,              // 습도 (REH)
    val windSpeed: Double?,          // 풍속 (WSD)
    val rainProbability: Double?,    // 강수확률 (POP)
    val rainType: String?,           // 강수형태 (PTY)
    val skyCondition: String?,        // 하늘상태 (SKY)
    val lastUpdateTime: String?      // 마지막 업데이트 시간
) : Serializable