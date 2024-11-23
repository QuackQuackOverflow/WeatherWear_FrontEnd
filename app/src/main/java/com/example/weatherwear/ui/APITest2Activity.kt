package com.example.apitest

import RWCResponse
import RWResponse
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.helpers.GetRWCHelper
import com.google.android.gms.location.LocationServices

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var getRegionAndWeatherCustomButton: Button
    /**
     * 샘플 데이터를 테스트하기 위한 버튼
     */
    private lateinit var getSampleDataButton: Button

    // Helper 객체
    private lateinit var getRWCHelper: GetRWCHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

        // UI 초기화
        resultTextView = findViewById(R.id.textViewResult)
        getRegionAndWeatherCustomButton = findViewById(R.id.buttonGetRegionAndWeather_Custom)

        /**
         * 샘플 데이터를 위한 버튼 초기화
         */
        getSampleDataButton = findViewById(R.id.buttonGetSampleData)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getRWCHelper = GetRWCHelper(this, fusedLocationClient)

        // 사용자 입력 nx, ny로 지역 및 날씨 정보 버튼 클릭
        getRegionAndWeatherCustomButton.setOnClickListener {
            getRWCHelper.fetchRWCFromGPS { rwcResponse ->
                if (rwcResponse != null) {
                    val resultText = buildRWCDisplayText(rwcResponse)
                    resultTextView.text = resultText
                } else {
                    resultTextView.text = "RWC 데이터를 가져올 수 없습니다."
                }
            }
        }

        /**
         * 샘플 데이터를 출력하는 버튼
         */
        getSampleDataButton.setOnClickListener {
            val sampleRWCResponse = SampleRWC().createSampleRWCResponse()
            val resultText = buildRWCDisplayText(sampleRWCResponse)
            resultTextView.text = resultText
        }
    }

    // RWCResponse 텍스트 포맷팅
    private fun buildRWCDisplayText(rwcResponse: RWCResponse): String {
        val builder = StringBuilder()
        builder.append("=== 지역 및 날씨 정보 ===\n")
        builder.append(formatWeatherInfoList(rwcResponse.regionAndWeather)) // List에 맞게 수정
        builder.append("\n")
        builder.append("=== 추천 의상 정보 ===\n")
        builder.append(formatClothingInfo(rwcResponse.clothingSet))
        return builder.toString()
    }

    // 지역 및 날씨 정보를 출력하는 함수 (List<RWResponse>에 맞게 수정)
    private fun formatWeatherInfoList(regionAndWeatherList: List<RWResponse>): String {
        val builder = StringBuilder()
        regionAndWeatherList.forEachIndexed { index, regionAndWeather ->
            builder.append("지역 ${index + 1}:\n")
            builder.append(formatWeatherInfo(regionAndWeather))
            builder.append("\n")
        }
        return builder.toString()
    }

    // 단일 RWResponse 날씨 정보를 출력하는 함수
    private fun formatWeatherInfo(regionAndWeather: RWResponse): String {
        val builder = StringBuilder()
        builder.append("지역: ${regionAndWeather.regionName}\n")
        val weather = regionAndWeather.weather
        builder.append(" - 날짜: ${weather.forecastDate}\n")
        builder.append(" - 시간: ${weather.forecastTime}\n")
        builder.append(" - 1시간 기온: ${weather.temp} °C\n")
        builder.append(" - 최저 기온: ${weather.minTemp} °C\n")
        builder.append(" - 최고 기온: ${weather.maxTemp} °C\n")
        builder.append(" - 강수량: ${weather.rainAmount} mm\n")
        builder.append(" - 강수 확률: ${weather.rainProbability} %\n")
        builder.append(" - 하늘 상태: ${weather.skyCondition}\n")
        builder.append(" - 습도: ${weather.humid} %\n")
        builder.append(" - 풍속: ${weather.windSpeed} m/s\n")
        return builder.toString()
    }

    // 추천 의상 정보를 출력하는 함수
    private fun formatClothingInfo(clothingSet: ClothingSet): String {
        val builder = StringBuilder()
        builder.append("의상 Set ID: ${clothingSet.id}\n")
        clothingSet.recommendedClothings.forEachIndexed { index, clothing ->
            builder.append("옷${index + 1}: ${clothing.name} (${clothing.type})\n")
        }
        return builder.toString()
    }
}
