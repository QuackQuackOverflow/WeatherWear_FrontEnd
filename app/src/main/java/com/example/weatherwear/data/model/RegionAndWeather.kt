package com.example.weatherwear.data.model

import com.example.weatherwear.data.model.Weather
import java.io.Serializable

//data class RWResponse(
//    val regionName: String,            // 지역 이름
//    val weather: Weather,           // 날씨 정보
//)

//RegionWeather 대신 RegionAndWeather라는 이름 사용
data class RegionAndWeather(
    val regionName: String?,     // 지역 이름
    val weather: List<Weather>  // 시간대별 날씨 정보
) : Serializable