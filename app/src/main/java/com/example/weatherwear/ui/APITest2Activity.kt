package com.example.apitest

import RWResponse
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var getRegionAndWeatherCustomButton: Button
    private lateinit var nxEditText: EditText
    private lateinit var nyEditText: EditText

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
        getRegionAndWeatherCustomButton = findViewById(R.id.buttonGetRegionAndWeather_Custom)
        nxEditText = findViewById(R.id.editTextNx)
        nyEditText = findViewById(R.id.editTextNy)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationToWeatherHelper = LocationToWeatherHelper(this, fusedLocationClient)
        getClothingHelper = GetClothingHelper(this)

        // 의류 추천받기 버튼 클릭
        getClothingSetButton.setOnClickListener {
            fetchAndDisplayClothingSet()
        }

        // GPS 기반 지역과 날씨 정보 버튼 클릭
        getRegionAndWeatherButton.setOnClickListener {
            fetchAndDisplayRegionAndWeather()
        }

        // 사용자 입력 nx, ny로 지역과 날씨 정보 버튼 클릭
        getRegionAndWeatherCustomButton.setOnClickListener {
            fetchAndDisplayRegionAndWeatherCustom()
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

    // GPS 기반 지역 및 날씨 정보 가져오기 및 UI 반영
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

    // 사용자 입력 nx, ny로 지역 및 날씨 정보 가져오기 및 UI 반영
    private fun fetchAndDisplayRegionAndWeatherCustom() {
        val nx = nxEditText.text.toString().toIntOrNull()
        val ny = nyEditText.text.toString().toIntOrNull()

        if (nx == null || ny == null) {
            resultTextView.text = "nx와 ny 값을 올바르게 입력해주세요."
            return
        }

        locationToWeatherHelper.fetchRegionAndWeather(nx, ny) { weatherData ->
            if (weatherData != null) {
                locationToWeatherHelper.saveRegionAndWeatherToPreferences(weatherData)
                val resultText = buildRegionAndWeatherDisplayText(weatherData)
                resultTextView.text = resultText
            } else {
                resultTextView.text = "입력한 nx, ny로 지역 및 날씨 정보를 가져올 수 없습니다."
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
        builder.append("지역: ${weatherData.regionName}\n")
        builder.append("날씨 정보:\n")
        weatherData.weatherList.forEachIndexed { index, weather ->
            builder.append("날씨 정보 ${index + 1}:\n")
            builder.append(" - 날짜: ${weather.forecastDate}\n")
            builder.append(" - 시간: ${weather.forecastTime}\n")
            builder.append(" - 1시간 기온: ${weather.temp} °C\n")
            builder.append(" - 최저 기온: ${weather.minTemp} °C\n")
            builder.append(" - 최고 기온: ${weather.maxTemp} °C\n")
            builder.append(" - 강수량: ${weather.rainAmount} mm\n")
            builder.append(" - 강수 확률: ${weather.rainProbability} %\n")
            builder.append(" - 강수 형태: ${weather.rainType}\n")
            builder.append(" - 하늘 상태: ${weather.skyCondition}\n")
            builder.append(" - 습도: ${weather.humid} %\n")
            builder.append(" - 풍속: ${weather.windSpeed} m/s\n")
            builder.append("\n")
        }
        return builder.toString()
    }
}
