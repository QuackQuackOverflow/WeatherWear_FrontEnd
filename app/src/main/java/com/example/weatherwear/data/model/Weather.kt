package com.example.weatherwear.data.model

data class Weather(

    val temp : Double,                  // 1시간 기온
    val probabilityOfRain : Double,     // 강수 확률
    val rainAmount : Double,            // 강수량
    val precipitationForm : Integer,    // 강수 형태
    val skyCondition : Integer,         // 하늘 상태
    val lastUpdateTime : String,        // 마지막 업데이트 시간
    val highestDailyTemp : Double,      // 일 최고 기온
    val lowestDailyTemp : Double,       // 일 최저 기온
    val humid : Double                  // 습도
)
