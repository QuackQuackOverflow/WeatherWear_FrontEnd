package com.example.apitest

import RWResponse
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.helpers.GetClothingHelper
import com.example.weatherwear.helpers.LocationToWeatherHelper
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var getClothingSetButton: Button
    private lateinit var getRegionAndWeatherButton: Button

    // Helper 객체
    private lateinit var locationToWeatherHelper: LocationToWeatherHelper
    private lateinit var getClothingHelper: GetClothingHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

        // UI 초기화
        resultTextView = findViewById(R.id.textViewResult)
        getClothingSetButton = findViewById(R.id.buttonGetClothingSet)
        getRegionAndWeatherButton = findViewById(R.id.buttonGetRegionAndWeather)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationToWeatherHelper = LocationToWeatherHelper(this, fusedLocationClient)
        getClothingHelper = GetClothingHelper(this)

        // 의류 추천받기 버튼 클릭
        getClothingSetButton.setOnClickListener {
            fetchAndDisplayClothingSet()
        }

        // 지역과 날씨 정보 버튼 클릭
        getRegionAndWeatherButton.setOnClickListener {
            fetchAndDisplayRegionAndWeather()
        }
    }

    // 의류 추천 데이터 가져오기 및 UI 반영
    private fun fetchAndDisplayClothingSet() {
        getClothingHelper.fetchClothingSet(userType = "hot") { clothingSet ->
            if (clothingSet != null) {
                getClothingHelper.saveClothingSetToPreferences(clothingSet)
                val resultText = buildClothingSetDisplayText(clothingSet)
                resultTextView.text = resultText
            } else {
                resultTextView.text = "추천 의상 세트를 가져올 수 없습니다."
            }
        }
    }

    // 지역 및 날씨 정보 가져오기 및 UI 반영
    private fun fetchAndDisplayRegionAndWeather() {
        locationToWeatherHelper.fetchRegionAndWeatherFromGPS { weatherData ->
            if (weatherData != null) {
                locationToWeatherHelper.saveRegionAndWeatherToPreferences(weatherData)
                val resultText = buildRegionAndWeatherDisplayText(weatherData)
                resultTextView.text = resultText
            } else {
                resultTextView.text = "지역 및 날씨 정보를 가져올 수 없습니다."
            }
        }
    }

    // 의상 세트 텍스트 포맷팅
    private fun buildClothingSetDisplayText(clothingSet: ClothingSet): String {
        val builder = StringBuilder()
        builder.append("의상 Set ID: ${clothingSet.id}\n")
        clothingSet.recommendedClothings.forEachIndexed { index, clothing ->
            builder.append("옷${index + 1}: ${clothing.name} (${clothing.type})\n")
        }
        return builder.toString()
    }

    // 지역 및 날씨 정보 텍스트 포맷팅
    private fun buildRegionAndWeatherDisplayText(weatherData: RWResponse): String {
        val builder = StringBuilder()
        builder.append("지역: ${weatherData.regionName ?: "알 수 없음"}\n")
        builder.append("날씨 정보:\n")
        builder.append("- 1시간 기온: ${weatherData.weather?.temp ?: "Unknown"} °C\n")
        builder.append("- 최저 기온: ${weatherData.weather?.minTemp ?: "Unknown"} °C\n")
        builder.append("- 최고 기온: ${weatherData.weather?.maxTemp ?: "Unknown"} °C\n")
        builder.append("- 강수량: ${weatherData.weather?.rainAmount ?: "Unknown"} mm\n")
        builder.append("- 강수 확률: ${weatherData.weather?.rainProbability ?: "Unknown"} %\n")
        builder.append("- 강수 형태: ${weatherData.weather?.rainType ?: "Unknown"}\n")
        builder.append("- 하늘 상태: ${weatherData.weather?.skyCondition ?: "Unknown"}\n")
        builder.append("- 습도: ${weatherData.weather?.humid ?: "Unknown"}%\n")
        builder.append("- 풍속: ${weatherData.weather?.windSpeed ?: "Unknown"}m/s")
        return builder.toString()
    }
}
