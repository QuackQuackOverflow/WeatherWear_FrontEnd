package com.example.weatherwear.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.RegionAndWeather

//class DetailedWeatherActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detailed_weather)
//
//        val weatherList = intent.getSerializableExtra("weatherList") as? List<RegionAndWeather>
//
//        val regionNameTextView: TextView = findViewById(R.id.regionName)
//        val scrollViewContent: LinearLayout = findViewById(R.id.scrollViewContent)
//
//        if (weatherList != null) {
//            // 첫 RegionAndWeather 객체의 지역명 설정
//            regionNameTextView.text = "${weatherList.first().regionName}의 상세 예보"
//
//            // Helper를 통해 상세 날씨 정보 UI 동적 추가
//            DetailedWeatherHelper.addWeatherDetails(this, scrollViewContent, weatherList)
//        }
//    }
//}
