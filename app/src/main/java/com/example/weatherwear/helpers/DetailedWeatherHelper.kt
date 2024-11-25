package com.example.weatherwear.helpers

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.example.weatherwear.R
import com.example.weatherwear.data.model.RegionAndWeather
import kotlin.math.roundToInt

//object DetailedWeatherHelper {
//
//    fun addWeatherDetails(context: Context, parentLayout: LinearLayout, weatherList: List<RegionAndWeather>) {
//        // 날짜별로 데이터를 그룹화
//        val groupedByDate = weatherList.groupBy { it.weather.forecastDate }
//
//        groupedByDate.forEach { (date, dailyWeather) ->
//            val dailyLayout = LinearLayout(context).apply {
//                orientation = LinearLayout.VERTICAL
//                layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//            }
//
//            // 날짜 TextView
//            val dateTextView = TextView(context).apply {
//                text = date
//                textSize = 20f
//                setBackgroundColor(context.resources.getColor(R.color.skyblue, null))
//                setTextColor(context.resources.getColor(R.color.black, null))
//                setPadding(20, 10, 20, 10)
//            }
//            dailyLayout.addView(dateTextView)
//
//            // 시간대별 날씨 타이틀
//            val hourlyWeatherTitle = TextView(context).apply {
//                text = "시간대별 날씨"
//                textSize = 18f
//                setBackgroundColor(context.resources.getColor(R.color.lightSkyblue, null))
//                setTextColor(context.resources.getColor(R.color.black, null))
//                setPadding(20, 10, 20, 10)
//            }
//            dailyLayout.addView(hourlyWeatherTitle)
//
//            // 추가 정보
//            val minTemp = dailyWeather.minOf { it.weather.temp }
//            val maxTemp = dailyWeather.maxOf { it.weather.temp }
//            val totalRainAmount = dailyWeather.sumOf { it.weather.rainAmount.toDouble() }
//            val averageRainAmount = dailyWeather.map { it.weather.rainAmount }.average()
//            val averageWindSpeed = dailyWeather.map { it.weather.windSpeed }.average()
//            val averageHumidity = dailyWeather.map { it.weather.humid }.average()
//
//            val detailedInfo = TextView(context).apply {
//                text = """
//                    최고 기온: ${maxTemp}°C
//                    최저 기온: ${minTemp}°C
//                    24시간 강수량: ${totalRainAmount}mm
//                    평균 강수량: ${averageRainAmount.roundToInt()}mm
//                    평균 풍속: ${averageWindSpeed.roundToInt()}m/s
//                    평균 습도: ${averageHumidity.roundToInt()}%
//                """.trimIndent()
//                textSize = 16f
//                setPadding(20, 10, 20, 10)
//            }
//
//            dailyLayout.addView(detailedInfo)
//            parentLayout.addView(dailyLayout)
//        }
//    }
//}
