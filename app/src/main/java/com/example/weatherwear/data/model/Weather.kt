package com.example.weatherwear.data.model

data class Weather(

    val temp : Double,                  // 1시간 기온
    val probabilityOfRain : Double,     // 강수 확률
    val rainAmount : Double,            // 강수 형태
    val precipitationForm : Integer,    // 하늘 상태
    val skyCondition : Integer,         // 마지막 갱신 시각(시간 단위)
    val lastUpdateTime : String,        // 일 최고 기온
    val highestDailyTemp : Double,      // 일 최저 기온
    val lowestDailyTemp : Double,       // 습도
    val humid : Double                  // 습도
)
